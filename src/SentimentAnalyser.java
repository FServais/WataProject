import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import twitter4j.Status;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Fabrice Servais (fabrice.servais@gmail.com)<br/>
 *         Date : 09/04/16
 */
public class SentimentAnalyser {

    final String DICTIONARY = "sentiment-dict.txt";
    final int MAX_TWEETS = 20;

    final int UPPER_BOUND = 1;
    final int LOWER_BOUND = -11;

    private String hashtagToAnalyse;
    private String outputTrainingFileName;

    Map<String, Integer> sentimentWords;
    TweetCleaner cleaner;

    public SentimentAnalyser(String hashtag, String output_filename) {
        this.sentimentWords = new HashMap<>();
        this.cleaner = new TweetCleaner();

        this.hashtagToAnalyse = hashtag;
        this.outputTrainingFileName = output_filename;
    }

    public void analyseTweets() {
        TweetCleaner tc = new TweetCleaner();
        List<Status> tweets = getTweets();
        ArrayList<String> cleanedTweets = tc.cleanTweets(getTweets());

        parseDictionary();

        // Give score to each tweet
//        Map<String, Integer> scoreMap = new HashMap<>();

        try {
            FileWriter writer = new FileWriter(outputTrainingFileName);
            int tweetIndex = 0;
            for(String tweet : cleanedTweets) {
                int score = score(tweet);
//                scoreMap.put(tweet, score);
                String originalTweet = tweets.get(tweetIndex).getText().replaceAll("[\n\t,]"," ");
                int tweetClass = getTweetClass(originalTweet, score);
                writer.write(originalTweet + "," + tweetClass + "\n");
                tweetIndex++;
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
        tweets = searchEngine.searchTweet(hashtagToAnalyse, MAX_TWEETS, lastId);

        System.out.println("Number of tweets: " + tweets.size());
        for(Status tweet : tweets){
            System.out.println("[" + tweet.getId() + "]" + " :: " +tweet.getText() + "---");
        }
        System.out.println("========================");

        return tweets;
    }

    private void parseDictionary(){
        DictionaryParser parser = new DictionaryParser();
        parser.parseFile(DICTIONARY);

        this.sentimentWords = parser.getSentimentMap();
    }

    private int score(String tweet) {
        String[] words = tweet.split("[ ][ ]*");

        int finalScore = 0;

        for(String word : words){

            if (this.sentimentWords.containsKey(word)){//first we check if the word is contained in ou dictionary
                finalScore += this.sentimentWords.get(word);
            }
            else{//if not we check through a Python program what's the lemma of the word

                /**
                 * try {
                 Process p = Runtime.getRuntime().exec("python lemmatizer.py " + word);
                 BufferedReader stdInput = new BufferedReader(new
                 InputStreamReader(p.getInputStream()));

                 BufferedReader stdError = new BufferedReader(new
                 InputStreamReader(p.getErrorStream()));

                 // read the output from the command
                 String output = "";
                 while ((output.concat(stdInput.readLine())) != null) {
                 continue;
                 }

                 if(output.length()>0){
                 //first we check that the python script was able to identify the meaning of the abbreviation
                 //this means that output has to be different from word
                 if(output.equals(word)){
                 //the script didn't resolve the word, so we have to add it to the dictionary
                 DictionaryParser.addWordToMap(word);
                 }
                 }
                 } catch (IOException e) {
                 e.printStackTrace();
                 }


                int weight = DictionaryParser.addWordToMap(word);
                sentimentWords.put(word, weight);*/
            }
        }

        return finalScore;
    }

    private int getTweetClass(String tweet, int score){
        if(score >= UPPER_BOUND)
            return 0;
        else if(score <= LOWER_BOUND)
            return 1;
        /*else{
            Scanner reader = new Scanner(System.in);  // Reading from System.in
            System.out.println("Is this tweet positive (0) or negative (1), 2 to ignore: " + tweet);
            return reader.nextInt();
        }*/
        else return -1;
    }

    public void evaluateMethod(String testingSetPath) {
        System.out.println("Reading file...");
        Map<String, Integer> testset = getTestingSet(testingSetPath);

        int nTruePositive = 0, nTrueNegative = 0, nFalsePositive = 0, nFalseNegative = 0, nNeutral = 0;
        
        System.out.println("Predicting...");
        int count = 0;
        for(Map.Entry<String, Integer> row : testset.entrySet()) {
            count++;

            if (count % 1000 == 0) {
                System.out.print(count + "...");
            } else if(count % 10000 == 0) {
                System.out.print("\n");
            }

            int outputClass = score(new TweetCleaner().cleanString(row.getKey()));

//            System.out.println("Analysing: --- " + row.getKey() + " --- (" + row.getValue() + ")");
//            System.out.println("---> Score found:" + outputClass);

            if (outputClass == 0) { // Positive
                if (row.getValue() == 0) {
                    nTruePositive++;
                } else {
                    nFalsePositive++;
                }
            } else if (outputClass == 1) { // Negative
                if (row.getValue() == 0) {
                    nFalseNegative++;
                } else {
                    nTrueNegative++;
                }
            } else {
                nNeutral++;
            }
        }

        System.out.println("\n");
        int total = nTruePositive + nTrueNegative + nFalsePositive + nFalseNegative;
        System.out.println("======== Results: ");
        System.out.println("Tweets analysed: " + total);
        System.out.println("----------");
        System.out.println("Correctly assigned: " + (nTrueNegative + nTruePositive));
        System.out.println("Wrongly assigned: " + (nFalseNegative + nFalsePositive));
        System.out.println("Not assigned: " + nNeutral);
        System.out.println("----------");
        System.out.println("Details: ");
        System.out.println("True Positive: " + nTruePositive*100/total + "%");
        System.out.println("True Negative: " + nTrueNegative*100/total + "%");
        System.out.println("False Positive: " + nFalsePositive*100/total + "%");
        System.out.println("False Negative: " + nFalseNegative*100/total + "%");
    }

    private Map<String, Integer> getTestingSet(String testingSetPath) {
        int maxLines = 100000, count = 0;

        final String[] CSV_HEADER = {"sentiment","tweetid","created_at","search_query","user","tweet"};

        Map<String, Integer> testset = new HashMap<>();

        try {
            FileReader fileReader = new FileReader(testingSetPath);
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withHeader(CSV_HEADER));

            List<CSVRecord> csvRecords = csvParser.getRecords();

            for(CSVRecord record : csvRecords) {
                if (count > maxLines){
                    break;
                }
                count++;

                int sentimentClass = record.get("sentiment").equals("0") ? 1 : 0; // 0 in test set is negative
                testset.put(record.get("tweet"), sentimentClass);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return testset;
    }
}