package com.spike.sparkstreaming.loader

import com.spike.sparkstreaming.config.{SparkSessionConfig, StreamingLoaderConfig}
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.streaming.Trigger

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


  initDf
    .writeStream
    .trigger(Trigger.ProcessingTime("5 seconds"))
    /*.foreachBatch { (batchDF: DataFrame, batchId: Long) =>
      batchDF.unpersist()
      println("batchId is -->" + batchId)
      batchDF.show(false)
      batchDF.unpersist()
    }*/
    /*.foreachBatch((batchDF, batchId) => {
      batchDF.unpersist()
      println("batchId is -->" + batchId)
      batchDF.show(false)
      batchDF.unpersist()
    })*/
    .outputMode("update")
    .option("checkpointLocation", "/Users/anujmehra/git/spark-stream-push-data-to-elastic/src/main/resources/checkpoint-location/")
    .start()
    .awaitTermination()

}
