package com.spike.sparkstreaming.loader

import com.spike.sparkstreaming.config.{SparkSessionConfig, StreamingLoaderConfig}
import com.spike.sparkstreaming.loader.JsonMsgSecurityDataLoader.{sparkSession, streamingLoaderConfig}
import org.apache.spark.sql.functions.col

object AvroMsgSecurityDataLoader extends App with Serializable{

  implicit val sparkSession = SparkSessionConfig("JsonMsgSecurityDataLoader", true).getSparkSession

  val configFilePath = "src/main/resources/application.conf"
  val streamingLoaderConfig = StreamingLoaderConfig(configFilePath)

  /*val initDf = sparkSession
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", streamingLoaderConfig.bootstrapServers)
    .option("subscribe", streamingLoaderConfig.inputDataTopicName)
    .load()
    .select(col("value").cast("string"))


  initDf
    .writeStream
    .outputMode("update")
    .format("console")
    .start()
    .awaitTermination()*/

}
