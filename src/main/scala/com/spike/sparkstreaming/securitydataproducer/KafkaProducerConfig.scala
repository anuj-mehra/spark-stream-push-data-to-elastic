package com.spike.sparkstreaming.securitydataproducer

import java.io.File
import com.typesafe.config.ConfigFactory

class KafkaProducerConfig (applicationConfFile: String) extends Serializable {

  lazy val config = ConfigFactory.parseFile(new File(applicationConfFile)).resolve

  lazy val bootstrapServers= config.getString("producer_config.bootstrap_servers")

  lazy val jsonMsgTopicName = config.getString("producer_config.json_message.topic_name")
  lazy val jsonMsgKeySerializer = config.getString("producer_config.json_message.key_serializer")
  lazy val jsonMsgValueSerializer = config.getString("producer_config.json_message.value_serializer")

  lazy val avroMsgTopicName = config.getString("producer_config.avro_message.topic_name")
  lazy val avroMsgKeySerializer = config.getString("producer_config.avro_message.key_serializer")
  lazy val avroMsgValueSerializer = config.getString("producer_config.avro_message.value_serializer")

}

object KafkaProducerConfig {
  def apply(applicationConfFile: String) = new KafkaProducerConfig(applicationConfFile)
}

