package com.github.simpleapp.kafka.tutorial1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemoGroups {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(ConsumerDemoGroups.class.getName());

        String bootstrapServers = "127.0.0.1:9092";
        //  String groupId = "my-fourth-application";
        /**
         *   to check how much message is lagging:
         *   $ kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group my-fourth-application --describe
         *
         *   if lag is 0 in above command, means current groupId read all latest message.
         *   so if you want to restart your application and read message from beginning
         *      you either have to reset groupId or you just make group id change.
         *   so if you do above step you will see all the message from the beginning.
         */
        String groupId = "my-fifth-application";

        String topic = "first_topic";

        // Create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");



        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        // subscribe consumer to  our topic(s)
//        consumer.subscribe(Collections.singleton(topic));
        consumer.subscribe(Arrays.asList(topic));

        // poll[ for new data
        while(true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for(ConsumerRecord<String, String> record : records){
                logger.info("Key: "+ record.key() + ", Value: "+ record.value());
                logger.info("Partition: "+record.partition() + ", Offset: "+ record.offset());
            }
        }
    }
}
