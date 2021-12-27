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

## Curl commands to fetch meta-data;
curl -XGET 'localhost:9200/_cat/indices?v&pretty'

## Command to create Index in ElasticSearch;
curl -XPUT 'localhost:9200/equity?&pretty'

curl -XPUT 'localhost:9200/corp?&pretty'

curl -XPUT 'localhost:9200/govt?&pretty'

curl -XPUT 'localhost:9200/muni?&pretty'

## Command to create Index in ElasticSearch with pre-defined schema;



## Command to add a document to an Index in ElasticSearch;
(a) adding a document to the 'equity' index created (here document id is '1');

curl -XPUT 'localhost:9200/equity/1?pretty' -d'
{
// actual json message to be saved
}
Sample;
curl -X PUT "localhost:9200/muni/_doc/MS99?timeout=5m&pretty" -H 'Content-Type: application/json' -d'
{
"@timestamp": "2099-11-15T13:12:00",
"message": "GET /search HTTP/1.1 200 1070000",
"user": {
"id": "MS99"
}
}
'

### Exmple is as below;
PUT uri---> http://localhost:9200/equity/_doc/MS9

Following source JSON that is saved;

{
"id" : "MS9",
"cusip" : "Cusip is MS9",
"isin" : "Isin is MS9",
"securityType" : "equity",
"value" : 9,
"currency" : "USD"
}

Following gets saved in elastic search; 

{
   "_index" : "equity",
   "_type" : "_doc",
   "_id" : "MS9",
   "_score" : 0.0049140146,
   "_source" : {
      "id" : "MS9",
      "cusip" : "Cusip is MS9",
      "isin" : "Isin is MS9",
      "securityType" : "equity",
      "value" : 9,
      "currency" : "USD"
   }
}

We get following response from ElasticSearch;
{"_index":"equity","_type":"_doc","_id":"MS9","_version":3,"result":"updated","_shards":{"total":2,"successful":1,"failed":0},"_seq_no":302,"_primary_term":2}

## View data inserted into Elastic Search using Kibana;


## How to ensure only few fields in the json are searchable;


## Command to GET a document from an Index in ElasticSearch (using document id);
curl -X GET "localhost:9200/equity/_doc/MS98?pretty"

## Command to SEARCH documents from an Index in ElasticSearch, using a search term;

#### Following command will search 'MS98' anywhere in the document | Exact Keyword Match;
curl -X GET "localhost:9200/equity/_search?q=MS98&pretty"

#### Following command will search 'USD' anywhere in the document | Exact Keyword Match;
curl -X GET "localhost:9200/equity/_search?q=USD&pretty"

#### Following command will search 'USD' in 'currency' columns and will be sorting on column 'value' | Exact Keyword Match;
curl -X GET "localhost:9200/equity/_search?q=currency:USD&sort=value:desc&pretty"

##RequestBody is the preferred way and is used in production.

## Performing Search using the RequestBody instead of GET URL command (This is called as TERM SEARCH);

There are two ways to search values from the index;
1. Term Query
2. Match Query

###Term Query --> Returns documents that contain an exact term in a provided field. 
You can use the term query to find documents based on a precise value such as a price, a product ID, or a username.

The term is a perfect match, that is, an exact query. The search term will not be segmented before the search, 
so our search term must be one of the document segmentation sets. Let’s say we want to find all the documents titled Jesus Verma. 

The term query does not analyze the search term. The term query only searches for the exact term you provide. 
This means the term query may return poor or no results when searching text fields.

###Match Query --> Returns documents that match a provided text, number, date or boolean value. The provided text is analyzed before matching.

The match query is the standard query for performing a full-text search, including options for fuzzy matching.

Avoid using the term query for text fields.

By default, Elasticsearch changes the values of text fields as part of analysis. This can make finding exact matches for text field values difficult.

To search text field values, use the match query instead.

#### Following command will search 'MS98' in the 'isin' column | Exact Keyword Match;
curl -X GET "localhost:9200/equity/_search?q=isin:MS98&pretty"

curl -X GET 'localhost:9200/equity/_search?pretty' -H 'Content-Type: application/json' -d'
{
   "query":{
      "match":{
         "isin":"MS98"
      }
   }
}'

#### Following command will search 'MS98' in the whole document (only given columns) | Exact Keyword Match;
curl -X GET "localhost:9200/equity/_search?q=MS98&pretty"


curl -X GET 'localhost:9200/equity/_search?pretty' -H 'Content-Type: application/json' -d'
{
   "query":{
      "multi_match":{
         "query":"MS98",
         "fields":["isin", "cusip"]
      }
   }
}'

#### Following command will search start with 'MS9' in the 'isin' column;


## Sort searched response data in an order;
### PS: when we do the sorting, the relevance score becomes null, as the relevance score no longer applies


## Pagination in the Search Results;


## Fetch only certain fields in response;



## Algo for relevance score;




## 'securitydata' Topic Schema
{
"id": "string",
"cusip": "string",
"isin": "string",
"description": "string",
"value": "int"
"currency": "string"
}

## Important topics;
1. Checkpointing
2. Window Operations
3. Watermark policy
4. View elastic search data using Kibana



## Helpful links

https://coralogix.com/blog/42-elasticsearch-query-examples-hands-on-tutorial/

https://dzone.com/articles/23-useful-elasticsearch-example-queries




