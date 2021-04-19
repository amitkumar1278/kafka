# KAFKA Operations

-------------------------------------------------------------------------------		

## START THE KAFKA ENVIRONMENT

NOTE: Your local environment must have Java 8+ installed.

Run the following commands in order to start all services in the correct order:

**Start the ZooKeeper service**
**Note:** Soon, ZooKeeper will no longer be required by Apache Kafka.

		$ bin/zookeeper-server-start.sh config/zookeeper.properties
		
Open another terminal session and run:

**Start the Kafka broker service**

		$ bin/kafka-server-start.sh config/server.properties

Once all services have successfully launched, you will have a basic Kafka environment running and ready to use.

-------------------------------------------------------------------------------		

## CREATE A TOPIC TO STORE YOUR EVENTS

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


-------------------------------------------------------------------------------		


## Kafka Console producer ClI	


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
	

getting warning in below, because topic was not created.
	
	$ kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic new_topic
		>hey this topic does not exist
		[2021-04-18 19:07:34,618] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 3 : {new_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
		[2021-04-18 19:07:34,724] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 4 : {new_topic=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
	
	
	
for second topic no warning, because it was created by Kafka with default configuration after first message was produced. but earlier no leader was elected, so we got warning.
		
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


-------------------------------------------------------------------------------	


## Kafka Console Consumer ClI	



	$ kafka-console-consumer.sh		=> documentation
	
if we run below command we may get nothing, even though we wrote few messages.<br> it does not read all the topic it only read from that point you launch it	and it intercept only new messages.

	$ kafka-console-consumer.sh  --bootstrap-server 127.0.0.1:9092 --topic first_topic
	
to understand this properly, run below producer command in one terminal:

	$ kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic
	

run below consumer command in another terminal:

	$ kafka-console-consumer.sh  --bootstrap-server 127.0.0.1:9092 --topic first_topic
	
now whatever message you will write in producer will be consumed by consumer, at this point of time Kafka server and Zookeeper should run.

Terminal 1:

	$ kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic
		>hi how are you
		>
			
Terminal 2:

	$ kafka-console-consumer.sh  --bootstrap-server 127.0.0.1:9092 --topic first_topic
		hi how are you


### to read all the message in the topic run below command:

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

here we asked console consumer to the beginning of the topic and tell us what there is.<br>here you can see, the order of the messages in this consumer is not total. the order is per partition. because "first_topic" was created with 3 partitions, so the order is only guranted at partiotion level. <br>if you try a topic with l partition, you will see total ordering.





-------------------------------------------------------------------------------	

## Kafka Consumers in Group	


	$ kafka-console-consumer.sh

start producer in one terminal:

	$ kafka-console-producer.sh --brr-list 127.0.0.1:9092 --topic first_topic

start group consumer in another terminal;<br> by default no messages will come but when we will write a message in producer and produce, apparently it will appear in consumer.

	$ kafka-console-consumer.sh  --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-firstapplication


-------------------------------------------------------------------------------	

**Example 1:**
### lets try with one Producer and three consumer of same topic and group.

**Producer 1:**

	$ kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic
		>hello
		>how r u
		>where r u
		>red
		>blue
		>green
		>orange
		>yellow
		>aqua
		>maroon
		>


**Consumer 1:**

	$ kafka-console-consumer.sh  --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-firstapplication
		hello
		how r u
		where r u
		blue
		yellow

	

**Consumer 2:**

	$ kafka-console-consumer.sh  --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-firstapplication
		orange
		maroon

	
**Consumer 3:**

	$ kafka-console-consumer.sh  --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-firstapplication
		red
		green
		aqua


in above example you can see that we have 1 producer and 3 consumer. <br> when there is only one consumer, all messages were consumed by same. <br> when we have 3 active consumer of same group. since we have 3 partition group so messages are split between consumers, that means each consumer can read from 3 partition.

<br>we didn't did any special configuration for this load balancing of messages, consumer group itself rebalance and share the load. 
<br>if 1 consumer will be down then message will be shared between 2 active consumer.

-------------------------------------------------------------------------------	

**Example 2:**

execute below command **1st** time

	$ kafka-console-consumer.sh  --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-second-application --from-beginning
		hello amit
		fun learning!
		play 123
		how 
		orange
		hello
		where r u
		orange
		maroon
		learning kafka
		just for fun
		play
		test 123
		bue
		how r u
		red
		green
		aqua
		awesome kafka
		just message
		some message that is acked
		hi how are you
		hello new consumer
		test 1
		red
		green
		blue
		yellow


execute same above command **2nd** time and we received 0 messages, even though we mention "--from-beginning". 

because we specified the group so the offset has been committed in kafka.<br> So it says, "my-second-application" group has read all the message till message number "N" so now it gonna read only new messages from message number 29.<br> so "--from-beginning" is not taking in account any more.


<br>so we can remove ***"--from-beginning"*** and it work as ususal and read all new messages.

	$ kafka-console-consumer.sh  --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-second-application


So if we stop the consumer application and keep producing messages and later relaunch the consumer application with the same application, we can see it only read the message which we just produced.


	$ kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic
		>hello
		>how r u
		>where r u
		>red
		>blue
		>green
		>orange
		>yellow
		>aqua
		>maroon
		>rekjhgkjfgk
		>abcd
		>efgh
		>ijkl
		>

relaunched the kafka consumer with group name after message is produced, and it looks like:

	$ kafka-console-consumer.sh  --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-second-application --from-beginning
		abcd
		ijkl
		efgh

-------------------------------------------------------------------------------	

	
## Kafka Consumer Group ClI	

List all consumer groups, describe a consumer group, delete consumer group info, or reset consumer group offsets.

	$ kafka-consumer-groups.sh

	$ kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
		my-firstapplication
		my-second-application

When no consumer is working:

group my-second-application

	$ kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group my-second-application
	
		Consumer group 'my-second-application' has no active members.
		
		GROUP                 TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
		my-second-application first_topic     0          10              10              0               -               -               -
		my-second-application first_topic     1          11              11              0               -               -               -
		my-second-application first_topic     2          11              11              0               -               -               -


group my-firstapplication

	$ kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group my-firstapplication
	
		Consumer group 'my-firstapplication' has no active members.
		
		GROUP               TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID     HOST            CLIENT-ID
		my-firstapplication first_topic     0          10              10              0               -               -               -
		my-firstapplication first_topic     1          11              11              0               -               -               -
		my-firstapplication first_topic     2          11              11              0               -               -   

above we can see each group is reading from all available partition and maintaining there offset, they also maintain group is "lagging" how many message on each group's partition.



When there is active consumer with group:

	$ kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group my-second-application
	
		GROUP                 TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                                           HOST            CLIENT-ID
		my-second-application first_topic     0          10              10              0               consumer-my-second-application-1-629abf8c-fe3d-4f69-9551-a0e060238ad4 /127.0.0.1      consumer-my-second-application-1
		my-second-application first_topic     1          11              11              0               consumer-my-second-application-1-629abf8c-fe3d-4f69-9551-a0e060238ad4 /127.0.0.1      consumer-my-second-application-1
		my-second-application first_topic     2          11              11              0               consumer-my-second-application-1-629abf8c-fe3d-4f69-9551-a0e060238ad4 /127.0.0.1      consumer-my-second-application-1


-------------------------------------------------------------------------------	

## Resetting Offsets	

	$ kafka-consumer-groups.sh

**to-earliest**
	
	$ kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --group my-firstapplication --reset-offsets --to-earliest
		WARN: No action will be performed as the --execute option is missing.In a future major release, the default behavior of this command will be to prompt the user before executing the reset rather than doing a dry run. You should add the --dry-run option explicitly if you are scripting this command and want to keep the current default behavior without prompting.
	One of the reset scopes should be defined: --all-topics, --topic.



	$ kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --group my-firstapplication --reset-offsets --to-earliest --execute
		One of the reset scopes should be defined: --all-topics, --topic.

	
	$ kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --group my-firstapplication --reset-offsets --to-earliest --execute --topic first_topic

		GROUP                          TOPIC                          PARTITION  NEW-OFFSET     
		my-firstapplication            first_topic                    0          0              
		my-firstapplication            first_topic                    1          0              
		my-firstapplication            first_topic                    2          0   
	

Now, if we restart our consumer, we will see all the data again:	
	
	$ kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-firstapplication
		hello amit
		fun learning!
		play 123
		how 
		orange
		hello
		where r u
		orange
		maroon
		abcd
		learning kafka
		just for fun
		play
		test 123
		bue
		how r u
		red
		green
		aqua
		rekjhgkjfgk
		ijkl
		awesome kafka
		just message
		some message that is acked
		hi how are you
		hello new consumer
		test 1
		red
		green
		blue
		yellow
		efgh
			

After starting consumer with group we can see like below, lagging will be 0.

	$ kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --describe --group my-firstapplication 
	
		GROUP               TOPIC           PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                                         HOST            CLIENT-ID
		my-firstapplication first_topic     0          10              10              0               consumer-my-firstapplication-1-f2942403-be0a-4e1d-a507-d5976ebe9b4d /127.0.0.1      consumer-my-firstapplication-1
		my-firstapplication first_topic     1          11              11              0               consumer-my-firstapplication-1-f2942403-be0a-4e1d-a507-d5976ebe9b4d /127.0.0.1      consumer-my-firstapplication-1
		my-firstapplication first_topic     2          11              11              0               consumer-my-firstapplication-1-f2942403-be0a-4e1d-a507-d5976ebe9b4d /127.0.0.1      consumer-my-firstapplication-1



### to get another option of reset lets play with the documentation:

	$ kafka-consumer-groups.sh	


**shift-by 2**

	
	$ kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --group my-firstapplication --reset-offsets --shift-by 2 --execute --topic first_topic
		[2021-04-20 00:32:57,537] WARN New offset (12) is higher than latest offset for topic partition first_topic-0. Value will be set to 10 (kafka.admin.ConsumerGroupCommand$)
		[2021-04-20 00:32:57,538] WARN New offset (13) is higher than latest offset for topic partition first_topic-1. Value will be set to 11 (kafka.admin.ConsumerGroupCommand$)
		[2021-04-20 00:32:57,538] WARN New offset (13) is higher than latest offset for topic partition first_topic-2. Value will be set to 11 (kafka.admin.ConsumerGroupCommand$)
		
		GROUP                          TOPIC                          PARTITION  NEW-OFFSET     
		my-firstapplication            first_topic                    0          10             
		my-firstapplication            first_topic                    1          11             
		my-firstapplication            first_topic                    2          11  	
**shift-by -2**	

	$ kafka-consumer-groups.sh --bootstrap-server 127.0.0.1:9092 --group my-firstapplication --reset-offsets --shift-by -2 --execute --topic first_topic
	
		GROUP                          TOPIC                          PARTITION  NEW-OFFSET     
		my-firstapplication            first_topic                    0          8              
		my-firstapplication            first_topic                    1          9              
		my-firstapplication            first_topic                    2          9 

we can shift forward or backward kafka console consumer group offset.

if we start consumer like below in another terminal, we should able to see 6 messages. so it did shift 2 offset on each partition.
	
	$ kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --group my-firstapplication
		maroon
		abcd
		rekjhgkjfgk
		ijkl
		yellow
		efgh
	
	
	
-------------------------------------------------------------------------------	

	
## Good to know		
	
- **producer-consumer with keys**: The CLI has many options, but here are the other that are most commonly used:
	
**Producer with keys**

	$ kafka-console-producer.sh --broker-list 127.0.0.1:9092 --topic first_topic --property parse.key=true --property key.separator=,
		>key,value
		>another key,another value
		>just key,
		>,just value
		>

**Consumer with keys**

	$ kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic first_topic --from-beginning --property print.key=true --property key.separator=,
		null,hello amit
		null,fun learning!
		null,play 123
		null,how 
		null,orange
		null,hello
		null,where r u
		null,orange
		null,maroon
		null,abcd
		another key,another value
		null,learning kafka
		null,just for fun
		null,play
		null,test 123
		null,bue
		null,how r u
		null,red
		null,green
		null,aqua
		null,rekjhgkjfgk
		null,ijkl
		key,value
		null,awesome kafka
		null,just message
		null,some message that is acked
		null,hi how are you
		null,hello new consumer
		null,test 1
		null,red
		null,green
		null,blue
		null,yellow
		null,efgh
		just key,
		,just value
	
-------------------------------------------------------------------------------	



	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	