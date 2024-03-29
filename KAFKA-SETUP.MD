# LINUX: Download and Setup Kafka path

**Step 1:** Check if java is install
		
		$ java -version

if java is not installed use below commands to install openjdk:
		
		$ sudo apt install openjdk-{{version}}-jdk 

-------------------------------------------------------------------------------	

**Step 2:** Download & Extract the Kafka binaries from: https://kafka.apache.org/downloads.html

-------------------------------------------------------------------------------	
	
**Step 3:** Go to download directory in terminal and execute below commands:
		
		$ mv Downloads/kafka_2.13-2.7.0.tgz .
		$ tar -xzf kafka_2.13-2.7.0.tgz
		$ cd kafka_2.13-2.7.0/
		$ bin/kafka-topics.sh			=>	this command will show list of available commands if java is installed;	Try Kafka commands using bin/kafka-topics.sh;
		
-------------------------------------------------------------------------------		

**Step 4:** Edit PATH to include Kafka (in ~/.bashrc for example)
		
		$ nano .bashrc		
			export PATH="$PATH:/your/path/to/your/kafka/bin"
			export PATH=/home/amit/kafka_2.13-2.7.0/bin:$PATH
			
-------------------------------------------------------------------------------		
**Step 5:** in new terminal configure/start zookeeper 
		
		$ cd kafka_2.13-2.7.0/
		$ mkdir data 
		$ mkdir data/zookeeper
		$ nano config/zookeeper.properties		
			current config:   dataDir=/tmp/zookeeper
			change to: dataDir=/home/amit/kafka_2.13-2.7.0/data/zookeeper
		$ cat config/zookeeper.properties
		$ bin/zookeeper-server-start.sh config/zookeeper.properties			=> if everything is well and zookeper started, you will get "binding to port 0.0.0.0/0.0.0.0:2181" in log. 
																								
Open a new terminal and run below commands to confirm:
		$ cd kafka_2.13-2.7.0/
		$ ls data/zookeeper/			=> check if new version is created like "version-2".
		
-------------------------------------------------------------------------------		

**Step 6:** in new terminal Configure/Start Kafka
		
		$ cd kafka_2.13-2.7.0/
		$ mkdir data/kafka
		$ nano config/server.properties
			current config:   log.dirs=/tmp/kafka-logs
			change to: dataDir=/home/amit/kafka_2.13-2.7.0/data/kafka
		$ kafka-server-start.sh			=> for successfull start check logs for "[KafkaServer id=0] started"

or

		$ bin/kafka-server-start.sh config/server.properties

**Important:** For the rest of the course, don't forget to add the extension .sh to commands being run

**open a new tab and run below commands:**
		
		$ cd kafka_2.13-2.7.0/
		$ ls data/kafka/			=> few files(5) should be created by kafka.
-------------------------------------------------------------------------------		

## Conduktor - Kafka GUI

Kafka does not come bundled with a UI, but I have created a software name Conduktor to help you use Kafka visually.

You can download Conduktor here: https://www.conduktor.io/

Conduktor allows you to perform all the administrative tasks on Kafka (such as creating topics, partitions, etc), as well as produce and consume, all from within a desktop application that should work on Windows, Mac, and Linux.

Please look these two pages in cases you're having issues:

Documentation: https://www.conduktor.io/docs

The FAQ for installation issues (at the bottom of the page): https://www.conduktor.io/download

-------------------------------------------------------------------------------		

## KafkaCat

KafkaCat as a replacement for Kafka CLI
KafkaCat (https://github.com/edenhill/kafkacat) is an open-source alternative to using the Kafka CLI, created by Magnus Edenhill.

if you have any interest in trying it out, recommend reading: https://medium.com/@coderunner/debugging-with-kafkacat-df7851d21968



-------------------------------------------------------------------------------		













