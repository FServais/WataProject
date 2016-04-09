
import twitter4j.*;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Status> tweets = null;

        tweets = searchTweet("#AppleVSFBI", 10);

        System.out.println("Number of tweets: " + tweets.size());

    }

    /**
     * Search the tweet corresponding to a query.
     * @param query Query to search.
     * @param maxNumberTweets Maximum number of tweets to retrieve. -1 if no limit (actually will be set to to max 180 requests).
     * @return List of tweet matching the query.
     * @throws TwitterException
     */
    private static List<Status> searchTweet(String query, int maxNumberTweets) {
        System.out.print("Retrieving page ");
        int pageNumber = 1; // For log purpose

        Twitter twitter = TwitterFactory.getSingleton();

        Query twitterQuery = new Query(query);
        twitterQuery.setCount(100); // Max # of tweet per page (100 is max)
        twitterQuery.setLang("en");
        twitterQuery.setResultType(Query.ResultType.mixed);

        List<Status> tweets = new ArrayList<>();

        if(maxNumberTweets == -1)
            maxNumberTweets = Integer.MAX_VALUE;

        long maxId = Long.MAX_VALUE;

        QueryResult results;
        do {
            // Log
            System.out.print(pageNumber + "...");
            ++pageNumber;
            if(pageNumber % 10 == 0)
                System.out.println();

            // Maximum number of requests by Twitter API (every 15 minutes)
            if(pageNumber >= 180)
                break;

            try {
                results = twitter.search(twitterQuery);
            } catch (TwitterException e) {
                System.out.println();
                return tweets;
            }

            List<Status> resultsTweets = results.getTweets();

            for(Status tweet : resultsTweets){
                if(tweet.getId() < maxId)
                    maxId = tweet.getId();
                
                // Ignore retweet since the original tweet should be in the search since it should also match the query
                if(tweet.isRetweet())
                    continue;

                tweets.add(tweet);

                if(tweets.size() >= maxNumberTweets)
                    break;
            }

            twitterQuery.setMaxId(maxId);

        } while(tweets.size() < maxNumberTweets && results.getTweets().size() > 0);
        System.out.println();

        return tweets;
    }
}
