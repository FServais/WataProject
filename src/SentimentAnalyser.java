import twitter4j.Status;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author Fabrice Servais (fabrice.servais@gmail.com)<br/>
 *         Date : 09/04/16
 */
public class SentimentAnalyser {

    final String DICTIONARY = "sentiment-dict.txt";
    final String HASHTAG = "#BatmanVsSuperman";
    final int MAX_TWEETS = 100;
    final int UPPER_BOUND = 2;
    final int LOWER_BOUND = -2;
    final String TRAINING_DATA_FILE = "training_data_file.csv";

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

        try {
            FileWriter writer = new FileWriter(TRAINING_DATA_FILE);
            for(String tweet : cleanedTweets) {
                int score = score(tweet);
                scoreMap.put(tweet, score);
                int tweetClass = getTweetClass(tweet, score);
                if (tweetClass != 0 && tweetClass != 1)
                    continue;
                writer.write(tweet + "," + tweetClass + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
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
//            else{//if not we check through a Python program what's the lemma of the word
//                int weight = 0;
//                try {
//                    Process p = Runtime.getRuntime().exec("python3 lemmatizer.py " + word);
//                    BufferedReader stdInput = new BufferedReader(new
//                            InputStreamReader(p.getInputStream()));
//
//                    BufferedReader stdError = new BufferedReader(new
//                            InputStreamReader(p.getErrorStream()));
//
//                    // read the output from the command
//                    String output = stdInput.readLine();
//
//                    if(output.length()>0){
//                        //first we check that the python script was able to identify the meaning of the abbreviation
//                        //this means that output has to be different from word
//                        if(output.equals(word)){
//                            //the script didn't resolve the word, so we have to add it to the dictionary
//                            weight = DictionnaryParser.addWordToMap(word);
//                            sentimentWords.put(word, weight);
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
        }

        return finalScore;
    }

    private int getTweetClass(String tweet, int score){
        if(score >= UPPER_BOUND)
            return 0;
        else if(score <= LOWER_BOUND)
            return 1;
        else{
            Scanner reader = new Scanner(System.in);  // Reading from System.in
            System.out.println("Is this tweet positive (0) or negative (1) (or ignore (2)): " + tweet);
            return reader.nextInt();
        }
    }
}