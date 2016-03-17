import twitter4j.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Twitter twitter = TwitterFactory.getSingleton();

        Query query = new Query("#AppleVsFBI");
        query.setCount(200);
        query.setLang("en");
        query.setResultType(Query.ResultType.recent);

        try {
            QueryResult results = twitter.search(query);
            List<Status> tweets = results.getTweets();

            for(Status tweet : tweets){
                System.out.println("@" + tweet.getUser().getScreenName() + " : " + tweet.getText() + "\n");
            }

            System.out.println("Received " + tweets.size() + " tweets");
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
}
