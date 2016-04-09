import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TwitterSearchEngine searchEngine = new TwitterSearchEngine();

        List<Status> tweets = null;
//        long lastId = -1;
        long lastId = 717428853405327365L;
        tweets = searchEngine.searchTweet("#AppleVsFBI", 2, lastId);

        System.out.println("Number of tweets: " + tweets.size());
        for(Status tweet : tweets){
            System.out.println(tweet.getText());
        }
    }
}
