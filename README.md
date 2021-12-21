# spark-stream-push-data-to-elastic
Structured Streaming provides fast, scalable, fault-tolerant, end-to-end exactly-once stream processing without the user having to reason about streaming.
The system ensures end-to-end exactly-once fault-tolerance guarantees through checkpointing and Write-Ahead Logs

This project has multiple loaders which caters to following usecases;
1. Reading data from Kafka and output the data to console
2. Reading data from Kafka and output the data to single kafka topic
3. Reading data from Kafka and output the data to single kafka topic and also using checkpoint location
4. Reading data from Kafka and output the data to multiple kafka topics (depending on condition) 
   and also using checkpoint location.
5. Using foreachbatch in the writeStream.   


## Start confluent cluster;
confluent local services start

## Stop confluent cluster;
confluent local services start

## Create Topic in Confluent Kafka
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic securitydata
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic equitydata
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic corpdata
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic govtdata
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic munidata

## Start Confluent Kafka Producer on shell
kafka-console-producer --topic securitydata --broker-list localhost:9092

## Start Confluent Kafka Consumer on shell
kafka-console-consumer --topic equitydata  --bootstrap-server localhost:9092 --from-beginning
kafka-console-consumer --topic corpdata  --bootstrap-server localhost:9092 --from-beginning
kafka-console-consumer --topic govtdata  --bootstrap-server localhost:9092 --from-beginning
kafka-console-consumer --topic munidata  --bootstrap-server localhost:9092 --from-beginning

## Important topics;
1. Checkpointing
2. Window Operations
3. Watermark policy


## 'securitydata' Topic Schema
{
"id": "string",
"cusip": "string",
"isin": "string",
"description": "string",
"currency": "string"
}
