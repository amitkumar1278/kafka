
# kafka connect confluent 

Confluent has provided wide range of connectors for Kafka for soure(producer) and sink(consumer) both. below is the steps to include confluent connector into your application.

Step 1: Search "kafka connect confluent" in google and follow this URL "https://www.confluent.io/hub/#"
Step 2: in the "https://www.confluent.io/hub/#" search for twitter in search box.
Step 3: Select "Twitter Source Connector", and it will redirect to new page.
Step 4:  find the hyperlink "Source code"
Step 5: Step 4 will redirect to Github like "https://github.com/jcustenborder/kafka-connect-twitter"
Step 6: Go through Readme file and refer the "Configuration" mentioned
Step 7: Go to "Release" tab/link search for the zip file of latest release and download and extract it.
Step 8: Create a new module in project like "Kafka connect => connectors". 
        paste directory "kafka-connect-twitter" which contains jar files to "Connectors" directory.


## by following above steps we copied the connector into our project. 
now we need to run this connector, to do so we need to follow below steps:


Step 1: to run the connector there is a command called "connect-standalone".
to get this open console and type "connect-standalone"

    amit@amit-Lenovo-ideapad-520-15IKB:~/kafka_2.13-2.7.0$ connect-standalone.sh
    USAGE: /home/amit/kafka_2.13-2.7.0/bin/connect-standalone.sh [-daemon] connect-standalone.properties

Step 2: find "connect-standalone.properties"

    amit@amit-Lenovo-ideapad-520-15IKB:~/kafka_2.13-2.7.0$ ls config
    connect-console-sink.properties    connect-file-sink.properties    connect-mirror-maker.properties  log4j.properties     tools-log4j.properties
    connect-console-source.properties  connect-file-source.properties  connect-standalone.properties    producer.properties  trogdor.conf
    connect-distributed.properties     connect-log4j.properties        consumer.properties              server.properties    zookeeper.properties

copy this file in the project next to "Connectors"

Step 3: All property of the file can be shame for now we need to change:
plugin.path=connectors

Step 4: Add property in twitter.properties like below:

        name=TwitterSourceDemo
        tasks.max=1
        connector.class=com.github.jcustenborder.kafka.connect.twitter.TwitterSourceConnector
        
        # Set these required values
        
        process.deletes=false
        filter.keywords=bitcoin
        
        # create this topic from console before running the application; using command:
        #amit@amit-Lenovo-ideapad-520-15IKB:~/kafka_2.13-2.7.0$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --create --topic twitter_status_connect --partitions 3 --replication-factor 1
        #WARNING: Due to limitations in metric names, topics with a period ('.') or underscore ('_') could collide. To avoid issues it is best to use either, but not both.
        #Created topic twitter_status_connect.
        kafka.status.topic=twitter_status_connect
        
        # create this topic from console before running the application;
        #amit@amit-Lenovo-ideapad-520-15IKB:~/kafka_2.13-2.7.0$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --create --topic twitter_deletes_connect --partitions 3 --replication-factor 1
        #WARNING: Due to limitations in metric names, topics with a period ('.') or underscore ('_') could collide. To avoid issues it is best to use either, but not both.
        #Created topic twitter_deletes_connect.
        kafka.delete.topic=twitter_deletes_connect
        
        #to check if topic is created or not:
        #amit@amit-Lenovo-ideapad-520-15IKB:~/kafka_2.13-2.7.0$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --list
        #__consumer_offsets
        #first_topic
        #new_topic
        #new_topic3
        #twitter_deletes_connect
        #twitter_status_connect
        #twitter_tweets
        
        
        twitter.oauth.consumerKey=uHeDmSuM7U0VjQdU67TfVUUL2
        twitter.oauth.consumerSecret=pTV5iBOc4jpSX8PhQOA5SIzcKMg9klCNcOv5bDXKN24LY5DoME
        twitter.oauth.accessToken=1317060962-ISWgK7uNWhSU6o6uj4lVmCezlybPHbRNq3UZpSY
        twitter.oauth.accessTokenSecret=5JEb0fFv887A79du9IHZLwJprGsg5I9PfdXjaDHjAVKmt

Step 5: twitter.properties are updated and topics are created, now start the consumer using below commands:

        amit@amit-Lenovo-ideapad-520-15IKB:~/kafka_2.13-2.7.0$ kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic twitter_status_connect --from-beginning

Step 6: now open a new terminal, and you should go to your project directory:

        amit@amit-Lenovo-ideapad-520-15IKB:~/kafka_2.13-2.7.0$ cd '/home/amit/git/kafka/kafka-beginners/kafka-connect'
        amit@amit-Lenovo-ideapad-520-15IKB:~/git/kafka/kafka-beginners/kafka-connect$ ls
        connectors  connect-standalone.properties  KAFKA-CONNECT.MD  run.sh  twitter.properties
        amit@amit-Lenovo-ideapad-520-15IKB:~/git/kafka/kafka-beginners/kafka-connect$ ll
        total 28
        drwxrwxr-x 3 amit amit 4096 May 28 02:13 ./
        drwxrwxr-x 8 amit amit 4096 May 27 19:29 ../
        drwxrwxr-x 3 amit amit 4096 May 28 01:37 connectors/
        -rw-rw-r-- 1 amit amit 2271 May 28 01:36 connect-standalone.properties
        -rw-rw-r-- 1 amit amit 4581 May 28 02:11 KAFKA-CONNECT.MD
        -rw-rw-r-- 1 amit amit    0 May 28 02:13 run.sh
        -rw-rw-r-- 1 amit amit 2269 May 28 02:04 twitter.properties
        amit@amit-Lenovo-ideapad-520-15IKB:~/git/kafka/kafka-beginners/kafka-connect$ cat run.sh
        #!/usr/bin/env bash
        # run the twitter connector
        #connect-standalone connect-standalone.properties twitter.properties
        # OR (linux / mac OSX)
        connect-standalone.sh connect-standalone.properties twitter.properties
        # OR (Windows)
        #connect-standalone.bat connect-standalone.properties twitter.properties

Step 7: Run the command from "run.sh" file, like below:

        amit@amit-Lenovo-ideapad-520-15IKB:~/git/kafka/kafka-beginners/kafka-connect$ connect-standalone.sh connect-standalone.properties twitter.properties
        [2021-05-28 02:20:10,422] INFO Kafka Connect standalone worker initializing ... (org.apache.kafka.connect.cli.ConnectStandalone:69)
        [2021-05-28 02:20:10,435] INFO WorkerInfo values:
        jvm.args = -Xms256M, -Xmx2G, -XX:+UseG1GC, -XX:MaxGCPauseMillis=20, -XX:InitiatingHeapOccupancyPercent=35, -XX:+ExplicitGCInvokesConcurrent, -XX:MaxInlineLevel=15, -Djava.awt.headless=true, -Dcom.sun.management.jmxremote, -Dcom.sun.management.jmxremote.authenticate=false, -Dcom.sun.management.jmxremote.ssl=false, -Dkafka.logs.dir=/home/amit/kafka_2.13-2.7.0/bin/../logs, -Dlog4j.configuration=file:/home/amit/kafka_2.13-2.7.0/bin/../config/connect-log4j.properties
        jvm.spec = Ubuntu, OpenJDK 64-Bit Server VM, 11.0.11, 11.0.11+9-Ubuntu-0ubuntu2.18.04

Step 8: if you run form right directory, you should not get any error. 
you can connector started, and you can see lots of logs and establish a connection and start producing data. 
Now if we go to console consumer, we can see data from twitter, directly put into our consumer right away.

Here we are running connector in Stand alone mode, this is not deep dive, we are just demonstrating how powerfulll connector is.

Using just configuration file we were able to take data from twitter and put in kafka right away.


