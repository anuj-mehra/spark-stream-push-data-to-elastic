package com.spike.sparkstreaming.hdfs

import com.spike.sparkstreaming.hdfs.HDFSConfig._
import org.apache.hadoop.fs.{FileSystem, LocalFileSystem}
import org.apache.hadoop.hdfs.DistributedFileSystem

class HDFSConfig {

  def getHadoopConf: org.apache.hadoop.conf.Configuration = {
    conf
  }

  def getHadoopFileSystemHandle: FileSystem = {
    hdfs
  }

}

object HDFSConfig{

  val conf:org.apache.hadoop.conf.Configuration = new org.apache.hadoop.conf.Configuration
  conf.setBoolean("", true)
  conf.set("fs.hdfs.impl",classOf[DistributedFileSystem].getName)
  conf.set("fs.file.impl", classOf[LocalFileSystem].getName)

  val hdfs:FileSystem = FileSystem.newInstance(conf)
}
