package com.github.simpleapp.kafka.tutorial2.twitter;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**   Follow the quick start of and copy msgQueue: https://github.com/twitter/hbc         */
public class TwitterProducer {

    Logger logger = LoggerFactory.getLogger(TwitterProducer.class.getName());

    String consumerKey = "uHeDmSuM7U0JHKJHKHHUYDVjQdU67TfVUUL2";  // API Key
    String consumerSecret = "pTV5iBOc4jpSX8PhQOA5UOIUSPDHFDSJNFNCWJEHFEWJHSIzcKMg9klCNcOv5bDXKN24LY5DoME"; // API Secret Key
    String bearerToken = "AAAAAAAAAAAAAAAAAAAAANatPwEAAAAAXDWN%2BZrR6k3l%2FCDDkKNz9vwiY%2Fw%3DTf15ctuz6hVzVFyyZU9nXYsRzo0T3DrGCN7YChZteoeseV25sC"; // Bearer Token
    String token = "1317060962-ISWgK7uNWhSU6o6uj4lVmCezlybPHbRNqUYUYUYUIY3UZpSY"; // Access Token
    String secret = "587A79du9IHZLwJprGsg5I9PfdXjaDHjAVKOIUYUOYUOUOUOUOmt"; // Access Token Secret

    public TwitterProducer() {
    }

    public static void main(String[] args) {

        new TwitterProducer().run();

    }

    public void run() {

        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);

        // Create a twitter client
        Client client = createTwitterClient(msgQueue);

        // Attempts to establish a connection
        client.connect();

        // Create a kafka producer

        // loop to send tweets to kafka
        // on a different thread, or multiple different threads....
        while (!client.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.stop();
            }

            if(msg != null){
                logger.info(msg);
            }

            logger.info("End of application");

        }
    }



    public Client createTwitterClient(BlockingQueue<String> msgQueue) {
        /**   Follow the quick start of and copy msgQueue: https://github.com/twitter/hbc         */

        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

        List<String> terms = Lists.newArrayList("australia", "api");
        hosebirdEndpoint.trackTerms(terms);

        // These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);

        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        Client hosebirdClient = builder.build();

        return hosebirdClient;
    }

}
