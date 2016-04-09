import twitter4j.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
    	TwitterSearchEngine searchEngine = new TwitterSearchEngine();
    	TweetCleaner tc = new TweetCleaner();
    	
        List<Status> tweets = null;      
        ArrayList<String> cleans = null;
        long lastId = 717428853405327365L;
        tweets = searchEngine.searchTweet("#AppleVsFBI", 10, lastId);
        cleans = tc.cleanTweets(tweets);
        
        System.out.println("Number of tweets: " + tweets.size());
        for(int i = 0; i < tweets.size(); ++i){
        	System.out.println("NEXT TWEET : ");
            System.out.println(tweets.get(i).getText());
            System.out.println("Clean : ");
            System.out.println(cleans.get(i));
            
        }

    }
}
