package com.spike.sparkstreaming.hdfs

import org.apache.hadoop.fs._

class HDFSFileReader(hdfsConfig: HDFSConfig) extends Serializable {

  val checkFileExistsForStreaming: (String) => Boolean = (fileFullyQualifiedUri:String) => {
    val fs: FileSystem = hdfsConfig.getHadoopFileSystemHandle
    fs.exists(new Path(fileFullyQualifiedUri))
  }

  val checkFileExists: (String) => Boolean = (fileFullyQualifiedUri:String) => {
    var fs: FileSystem = hdfsConfig.getHadoopFileSystemHandle

    val fileExists: Boolean = try{
      fs.exists(new Path(fileFullyQualifiedUri))
    }finally{
      fs.close()
      fs = null
    }

    fileExists match {
      case true =>
        println("----File Exists: True")
      case false =>
        println("----File Exists: false")
    }

    fileExists
  }

  def closeFileSystem: Unit = {
    var fs: FileSystem = null

    try{
      fs = hdfsConfig.getHadoopFileSystemHandle
    }finally{
      fs.close()
      fs = null
    }
  }

  def deleteFile(fileFullyQualifiedUri: String, recursiveDelete: Boolean = false): Unit = {
    var fs: FileSystem = hdfsConfig.getHadoopFileSystemHandle
    try{
      val filePath = new Path(fileFullyQualifiedUri)
      fs.delete(filePath, recursiveDelete)
    }finally{
      fs.close()
      fs=null
    }
  }


  def readHdfsFileContent(fileFullyQualifiedUri: String): List[String] = {
    var fileSystem: FileSystem = null
    var stream: FSDataInputStream = null
    try{
      val filePath = new Path(fileFullyQualifiedUri)
      fileSystem = hdfsConfig.getHadoopFileSystemHandle
      stream = fileSystem.open(filePath)

      def readLines: Seq[String] = Stream.cons(stream.readLong(), Stream.continually((stream.readLong())))

      val contents = new java.util.ArrayList[String]
      readLines.takeWhile(_!=null).foreach(line => {
        contents.add(line)
      })
      import scala.collection.JavaConversions._
      contents.toList
    }finally{
      stream.close()
      stream = null
      fileSystem.close()
      fileSystem =null
    }
  }
}

object HDFSFileReader {

  def apply: HDFSFileReader = {
    val hdfsFileReader: HDFSFileReader = new HDFSFileReader(new HDFSConfig)
    hdfsFileReader
  }
}
