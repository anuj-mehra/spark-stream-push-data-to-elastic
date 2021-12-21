package com.spike.sparkstreaming.loader

import com.spike.sparkstreaming.config.{SparkSessionConfig, StreamingLoaderConfig}
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.functions._

object JsonMsgSecurityDataToKafkaLoader extends App with Serializable{

  implicit val sparkSession = SparkSessionConfig("JsonMsgSecurityDataToKafkaLoader", true).getSparkSession

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

  val df2 = initDf.withColumn("key", lit("key"))

 // Pushing data to Equity topic --> equitydata
  df2
    .writeStream
    .trigger(Trigger.ProcessingTime("5 seconds"))
    .outputMode("update")
    .format("kafka")
    .option("kafka.bootstrap.servers", streamingLoaderConfig.bootstrapServers)
    .option("topic", streamingLoaderConfig.equityOutputDataTopicName)
    .option("checkpointLocation", "/Users/anujmehra/git/spark-stream-push-data-to-elastic/src/main/resources/checkpoint-location-2/")
    .start()
    .awaitTermination()

}
