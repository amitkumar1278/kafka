package akg.kafka.tutorial3;

import com.google.gson.JsonParser;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ElsSrchCnsmrIdompotence
{

    public static RestHighLevelClient createClient(){

        // Replaced with your own credentials
        String hostname = "kafka-course-8513360256.ap-southeast-2.bonsaisearch.net";
        String username = "7d5hbnex93";
        String password = "usfypm4gd0";

        // don't do if you run a local ElasticSearch
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        RestClientBuilder builder = RestClient.builder(new HttpHost(hostname, 443, "https"))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                        return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
            });

        RestHighLevelClient client = new RestHighLevelClient(builder);

        return client;
    }

    public static KafkaConsumer<String, String > createConsumer(String topic){
        String bootstrapServers = "127.0.0.1:9092";
        String groupId = "kafka-demo-elasticsearch";

        // Create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // disable auto commit of offsets
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10"); // disable auto commit of offsets

        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList(topic));
        return consumer;
    }


    public static void main(String[] args) throws IOException {

        Logger logger = LoggerFactory.getLogger(ElsSrchCnsmrIdompotence.class.getName());
        RestHighLevelClient client = createClient();
        KafkaConsumer<String, String> consumer = createConsumer("twitter_tweets");

        while(true){

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            logger.info("\n\nReceived: " + records.count() + " records");

            // where we insert data into elasticsearch
            for(ConsumerRecord<String, String> record : records){

                // There is 2 strategies to create id
                // 1. Kafka Generic id: this we can do when we can't find id
                // String id = record.topic() + "_" +record.partition() + "_" + record.offset();

                // 2. twitter feed specific id
                String id = extractIdFromTweet(record.value());

                // passing id here to make our consumer idempotent
                IndexRequest indexRequest = new IndexRequest("twitter", "tweets", id)
                        .source(record.value(), XContentType.JSON);
                IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
                logger.info(indexResponse.getId());

                try {
                    Thread.sleep(10); // introduce small delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            logger.info("Committing offsets...");
            consumer.commitSync();
            logger.info("Offsets have been committed.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // to verify if our consumer have correct offset, use below command in terminal.
        //  amit@amit-Lenovo-ideapad-520-15IKB:~/kafka_2.13-2.7.0$ kafka-consumer-groups.sh --bootstrap-server 120.0.0.1:9092 --group kafka-demo-elasticsearch --describe
    }

    private static JsonParser jsonParser = new JsonParser();

    private static String extractIdFromTweet(String tweetJson) {

        // gson library
       return  jsonParser.parse(tweetJson)
                .getAsJsonObject()
                .get("id_str")
                .getAsString();
    }
}
