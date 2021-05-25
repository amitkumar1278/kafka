# Twitter Setup Instruction

### Command to create topics: if topic is not created from program, we can create from cmd:

        amit@amit-Lenovo-ideapad-520-15IKB:~/kafka_2.13-2.7.0$ kafka-topics.sh --zookeeper 127.0.0.1:2181 --create --topic twitter_tweets --partitions 6 --replication-factor 1
        WARNING: Due to limitations in metric names, topics with a period ('.') or underscore ('_') could collide. To avoid issues it is best to use either, but not both.
        Created topic twitter_tweets.

### if java Consumer is not available we can start a consumer for twitter producer and topic using below commands:

        amit@amit-Lenovo-ideapad-520-15IKB:~/kafka_2.13-2.7.0$ kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic twitter_tweets


Once Producer starting producing tweets it will look like below:

        amit@amit-Lenovo-ideapad-520-15IKB:~/kafka_2.13-2.7.0$ kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic twitter_tweets
        {"created_at":"Mon May 24 13:24:21 +0000 2021","id":1396819359390044164,"id_str":"1396819359390044164","text":"Despite the worldwide \"crisis\" that is the Covid virus it appears to me \"domestic violence\" fatalities outnumber co\u2026 https:\/\/t.co\/Su5JcN4QS7","source":"\u003ca href=\"https:\/\/about.twitter.com\/products\/tweetdeck\" rel=\"nofollow\"\u003eTweetDeck\u003c\/a\u003e","truncated":true,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":232787997,"id_str":"232787997","name":"PavlovsGecko","screen_name":"AuspolRefugee","location":"2483","url":null,"description":"drummer, electrician, grandfather, skeptic. Hater of gratuitous abuse but not backward about being https:\/\/t.co\/z2PLNOoZya yanks,hate USA control of our media\/arts content","translator_type":"none","protected":false,"verified":false,"followers_count":956,"friends_count":793,"listed_count":27,"favourites_count":4366,"statuses_count":76865,"created_at":"Sat Jan 01 08:55:07 +0000 2011","utc_offset":null,"time_zone":null,"geo_enabled":false,"lang":null,"contributors_enabled":false,"is_translator":false,"profile_background_color":"ACDED6","profile_background_image_url":"http:\/\/abs.twimg.com\/images\/themes\/theme18\/bg.gif","profile_background_image_url_https":"https:\/\/abs.twimg.com\/images\/themes\/theme18\/bg.gif","profile_background_tile":false,"profile_link_color":"038543","profile_sidebar_border_color":"EEEEEE","profile_sidebar_fill_color":"F6F6F6","profile_text_color":"333333","profile_use_background_image":true,"profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/1095213392246145024\/x-g30YU-_normal.jpg","profile_image_url_https":"https:\/\/pbs.twimg.com\/profile_images\/1095213392246145024\/x-g30YU-_normal.jpg","default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null,"withheld_in_countries":[]},"geo":null,"coordinates":null,"place":null,"contributors":null,"is_quote_status":false,"extended_tweet":{"full_text":"Despite the worldwide \"crisis\" that is the Covid virus it appears to me \"domestic violence\" fatalities outnumber covid virus fatalities in Australia yet #auspol is still refusing to take real action to address this human disaster. Am I right?","display_text_range":[0,242],"entities":{"hashtags":[{"text":"auspol","indices":[153,160]}],"urls":[],"user_mentions":[],"symbols":[]}},"quote_count":0,"reply_count":0,"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[],"urls":[{"url":"https:\/\/t.co\/Su5JcN4QS7","expanded_url":"https:\/\/twitter.com\/i\/web\/status\/1396819359390044164","display_url":"twitter.com\/i\/web\/status\/1\u2026","indices":[117,140]}],"user_mentions":[],"symbols":[]},"favorited":false,"retweeted":false,"filter_level":"low","lang":"en","timestamp_ms":"1621862661584"}




