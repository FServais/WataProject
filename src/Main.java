import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TwitterSearchEngine searchEngine = new TwitterSearchEngine();

        List<Status> tweets = null;
//        long lastId = -1;
        long lastId = 718702550183096320L - 1;
        tweets = searchEngine.searchTweet("#AppleVsFBI", 5, lastId);

        System.out.println("Number of tweets: " + tweets.size());
        for(Status tweet : tweets){
            System.out.println("[" + tweet.getId() + "]" + " :: " +tweet.getText() + "---");
        }
    }
}
