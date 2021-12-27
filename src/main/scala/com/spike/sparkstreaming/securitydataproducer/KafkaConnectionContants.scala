package com.spike.sparkstreaming.securitydataproducer

object KafkaConnectionContants extends Enumeration {

  type Main = Value

  // Assigning values
  val BootstrapServers = Value("bootstrap.servers").toString
  val KeySerializer = Value("key.serializer").toString
  val ValueSerializer = Value("value.serializer").toString
  val Acknowledgement = Value("acks").toString
  val BufferMemory = Value("buffer.memory").toString
  val BatchSize = Value("batch.size").toString
  val LingerMs = Value("linger.ms").toString
}
