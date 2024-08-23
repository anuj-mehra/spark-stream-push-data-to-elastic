package com.spike.sparkstreaming.loader

import com.spike.sparkstreaming.config.{SparkSessionConfig, StreamingLoaderConfig}
import com.spike.sparkstreaming.hdfs.{HDFSConfig, HDFSFileReader}
import org.apache.spark.sql.streaming.{OutputMode, StreamingQuery, Trigger}

import scala.util.control.Breaks.{break, breakable}

object JsonMsgSecurityDataLoaderWithCleanShutdown extends App with Serializable{

  implicit val sparkSession = SparkSessionConfig("JsonMsgSecurityDataLoaderWithCleanShutdown", true).getSparkSession

  val fileFullyQualifiedUri = ""
  val configFilePath = "src/main/resources/application.conf"
  val streamingLoaderConfig = StreamingLoaderConfig(configFilePath)

  import org.apache.spark.sql.functions.col

  this.cleanupPreviousShutdown(fileFullyQualifiedUri)

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
    .outputMode(OutputMode.Update())
    .format("console")
    .option("checkpointLocation", "/Users/anujmehra/git/spark-stream-push-data-to-elastic/src/main/resources/checkpoint-location-4/")
    .start()

  //query.awaitTermination

  val hdfsFileReader = HDFSFileReader.apply
  val gracefulShutDownTimeMs = "1000".toLong

  breakable{
    try{
      while(true){
        println("-----inside while loop-----")
        if(hdfsFileReader.checkFileExistsForStreaming(fileFullyQualifiedUri)){
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

    while(query.isActive){
      val msg = query.status.message

      (query.status.isDataAvailable, query.status.isTriggerActive, msg.equals("Initializing sources")) match {
        case (true, true, true) =>
          println("----query can be terminated---")
          query.stop
        case _ =>
          query.awaitTermination(gracefulShutDownTimeMs)
      }
    }
  }

  private def cleanupPreviousShutdown(gracefulShutdownFile:String): Unit = {
    val hdfsFileReader: HDFSFileReader = HDFSFileReader.apply
    hdfsFileReader.checkFileExistsForStreaming(gracefulShutdownFile) match {
      case true =>
        hdfsFileReader.deleteFile(gracefulShutdownFile, false)
      case false =>
    }
  }

}
