import twitter4j.Status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Fabrice Servais (fabrice.servais@gmail.com)<br/>
 *         Date : 09/04/16
 */
public class SentimentAnalyser {

    Map<String, Integer> sentimentWords;

    public SentimentAnalyser() {
        this.sentimentWords = new HashMap<String, Integer>();
    }

    public void analyseTweets() {
        List<Status> tweets = getTweets();

        parseDictionary();

        // Give score to each tweet
        Map<Status, Integer> scoreMap = new HashMap<Status, Integer>();

        for(Status tweet : tweets) {
            int score = score(tweet);
            scoreMap.put(tweet, score);
        }
    }

    private List<Status> getTweets(){
        TwitterSearchEngine searchEngine = new TwitterSearchEngine();

        List<Status> tweets = null;
        long lastId = -1;
//        long lastId = 718702550183096320L - 1;
        tweets = searchEngine.searchTweet("#AppleVsFBI", 100, lastId);

        System.out.println("Number of tweets: " + tweets.size());
        for(Status tweet : tweets){
            System.out.println("[" + tweet.getId() + "]" + " :: " +tweet.getText() + "---");
        }
        System.out.println("========================");

        return tweets;
    }

    private void parseDictionary(){
        DictionnaryParser parser = new DictionnaryParser();
        parser.parseFile("sentiment-dict.txt");

        this.sentimentWords = parser.getSentimentMap();
    }

    private int score(Status tweet) {
        String text = tweet.getText();
        text = text.toLowerCase();
        text = text.replaceAll("[^a-zA-Z ]","");

        String[] words = text.split(" ");

        int finalScore = 0;

        for(String word : words){
            if (word.startsWith("http")){
                continue;
            }

            if (this.sentimentWords.containsKey(word)){
                finalScore += this.sentimentWords.get(word);
            }
        }

        return finalScore;
    }
}
