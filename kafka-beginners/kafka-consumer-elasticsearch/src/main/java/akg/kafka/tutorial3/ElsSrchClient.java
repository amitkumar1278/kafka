package akg.kafka.tutorial3;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
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

public class ElsSrchClient
{

    public static RestHighLevelClient createClient(){

        // Created Cluster at : https://app.bonsai.io/clusters/kafka-course-8513360256/console
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


    public static void main(String[] args) throws IOException {

        Logger logger = LoggerFactory.getLogger(ElsSrchClient.class.getName());
        RestHighLevelClient client = createClient();
        String jsonString = " {\"foo\": \"bar\"}";

        // Before this we need to create an index in Elastic Search using query:
        // Query:  PUT /twitter
        //Query Response: 200
        //   {"acknowledged": true, "shards_acknowledged": true, "index": "twitter" }
        IndexRequest indexRequest = new IndexRequest("twitter", "tweets")
                .source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        String id = indexResponse.getId();
        logger.info(id);

        // To verify, if the docs of above id is inserted or not to the elastic search, please run below query in elasticsearch:
        // Query: GET /twitter/tweets/{id from console}
        // Query Response like: 200
        // {"_index": "twitter",  "_type": "tweets", "_id": "N42op3kBU7dCTD5L84DA", "_version": 1, "_seq_no": 0, "_primary_term": 1, "found": true, "_source": { "foo": "bar" } }
        // if you run this program again a new record will inserted with new id

        // close the client gracefully
        client.close();
    }
}
