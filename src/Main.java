import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TwitterSearchEngine searchEngine = new TwitterSearchEngine();

        List<Status> tweets = null;

        tweets = searchEngine.searchTweet("#AppleVsFBI", 50000);

        System.out.println("Number of tweets: " + tweets.size());
    }
}
