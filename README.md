# spark-stream-push-data-to-elastic

## Start confluent cluster;
confluent local services start

## Stop confluent cluster;
confluent local services start

## Create Topic in Confluent Kafka
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic securitydata

## Start Confluent Kafka Producer on shell
kafka-console-producer --topic securitydata --broker-list localhost:9092

## Important topics;
1. Checkpointing
2. Window Operations


## 'securitydata' Topic Schema
{
"id": "string",
"cusip": "string",
"isin": "string",
"description": "string",
"currency": "string"
}
