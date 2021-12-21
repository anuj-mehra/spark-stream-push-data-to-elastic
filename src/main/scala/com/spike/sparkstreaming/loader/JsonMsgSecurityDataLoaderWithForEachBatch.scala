package com.spike.sparkstreaming.loader

import com.spike.sparkstreaming.config.{SparkSessionConfig, StreamingLoaderConfig}
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

  val query = initDf
    .writeStream
    .trigger(Trigger.ProcessingTime("5 seconds"))
    .foreachBatch((incomingdata: DataFrame, batchId: Long) => {
      incomingdata.persist(StorageLevel.MEMORY_ONLY)

      incomingdata.show(false)

      incomingdata.foreach((row) => {
        val data = row.getAs[String]("value")

        data.contains("equity") match {
          case true =>
            println("This is Equity data ---> " + data)
          case false =>
            data.contains("corp") match {
              case true =>
                println("This is CORP data ---> " + data)
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
