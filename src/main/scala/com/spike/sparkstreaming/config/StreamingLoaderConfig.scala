package com.spike.sparkstreaming.config

import java.io.File
import com.typesafe.config.ConfigFactory

class StreamingLoaderConfig (applicationConfFile: String) extends Serializable {

  val config = ConfigFactory.parseFile(new File(applicationConfFile)).resolve

  lazy val bootstrapServers= config.getString("streaming_loader_config.kafka.bootstrap.servers")

  lazy val jsonInputDataTopicName = config.getString("streaming_loader_config.json_message.topic_name")
  lazy val jsonMsgKeySerializer = config.getString("streaming_loader_config.json_message.key_serializer")
  lazy val jsonMsgValueSerializer = config.getString("streaming_loader_config.json_message.value_serializer")

}

object StreamingLoaderConfig {
  def apply(applicationConfFile: String) = new StreamingLoaderConfig(applicationConfFile)
}
