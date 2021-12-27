package com.spike.sparkstreaming.loader

import com.spike.sparkstreaming.config.{SparkSessionConfig, StreamingLoaderConfig}
import com.spike.sparkstreaming.http.{HTTPGetClient, HTTPPostClient, HTTPPutClient}
import com.spike.sparkstreaming.securitydataproducer.{SecurityData, SecurityDataJSONConverter}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.storage.StorageLevel

object JsonMsgSecurityDataLoaderWithForEachBatch extends App with Serializable{

  implicit val sparkSession = SparkSessionConfig("JsonMsgSecurityDataLoaderWithForEachBatch", true).getSparkSession

  val configFilePath = "src/main/resources/application.conf"
  val streamingLoaderConfig = StreamingLoaderConfig(configFilePath)

  import org.apache.spark.sql.functions.col

  val initDf = sparkSession
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", streamingLoaderConfig.bootstrapServers)
    .option("subscribe", streamingLoaderConfig.jsonInputDataTopicName)
    .option("startingOffsets", "latest")
    .load()
    .select(col("key").cast("string"), col("value").cast("string"))

  val httpPutClient = new HTTPPutClient

  val query = initDf
    .writeStream
    .trigger(Trigger.ProcessingTime("5 seconds"))
    .foreachBatch((incomingdata: DataFrame, batchId: Long) => {

      val numPartitions = incomingdata.rdd.getNumPartitions

      println("numPartitions ---> " + numPartitions)

      incomingdata.persist(StorageLevel.MEMORY_ONLY)

      incomingdata.show(false)

      incomingdata.foreach((row) => {
        val json = row.getAs[String]("value")
        val securityData: SecurityData = SecurityDataJSONConverter.convertFromJson(json)

        securityData.securityType.equals("equity") match {
          case true =>
            val uri = s"http://localhost:9200/equity/_doc/${securityData.id}"
            println("uri--->" + uri)
            val putResponse = httpPutClient.put(uri, json)
            println(putResponse)
          case false =>
            securityData.securityType.equals("corp") match {
              case true =>
                val uri = s"http://localhost:9200/corp/_doc/${securityData.id}"
                println("uri--->" + uri)
                val putResponse = httpPutClient.put(uri, json)
                println(putResponse)
              case false =>
            }
        }
      })
      println("batchId is -->" + batchId)
    })
    .outputMode(OutputMode.Update())
    .option("checkpointLocation", "/Users/anujmehra/git/spark-stream-push-data-to-elastic/src/main/resources/checkpoint-location-3/")
    .start()

  query.awaitTermination()
}
