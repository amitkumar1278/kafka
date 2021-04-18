# KAFKA Operations

## START THE KAFKA ENVIRONMENT
-------------------------------------------------------------------------------		

NOTE: Your local environment must have Java 8+ installed.

Run the following commands in order to start all services in the correct order:

**Start the ZooKeeper service**
**Note:** Soon, ZooKeeper will no longer be required by Apache Kafka.

		$ bin/zookeeper-server-start.sh config/zookeeper.properties
		
Open another terminal session and run:

**Start the Kafka broker service**

		$ bin/kafka-server-start.sh config/server.properties

Once all services have successfully launched, you will have a basic Kafka environment running and ready to use.

## CREATE A TOPIC TO STORE YOUR EVENTS
-------------------------------------------------------------------------------		

Kafka is a distributed event streaming platform that lets you read, write, store, and process events (also called records or messages in the documentation) across many machines.

Example events are payment transactions, geolocation updates from mobile phones, shipping orders, sensor measurements from IoT devices or medical equipment, and much more. These events are organized and stored in topics. Very simplified, a topic is similar to a folder in a filesystem, and the events are the files in that folder.

So before you can write your first events, you must create a topic. Open another terminal session and run:

	$ kafka-topics.sh

### create topics

**command to understand some parameter**

	$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create		=> error 'Missing required argument "[partitions]"'
	$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3		=> error:  Missing required argument "[replication-factor]"
	$ $ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 2		error: InvalidReplicationFactorException: Replication factor: 2 larger than available brokers: 1. 

**create topics:**
		
	$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 1			=> Replication factor can't be greater than number of brokers

	$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic second_topic --create --partitions 6 --replication-factor 1
	
	$ bin/kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092

All of Kafka's command line tools have additional options: run the kafka-topics.sh command without any arguments to display usage information. For example, it can also show you details such as the partition count of the new topic:

**to get list of existing topics:**
		
	$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --list

**topic details:**
		
	$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --describe
	$ bin/kafka-topics.sh --describe --topic quickstart-events --bootstrap-server localhost:9092


**topic delete:**
		
	$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic second_topic --delete
		Topic second_topic is marked for deletion.
		Topic:quickstart-events  PartitionCount:1    ReplicationFactor:1 Configs:<br>
		Topic: quickstart-events Partition: 0    Leader: 0   Replicas: 0 Isr: 0

		
<br>
## Kafka Console producer ClI	
-------------------------------------------------------------------------------		

To send data to topic:

	$ kafka-console-producer.sh		=> to access documentation
	$ kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic		=> if its well, you will see a ">" in right hand side, means it's ready to produce message.
		>hello amit
		>awesome kafka
		>learning kafka
		>just message
		>^C
	
	
	$ kafka-console-producer.sh --broker-list 127.0.09092 --topic first_topic --producer-property acks=all
		>some message that is acked
		>just for fun
		>fun learning!
		>^C
	

getting error in below, because topic was not created.
	
	$ $ kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic new_topic
		>hey this topic does not exist
		[2021-04-18 19:07:34,618] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 3 : {new_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
		[2021-04-18 19:07:34,724] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 4 : {new_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
	
	
	
=> for second topic no warning, because it was created by Kafka with default configuration after first topics execution. but earlier no leader was elected, so we got excpetion.
		
	$ kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic new_topic
		>hey this topic does not exist
		[2021-04-18 19:07:34,618] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 3 : {new_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
		[2021-04-18 19:07:34,724] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 4 : {new_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
		>another message
		>^C
	
	
	
	
to confirm new topic created exit producer and run:

	$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --list
		first_topic
		new_topic

this new topic has default configuration which we don't want usually. by default, PartitionCount: 1
**Suggestion**, Always create topic before producing.
	
	$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic new_topic --describe
		Topic: new_topic	PartitionCount: 1	ReplicationFactor: 1	Configs: 
		Topic: new_topic	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
	

Change default Configuration:

	$ nano config/server.properties
		set => num.partitions=3

running with new config:

	$ kafka-console-producer.sh --broker-list 127.0.0.1:2 --topic new_topic3
		>helloWorld
		[2021-04-18 23:30:35,743] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 3 : {new_topic3=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)

	$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --list
		first_topic
		new_topic
		new_topic3


	$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic new_topic3 --describe
		Topic: new_topic3	PartitionCount: 3	ReplicationFactor: 1	Configs: 
		Topic: new_topic3	Partition: 0	Leader: 0	Replicas: 0	Isr: 0
		Topic: new_topic3	Partition: 1	Leader: 0	Replicas: 0	Isr: 0
		Topic: new_topic3	Partition: 2	Leader: 0	Replicas: 0	Isr: 0





## Kafka Console Consumer ClI	
-------------------------------------------------------------------------------	


	$ kafka-console-consumer.sh		=> documentation
	
if we run below command we may get nothing, even though we wrote few messages.<br> it does not read all the topic it only read from that point you launch it	and it only intercept only new messages.

	$ kafka-console-consumer.sh  --bootstrap-server 127.0.0.1:9092 --topic first_topic
	
to understand this properly, 

run below producer command in one terminal:

	$ kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic
	

run below consume command in another terminal:

	$ kafka-console-consumer.sh  --bootstrap-server 127.0.0.1:9092 --topic first_topic
	
now whatever message you will write in producer will be consume by consumer, at this point of time Kafka server and Zookeeper should run.

Terminal 1:

	$ kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic
		>hi how are you
		>
			
Terminal 2:

	$ kafka-console-consumer.sh  --bootstrap-server 127.0.0.1:9092 --topic first_topic
		hi how are you


### how do we read all the message in the topic?
to do so we need to run below command

	$ kafka-console-consumer.sh  --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning
		hello amit
		fun learning!
		play 123
		learning kafka
		just for fun
		play
		awesome kafka
		just message
		some message that is acked
		hi how are you

here we asked console consumer to the beginning of the topic and tell us what there is.
here you can see, the order of the messages in this consumer is not total. the order is per partition. because "first_topic" was created with 3 partitions, so the order is only guranted at partiotion level. 
if you try a topic with l partition, you will see total ordering.







## Kafka Consumers in Group	
-------------------------------------------------------------------------------	





## Kafka Consumer Group ClI	
-------------------------------------------------------------------------------	





## Kafka Console producer ClI	
-------------------------------------------------------------------------------	






	
	

	
	
	
	
	
	
	
