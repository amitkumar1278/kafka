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

public class ElasticSearchConsumerIdompotence
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

        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList(topic));
        return consumer;
    }


    public static void main(String[] args) throws IOException {

        Logger logger = LoggerFactory.getLogger(ElasticSearchConsumerIdompotence.class.getName());
        RestHighLevelClient client = createClient();
        KafkaConsumer<String, String> consumer = createConsumer("twitter_tweets");

        while(true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

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
                    Thread.sleep(1000); // introduce small delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // To verify, if the tweets of above/logged id is inserted or not to the elastic search cluster, please run below query:
                // Query: GET /twitter/tweets/{id from console}
                // Query Response like: 200
                // "{\"_index\":\"twitter\",\"_type\":\"tweets\",\"_id\":\"Ro3tp3kBU7dCTD5LUYA3\",\"_version\":1,\"_seq_no\":14,\"_primary_term\":1,\"found\":true,\"_source\":{\"created_at\":\"Mon May 24 13:24:45 +0000 2021\",\"id\":1396819458702913500,\"id_str\":\"1396819458702913539\",\"text\":\"Interesting thread for Canadians\",\"source\":\"<a href=\\"https:\/\/mobile.twitter.com\\" rel=\\"nofollow\\">Twitter Web App<\/a>\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":2265838652,\"id_str\":\"2265838652\",\"name\":\"Michael Cowtan Climate Action CK \ud83c\udf10\",\"screen_name\":\"MichaelCowtan\",\"location\":\"Chatham-Kent, Ontario\",\"url\":null,\"description\":\"Reformed conservative, it took me 75 years to realise that I am a social democrat,  LGBT ally, No DMs please\ud83c\udff3\ufe0f\u200d\ud83c\udf08\ud83d\udd4a\ufe0f\ud83c\udf0f\ud83c\udf0d\ud83c\udf0e\ud83c\udf10#climatebrawl #Vaccinated\",\"translator_type\":\"none\",\"protected\":false,\"verified\":false,\"followers_count\":2187,\"friends_count\":2962,\"listed_count\":13,\"favourites_count\":82353,\"statuses_count\":38774,\"created_at\":\"Sat Dec 28 12:28:26 +0000 2013\",\"utc_offset\":null,\"time_zone\":null,\"geo_enabled\":true,\"lang\":null,\"contributors_enabled\":false,\"is_translator\":false,\"profile_background_color\":\"C0DEED\",\"profile_background_image_url\":\"http:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png\",\"profile_background_image_url_https\":\"https:\/\/abs.twimg.com\/images\/themes\/theme1\/bg.png\",\"profile_background_tile\":false,\"profile_link_color\":\"1DA1F2\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":true,\"profile_image_url\":\"http:\/\/pbs.twimg.com\/profile_images\/1352982255430471680\/H7DEwF7H_normal.jpg\",\"profile_image_url_https\":\"https:\/\/pbs.twimg.com\/profile_images\/1352982255430471680\/H7DEwF7H_normal.jpg\",\"profile_banner_url\":\"https:\/\/pbs.twimg.com\/profile_banners\/2265838652\/1615649375\",\"default_profile\":true,\"default_profile_image\":false,\"following\":null,\"follow_request_sent\":null,\"notifications\":null,\"withheld_in_countries\":[]},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"quoted_status_id\":1396698068532269000,\"quoted_status_id_str\":\"1396698068532269057\",\"quoted_status\":{\"created_at\":\"Mon May 24 05:22:23 +0000 2021\",\"id\":1396698068532269000,\"id_str\":\"1396698068532269057\",\"text\":\"+UPDATE+\n\nCanada has informed UK it expects the same \u201czero tariffs\/zero quota\u201d on agriculture as Australia is being\u2026 https:\/\/t.co\/VDoWodBM8E\",\"source\":\"<a href=\\"https:\/\/mobile.twitter.com\\" rel=\\"nofollow\\">Twitter Web App<\/a>\",\"truncated\":true,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":134890715,\"id_str\":\"134890715\",\"name\":\"Nick\ud83c\uddec\ud83c\udde7\ud83c\uddea\ud83c\uddfa\",\"screen_name\":\"nicktolhurst\",\"location\":\"Europe\",\"url\":null,\"description\":\"Anglo-European. Optimistic Arsenal fan, Agatha Christie head & whisky drinker. Purveyor of general englishness from a time before world lost its marbles\",\"translator_type\":\"none\",\"protected\":false,\"verified\":false,\"followers_count\":33592,\"friends_count\":2453,\"listed_count\":209,\"favourites_count\":27602,\"statuses_count\":45650,\"created_at\":\"Mon Apr 19 19:03:48 +0000 2010\",\"utc_offset\":null,\"time_zone\":null,\"geo_enabled\":true,\"lang\":null,\"contributors_enabled\":false,\"is_translator\":false,\"profile_background_color\":\"000000\",\"profile_background_image_url\":\"http:\/\/abs.twimg.com\/images\/themes\/theme19\/bg.gif\",\"profile_background_image_url_https\":\"https:\/\/abs.twimg.com\/images\/themes\/theme19\/bg.gif\",\"profile_background_tile\":false,\"profile_link_color\":\"E81C4F\",\"profile_sidebar_border_color\":\"000000\",\"profile_sidebar_fill_color\":\"000000\",\"profile_text_color\":\"000000\",\"profile_use_background_image\":false,\"profile_image_url\":\"http:\/\/pbs.twimg.com\/profile_images\/968494104178036736\/FJQHUsSX_normal.jpg\",\"profile_image_url_https\":\"https:\/\/pbs.twimg.com\/profile_images\/968494104178036736\/FJQHUsSX_normal.jpg\",\"profile_banner_url\":\"https:\/\/pbs.twimg.com\/profile_banners\/134890715\/1493128717\",\"default_profile\":false,\"default_profile_image\":false,\"following\":null,\"follow_request_sent\":null,\"notifications\":null,\"withheld_in_countries\":[]},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"is_quote_status\":false,\"extended_tweet\":{\"full_text\":\"+UPDATE+\n\nCanada has informed UK it expects the same \u201czero tariffs\/zero quota\u201d on agriculture as Australia is being offered if a trade deal is to be signed.\n\nThis would seem to confirm civil servants\u2019 warnings that a trade precedent is being set that will destroy British farming.\",\"display_text_range\":[0,280],\"entities\":{\"hashtags\":[],\"urls\":[],\"user_mentions\":[],\"symbols\":[]}},\"quote_count\":118,\"reply_count\":99,\"retweet_count\":1607,\"favorite_count\":3293,\"entities\":{\"hashtags\":[],\"urls\":[{\"url\":\"https:\/\/t.co\/VDoWodBM8E\",\"expanded_url\":\"https:\/\/twitter.com\/i\/web\/status\/1396698068532269057\",\"display_url\":\"twitter.com\/i\/web\/status\/1\u2026\",\"indices\":[117,140]}],\"user_mentions\":[],\"symbols\":[]},\"favorited\":false,\"retweeted\":false,\"filter_level\":\"low\",\"lang\":\"en\"},\"quoted_status_permalink\":{\"url\":\"https:\/\/t.co\/KT5kRR55Vu\",\"expanded\":\"https:\/\/twitter.com\/nicktolhurst\/status\/1396698068532269057\",\"display\":\"twitter.com\/nicktolhurst\/s\u2026\"},\"is_quote_status\":true,\"quote_count\":0,\"reply_count\":0,\"retweet_count\":0,\"favorite_count\":0,\"entities\":{\"hashtags\":[],\"urls\":[],\"user_mentions\":[],\"symbols\":[]},\"favorited\":false,\"retweeted\":false,\"filter_level\":\"low\",\"lang\":\"en\",\"timestamp_ms\":\"1621862685262\"}}"
                // if you run this program again a new record will inserted with new id
            }
        }

        // close the client gracefully
       // client.close();
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
