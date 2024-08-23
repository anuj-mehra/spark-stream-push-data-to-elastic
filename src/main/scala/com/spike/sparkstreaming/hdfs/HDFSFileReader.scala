package com.spike.sparkstreaming.hdfs

import org.apache.hadoop.fs._

class HDFSFileReader(hdfsConfig: HDFSConfig) extends Serializable {

  val checkFileExists: (String) => Boolean = (fileFullyQualifiedUri:String) => {
    val fs: FileSystem = hdfsConfig.getHadoopFileSystemHandle
    fs.exists(new Path(fileFullyQualifiedUri))
  }


  def closeFileSystem: Unit = {


  }


}
