package com.spike.sparkstreaming.loader

import com.spike.sparkstreaming.config.{SparkSessionConfig, StreamingLoaderConfig}
import com.spike.sparkstreaming.hdfs.{HDFSConfig, HDFSFileReader}
import org.apache.spark.sql.streaming.{StreamingQuery, Trigger}

import scala.util.control.Breaks.{break, breakable}

object JsonMsgSecurityDataLoaderWithCleanShutdown extends App with Serializable{

  implicit val sparkSession = SparkSessionConfig("JsonMsgSecurityDataLoaderWithCleanShutdown", true).getSparkSession

  val fileFullyQualifiedUri = ""
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


  val df2 = initDf.withColumnRenamed("value", "input-data").drop("key")

  val query = df2
    .writeStream
    .trigger(Trigger.ProcessingTime("10 seconds"))
    .outputMode("update")
    .format("console")
    .option("checkpointLocation", "/Users/anujmehra/git/spark-stream-push-data-to-elastic/src/main/resources/checkpoint-location-4/")
    .start()

  query.awaitTermination

  val hdfsFileReader = new HDFSFileReader(new HDFSConfig)
  val gracefulShutDownTimeMs = "1000".toLong

  breakable{
    try{
      while(true){
        println("-----inside while loop-----")
        if(hdfsFileReader.checkFileExists(fileFullyQualifiedUri)){
          stopStreamQuery(query, gracefulShutDownTimeMs)
          break;
        }else{
          query.awaitTermination(gracefulShutDownTimeMs)
        }
      }
    }finally{
      hdfsFileReader.closeFileSystem
    }
  }

  private def stopStreamQuery(query: StreamingQuery, gracefulShutDownTimeMs: Long): Unit = {

  }

}
