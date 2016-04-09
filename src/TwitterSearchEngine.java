import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Fabrice Servais (fabrice.servais@gmail.com)<br/>
 *         Date : 09/04/16
 */
public class TwitterSearchEngine {

    private long lastMaxId;

    public TwitterSearchEngine() {
        this.lastMaxId = Long.MAX_VALUE;
    }

    public long getLastMaxId() {
        return lastMaxId;
    }

    /**
     * Search the tweet corresponding to a query.
     * @param query Query to search.
     * @param maxNumberTweets Maximum number of tweets to retrieve. -1 if no limit (actually will be set to to max 180 requests).
     * @param fromMaxId Begins the search from that ID. -1 if first time.
     * @return List of tweet matching the query.
     * @throws TwitterException
     */
    public List<Status> searchTweet(String query, int maxNumberTweets, long fromMaxId) {
        System.out.print("Retrieving page ");
        int pageNumber = 1; // For log purpose

        Twitter twitter = TwitterFactory.getSingleton();

        Query twitterQuery = new Query(query);
        twitterQuery.setCount(100); // Max # of tweet per page (100 is max)
        twitterQuery.setLang("en");
        twitterQuery.setResultType(Query.ResultType.recent);

        if (fromMaxId != -1)
            twitterQuery.setMaxId(fromMaxId);

        List<Status> tweets = new ArrayList<>();

        if(maxNumberTweets == -1)
            maxNumberTweets = Integer.MAX_VALUE;

        QueryResult results;
        do {
            // Log
            System.out.print(pageNumber + "...");
            ++pageNumber;
            if(pageNumber % 10 == 0)
                System.out.println();

            // Maximum number of requests by Twitter API (every 15 minutes)
            if(pageNumber >= 180) {
                break;
            }

            try {
                results = twitter.search(twitterQuery);
            } catch (TwitterException e) {
                System.out.println();
                return tweets;
            }

            List<Status> resultsTweets = results.getTweets();

            for(Status tweet : resultsTweets){
                if(tweet.getId() < this.lastMaxId)
                    this.lastMaxId = tweet.getId();

                // Ignore retweet since the original tweet should be in the search since it should also match the query
                if(tweet.isRetweet())
                    continue;

                // Ignoring the links
                if(tweet.getText().contains("http://"))
                    continue;

                tweets.add(tweet);

                if(tweets.size() >= maxNumberTweets)
                    break;
            }

            twitterQuery.setMaxId(this.lastMaxId);

        } while(tweets.size() < maxNumberTweets && results.getTweets().size() > 0);
        System.out.println();
        System.out.println("Last maxId: " + this.lastMaxId);
        return tweets;
    }

    public List<Status> searchTweet(String query, int maxNumberTweets) {
        return this.searchTweet(query, maxNumberTweets, -1);
    }


    public void cleanSearchEngine(){
        this.lastMaxId = Long.MAX_VALUE;
    }
}
