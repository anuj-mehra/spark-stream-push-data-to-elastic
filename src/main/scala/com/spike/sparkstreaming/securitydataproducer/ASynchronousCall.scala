package com.spike.sparkstreaming.securitydataproducer

import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import java.io.Serializable
import java.util.Properties

object ASynchronousCall extends App{

  val producerConfig = KafkaProducerConfig("/Users/anujmehra/git/spark-stream-push-data-to-elastic/src/main/resources/producer-application.conf")
  println(producerConfig.bootstrapServers)

  val kafkaProps = new Properties
  kafkaProps.put(KafkaConnectionContants.BootstrapServers, producerConfig.bootstrapServers)
  kafkaProps.put(KafkaConnectionContants.KeySerializer, producerConfig.jsonMsgKeySerializer)
  kafkaProps.put(KafkaConnectionContants.ValueSerializer, producerConfig.jsonMsgValueSerializer)
  kafkaProps.put(KafkaConnectionContants.Acknowledgement,"all")

  //The total bytes of memory the producer can use to buffer records waiting to be sent to the server.
  //Default value ==> 33554432
  kafkaProps.put(KafkaConnectionContants.BufferMemory,"33554432")

  //The producer will attempt to batch records together into fewer requests whenever multiple records are being sent to the same partition.
  //This helps performance on both the client and the server. This configuration controls the default batch size in bytes.
  //No attempt will be made to batch records larger than this size.
  //Requests sent to brokers will contain multiple batches, one for each partition with data available to be sent.
  // Default value == 16384 bytes
  kafkaProps.put(KafkaConnectionContants.BatchSize,"16384")

  //This setting defaults to 0 (i.e. no delay). Setting linger.ms=5,
  // for example, would have the effect of reducing the number of requests sent but would add up to 5ms of latency to records sent in the absence of load.
  kafkaProps.put(KafkaConnectionContants.LingerMs,"10")

  val topicName = producerConfig.jsonMsgTopicName
  val kafkaProducer = new KafkaProducer[String,String](kafkaProps)

  val asyncProducer = new ASynchronousCall
  asyncProducer producerMessages(kafkaProducer, topicName)
}

class ASynchronousCall extends Serializable {

  def producerMessages: (KafkaProducer[String,String], String) => Unit
  = (kafkaProducer: KafkaProducer[String,String], topicName: String) => {

    try{
      val counter = 100

      import SecurityDataJSONConverter._
      for(i <- 0 to counter){
        val key = i.toString
        val securityData = SecurityData(s"MS${i}", s"Cusip is MS${i}", s"Isin is MS${i}", SecurityDataType.EQUITY, i, "USD")
        val value = convertToJson(securityData)

        val message: ProducerRecord[String, String] = new ProducerRecord[String,String](topicName, value)
        kafkaProducer.send(message, new DemoUserCallback(key, value))
      }

    }catch{
      case e: Exception =>
        println("----- exception occured : producer --> " + e.printStackTrace())
    }finally{
      println("----calling flush----")
      kafkaProducer.flush()
      kafkaProducer.close()
    }

  }
}

class DemoUserCallback(key: String, value: String) extends Callback{

  override def onCompletion(recordMetadata: RecordMetadata, e: Exception): Unit = {
    val ex = Option(e)

    ex.isDefined match {
      case true =>
        e.printStackTrace()
        println(s"---not able to send message for key = ${key} and value = ${value}")

      case false =>
        println("offset--->" + recordMetadata.offset())
        println("partition--->" + recordMetadata.partition())
        println("topic--->" + recordMetadata.topic())
    }
  }

}