package com.github.simpleapp.kafka.tutorial1;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerDemoWithCallback {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallback.class);

        String bootstrapServers =  "127.0.0.1:9092";

        // create Producer Properties: https://kafka.apache.org/documentation/#producerconfigs
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty("", "");

        // Create Producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        for(int i = 0; i< 10; i++) {
            // Create a producer record

            String topic = "first_topic";
            String value = "hello world" + Integer.toString(i);
            String key = "id_" + Integer.toString(i);

            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, value);
// testing for git

            logger.info("Key: "+ key); // log the key
            // id_0 is going to partition 1
            // id_1 is going to partition 0
            // id_2 is going to partition 2
            // id_3 is going to partition 0
            // id_4 is going to partition 2
            // id_5 is going to partition 2
            // id_6 is going to partition 0
            // id_7 is going to partition 2
            // id_8 is going to partition 1
            // id_9 is going to partition 2

            // above we observe, Basically by providing the key same key will always go to same partition, run this method multiple times and observe.

            // send data -- asynchronous
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // execute every time a record is successfully sent or an exception is thrown
                    if (e == null) {
                        // the record was successfully sent
                        logger.info("Received new metadata. \n" +
                                "Topic: " + recordMetadata.topic() + "\n" +
                                "Partition: " + recordMetadata.partition() + "\n" +
                                "Offset: " + recordMetadata.offset() + "\n" +
                                "Timestamp: " + recordMetadata.timestamp());
                    } else {
                        logger.error("Error while producing  ", e);
                    }
                }
            }).get(); // block the .send() to make it synchronous, don't do this in productions!
        }

        // flush data
        producer.flush();

        // flush and close producer
        producer.close();
    }
}
