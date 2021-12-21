package com.spike.sparkstreaming.config

import java.io.File
import com.typesafe.config.ConfigFactory

class StreamingLoaderConfig (applicationConfFile: String) extends Serializable {

  val config = ConfigFactory.parseFile(new File(applicationConfFile)).resolve

  lazy val bootstrapServers= config.getString("streaming_loader_config.kafka.bootstrap.servers")

  lazy val jsonInputDataTopicName = config.getString("streaming_loader_config.json_message.input_topic_name")
  lazy val jsonMsgKeySerializer = config.getString("streaming_loader_config.json_message.key_serializer")
  lazy val jsonMsgValueSerializer = config.getString("streaming_loader_config.json_message.value_serializer")

  lazy val equityOutputDataTopicName = config.getString("streaming_loader_config.json_message.equity_output_topic_name")
  lazy val corpOutputDataTopicName = config.getString("streaming_loader_config.json_message.corp_output_topic_name")
  lazy val govtOutputDataTopicName = config.getString("streaming_loader_config.json_message.govt_output_topic_name")
  lazy val muniOutputDataTopicName = config.getString("streaming_loader_config.json_message.muni_output_topic_name")

}

object StreamingLoaderConfig {
  def apply(applicationConfFile: String) = new StreamingLoaderConfig(applicationConfFile)
}
