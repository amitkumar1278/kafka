# KAFKA Operations

# START THE KAFKA ENVIRONMENT
==================================================================
NOTE: Your local environment must have Java 8+ installed.

Run the following commands in order to start all services in the correct order:

# Start the ZooKeeper service
# Note: Soon, ZooKeeper will no longer be required by Apache Kafka.
		$ bin/zookeeper-server-start.sh config/zookeeper.properties
Open another terminal session and run:

# Start the Kafka broker service
		bin/kafka-server-start.sh config/server.properties

Once all services have successfully launched, you will have a basic Kafka environment running and ready to use.

# CREATE A TOPIC TO STORE YOUR EVENTS
==================================================================

Kafka is a distributed event streaming platform that lets you read, write, store, and process events (also called records or messages in the documentation) across many machines.

Example events are payment transactions, geolocation updates from mobile phones, shipping orders, sensor measurements from IoT devices or medical equipment, and much more. These events are organized and stored in topics. Very simplified, a topic is similar to a folder in a filesystem, and the events are the files in that folder.

So before you can write your first events, you must create a topic. Open another terminal session and run:

$ kafka-topics.sh

## create topics

=> command to understand some parameter
		$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create		=> error 'Missing required argument "[partitions]"'
		$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3		=> error:  Missing required argument "[replication-factor]"
		$ $ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 2		error: InvalidReplicationFactorException: Replication factor: 2 larger than available brokers: 1. 

=> create topics:
		$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --create --partitions 3 --replication-factor 1			=> Replication factor can't be greater than number of brokers

		$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic second_topic --create --partitions 6 --replication-factor 1
		
		$ bin/kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092

=> 	All of Kafka's command line tools have additional options: run the kafka-topics.sh command without any arguments to display usage information. For example, it can also show you details such as the partition count of the new topic:

=> to get list of existing topics:
		$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --list

 => topic details:
		$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic first_topic --describe
		$ bin/kafka-topics.sh --describe --topic quickstart-events --bootstrap-server localhost:9092


=> topic delete: 
		$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic second_topic --delete
		Topic second_topic is marked for deletion.
		

Topic:quickstart-events  PartitionCount:1    ReplicationFactor:1 Configs:
Topic: quickstart-events Partition: 0    Leader: 0   Replicas: 0 Isr: 0

===============================================================================

		

		
===============================================================================
# Kafka Console producer ClI	
