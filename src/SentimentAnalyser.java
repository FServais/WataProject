import twitter4j.Status;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Fabrice Servais (fabrice.servais@gmail.com)<br/>
 *         Date : 09/04/16
 */
public class SentimentAnalyser {

    final String DICTIONARY = "sentiment-dict.txt";
    final String HASHTAG = "#AppleVsFBI";
    final int MAX_TWEETS = 10;

    Map<String, Integer> sentimentWords;
    TweetCleaner cleaner;

    public SentimentAnalyser() {
        this.sentimentWords = new HashMap<>();
        this.cleaner = new TweetCleaner();
    }

    public void analyseTweets() {
        TweetCleaner tc = new TweetCleaner();
        ArrayList<String> cleanedTweets = tc.cleanTweets(getTweets());

        parseDictionary();

        // Give score to each tweet
        Map<String, Integer> scoreMap = new HashMap<>();

        for(String tweet : cleanedTweets) {
            int score = score(tweet);
            scoreMap.put(tweet, score);
        }
    }

    private List<Status> getTweets(){
        TwitterSearchEngine searchEngine = new TwitterSearchEngine();

        List<Status> tweets;
        long lastId = -1;
        tweets = searchEngine.searchTweet(HASHTAG, MAX_TWEETS, lastId);

        System.out.println("Number of tweets: " + tweets.size());
        for(Status tweet : tweets){
            System.out.println("[" + tweet.getId() + "]" + " :: " +tweet.getText() + "---");
        }
        System.out.println("========================");

        return tweets;
    }

    private void parseDictionary(){
        DictionnaryParser parser = new DictionnaryParser();
        parser.parseFile(DICTIONARY);

        this.sentimentWords = parser.getSentimentMap();
    }

    private int score(String tweet) {
        String[] words = tweet.split(" ");

        int finalScore = 0;

        for(String word : words){
            if (this.sentimentWords.containsKey(word)){//first we check if the word is contained in ou dictionary
                finalScore += this.sentimentWords.get(word);
            }
            else{//if not we check through a Python program what's the lemma of the word
                try {
                    Process p = Runtime.getRuntime().exec("python lemmatizer.py " + word);
                    BufferedReader stdInput = new BufferedReader(new
                            InputStreamReader(p.getInputStream()));

                    // read the output from the command
                    String output = "";
                    while ((output.concat(stdInput.readLine())) != null) {
                        continue;
                    }

                    if(output.length()>0){
                        //first we check that the python script was able to identify the meaning of the abbreviation
                        //this means that output has to be different from word
                        if(output.equals(word) || !this.sentimentWords.containsKey(output)){
                            //the script didn't resolve the word, so we have to add it to the dictionary
                            DictionnaryParser.addWordToMap(word);
                        }
                        else finalScore += this.sentimentWords.get(output);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return finalScore;
    }
}