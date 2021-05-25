# We are going to use Elastic Search to consume all data produced by Kafka Producer using Kafka Consumer.

# to do, so we need to set up Elastic Search, basically two ways can be followed:

1. Follow the installation steps mentioned in elastic.co sites:
   https://www.elastic.co/guide/en/elasticsearch/reference/current/install-elasticsearch.html
   
2. There is another simple way, which give fully managed Elasticsearch, at any scale deployed in your account:
   https://bonsai.io/
   
we can follow either of elastic search setup approach but "bonsai" is easy to use because it is hosted elastic search.
we can get here free node cluster, you don't have to set up it's perfectly setup and endpoints are available right away, and it's online.
it's like a real world setup because we have something like an Elasticsearch somewhere on the machine on the web to basically use.
to use "Bonsai" we have to follow below steps:
1.  signup by providing personal details, Describe Project Details, 
2.  provide cluster name like "KAFKA_COURSE"
3.  Confirm Email
4.  Cluster will be ready to use,so select Cluster
5.  Select created Cluster => select "Credentials" under "Access" tab
6.  here we have below thing which is useful for accessing/connecting, it will be like:
      Full Access URL:  https://7d5hbnex93:usfypm4gd0@kafka-course-8513360256.ap-southeast-2.bonsaisearch.net:4433
      Access Key: 7d5hbnex933
      Access Secret: usfypm4gd03
    
  ----------------------------------------------------------------------  

## before going to code to see how Elasticsearch will work, lets check some feature available at "Bonsai".

### 1. Interactive Console => Select "Console" under "DATA" tab

This allows us to run Rest API call, if you want to run in own tools then we can use "POSTMAN".

but we will use here "Bonsai", So in Elastic Search we can run query and it will give output.

for example, in Bonsai requested URL is "GET" => "/" then Output

   {
      "name": "ip-172-31-33-207",
      "cluster_name": "elasticsearch",
      "cluster_uuid": "9BGrDS6GRKWNsehNB1Ko7w",
      "version": {
         "number": "7.10.2",
         "build_flavor": "oss",
         "build_type": "tar",
         "build_hash": "747e1cc71def077253878a59143c1f785afa92b9",
         "build_date": "2021-01-13T00:42:12.435326Z",
         "build_snapshot": false,
         "lucene_version": "8.7.0",
         "minimum_wire_compatibility_version": "6.8.0",
         "minimum_index_compatibility_version": "6.0.0-beta1"
      },
      "tagline": "You Know, for Search"
   }

So in Elasticsearch we can run query, and it will give information like above.

## Example tutorial to get started

   Cluster Health:    https://www.elastic.co/guide/en/elasticsearch/reference/7.12/cluster-health.html

you can follow above link found different url/query to play with, ex:

1. Query: GET _cluster/health
Query Response:
   
   {
      "cluster_name": "elasticsearch",
      "status": "green",
      "timed_out": false,
      "number_of_nodes": 3,
      "number_of_data_nodes": 3,
      "active_primary_shards": 0,
      "active_shards": 0,
      "relocating_shards": 0,
      "initializing_shards": 0,
      "unassigned_shards": 0,
      "delayed_unassigned_shards": 0,
      "number_of_pending_tasks": 0,
      "number_of_in_flight_fetch": 0,
      "task_max_waiting_in_queue_millis": 0,
      "active_shards_percent_as_number": 100
   }

2. Query: GET /_cluster/health?wait_for_status=yellow&timeout=50s
   Query Response:

   {
      "cluster_name": "elasticsearch",
      "status": "green",
      "timed_out": false,
      "number_of_nodes": 3,
      "number_of_data_nodes": 3,
      "active_primary_shards": 0,
      "active_shards": 0,
      "relocating_shards": 0,
      "initializing_shards": 0,
      "unassigned_shards": 0,
      "delayed_unassigned_shards": 0,
      "number_of_pending_tasks": 0,
      "number_of_in_flight_fetch": 0,
      "task_max_waiting_in_queue_millis": 0,
      "active_shards_percent_as_number": 100
   }

2. Query: GET /_cat/health?v
   Query Response:
   epoch      timestamp cluster       status node.total node.data shards pri relo init unassign pending_tasks max_task_wait_time active_shards_percent
   1621939066 10:37:46  elasticsearch green           3         3      0   0    0    0        0             0                  -                100.0%


2. Query: GET /_cat/nodes?v
   Query Response:
   ip            heap.percent ram.percent cpu load_1m load_5m load_15m node.role master name
   ip            heap.percent ram.percent cpu load_1m load_5m load_15m node.role master name
   172.31.28.236                                                       dimr      -      ip-172-31-28-236
   172.31.33.207                                                       dimr      *      ip-172-31-33-207
   172.31.12.38                                                        dimr      -      ip-172-31-12-38
   

2. Query: GET /_cat/indices?v
   Query Response:
   health status index uuid pri rep docs.count docs.deleted store.size pri.store.size

Note: Indices is a place where we exactly we store data. so this is new elasticsearch cluster and we have not created any index so if we list indices so we received system indices only not user indices, like above.
So we will create an index called "twitter" so this is where we will store our Twitter data.


2. Query: GET /_cluster/health?wait_for_status=yellow&timeout=50s
   Query Response:
   
## Create an Index

we can follow below page to create an Index:

   https://www.elastic.co/guide/en/elasticsearch/reference/7.12/indices-create-index.html

Query:  PUT /twitter
Query Response: 200

   {
      "acknowledged": true,
      "shards_acknowledged": true,
      "index": "twitter"
   }
   
if run above query again, then we will get 400 because index already exists:

   {
      "error": {
         "root_cause": [
            {
            "type": "resource_already_exists_exception",
            "reason": "index [twitter/MrJPpArsQQCZjWPdEJfqaQ] already exists",
            "index_uuid": "MrJPpArsQQCZjWPdEJfqaQ",
            "index": "twitter"
            }
         ],
         "type": "resource_already_exists_exception",
         "reason": "index [twitter/MrJPpArsQQCZjWPdEJfqaQ] already exists",
         "index_uuid": "MrJPpArsQQCZjWPdEJfqaQ",
         "index": "twitter"
      },
      "status": 400
   }
   
check indices: Query: GET /_cat/indices?v

   health status index                     uuid                   pri rep docs.count docs.deleted store.size pri.store.size
   green  open   twitter MrJPpArsQQCZjWPdEJfqaQ   1   1          0            0       416b           208b


## Insert data to an Index

   url:  https://www.elastic.co/guide/en/elasticsearch/reference/7.9/getting-started-index.html

There are a variety of ingest options for Elasticsearch, but in the end they all do the same thing: put JSON documents into an Elasticsearch index.
You can do this directly with a simple PUT request that specifies the index you want to add the document, a unique document ID, and one or more "field": "value" pairs in the request body:

Query:   
PUT /{index}/{index type}/{id}
PUT /twitter/tweets/1

Query input:
   {
      "course": "kafka for Beginners",
      "instructor": "Amit Kumar",
      "module": "Elasticsearch"
   }

Query Response:


   {
      "_index": "twitter",
      "_type": "tweets",
      "_id": "1",
      "_version": 1,
      "result": "created",
      "_shards": {
         "total": 2,
         "successful": 2,
         "failed": 0
      },
      "_seq_no": 0,
      "_primary_term": 1
   }


if we run again the same query, response will be like:

   {
      "_index": "twitter",
      "_type": "tweets",
      "_id": "1",
      "_version": 2,
      "result": "updated",
      "_shards": {
         "total": 2,
         "successful": 2,
         "failed": 0
      },
      "_seq_no": 1,
      "_primary_term": 1
   }


### if you want to see the content:

GET /twitter/tweets/1

Query Response:

{
   "_index": "twitter",
   "_type": "tweets",
   "_id": "1",
   "_version": 2,
   "_seq_no": 1,
   "_primary_term": 1,
   "found": true,
   "_source": {
      "course": "kafka for Beginners",
      "instructor": "Amit Kumar",
      "module": "Elasticsearch"
   }
}


### delete document:

DELETE /twitter/tweets/1

Query Response:

   {
      "_index": "twitter",
      "_type": "tweets",
      "_id": "1",
      "_version": 3,
      "result": "deleted",
      "_shards": {
      "total": 2,
      "successful": 2,
      "failed": 0
      },
      "_seq_no": 2,
      "_primary_term": 1
   }


### delete index

DELETE /twitter

Query Response:

   {
      "acknowledged": true
   }



