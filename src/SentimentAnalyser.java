import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import twitter4j.Status;

import java.io.*;
import java.util.*;

/**
 * Main class used to classify a set of tweets, dividing them into three classes, negative, positive and neutrals, based
 * on a set of known positive and negative words and a given upper and lower bounds. In particular, positive words give
 * a score of 1 and negative give a score of -1 (neutrals give no score). Tweets whose final point is between lower and
 * upper bound are classified as neutral, those with score lower than the lower bound are classified as negative and
 * those with the score higher than the upper bound are classified as positive.
 *
 * Uses a set of constants:
 *
 *      DICTIONARY -> The name of a txt file containing a list of words and whether they are positive or negative
 *      MAX_TWEETS -> An integer representing the maximum number of tweets to be retrieved to perform the training
 *      UPPER_BOUND -> An integer used so that tweets with score bigger than UPPER_BOUND are flagged as positive
 *      LOWER_BOUND -> An integer (negative) so that tweets with score lower than LOWER_BOUND are flagged as negative
 *
 * @author Fabrice Servais (fabrice.servais@gmail.com)
 * @author Michele Imperiali
 * @author Laurent Vanosmael
 *
 * @since 23/04/2016
 */
public class SentimentAnalyser {


    private final String DICTIONARY = "sentiment-dict.txt";
    private final int MAX_TWEETS = 100;

    private final int UPPER_BOUND = 1;
    private final int LOWER_BOUND = -1;

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

    /**
     * Classifies a list of tweets and stores the results into an ouput file.
     *
     * @param appendResultsToEndOfFile whether to append results to the end of file or not
     */
    public void analyseTweets(boolean appendResultsToEndOfFile) {
        TweetCleaner tc = new TweetCleaner();
        long fromId = -1;
        List<Status> tweets = getTweets(fromId);
        ArrayList<String> cleanedTweets = tc.cleanTweets(getTweets());

        parseDictionary();

        try {
            FileWriter writer = new FileWriter(outputTrainingFileName, appendResultsToEndOfFile);
            int tweetIndex = 0;
            for(String tweet : cleanedTweets) {
                int score = score(tweet, 0);
                String originalTweet = tweets.get(tweetIndex).getText().replaceAll("[\n\t,]"," ");
                int tweetClass = getTweetClass(originalTweet, score, 0);
                writer.write(originalTweet + "," + tweetClass + "\n");
                tweetIndex++;
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve a list of tweets with a given hahstag
     *
     * @param maxId if different from -1 is the tweet ID from which we start our search
     * @return the list of found tweets
     */
    private List<Status> getTweets(long maxId){
        TwitterSearchEngine searchEngine = new TwitterSearchEngine();

        List<Status> tweets;
        tweets = searchEngine.searchTweet(hashtagToAnalyse, MAX_TWEETS, maxId);

        System.out.println("Number of tweets: " + tweets.size());
        for(Status tweet : tweets){
            System.out.println("[" + tweet.getId() + "]" + " :: " +tweet.getText() + "---");
        }
        System.out.println("========================");

        return tweets;
    }

    /**
     * Overrides the getTweets function in case no tweet ID is provided
     *
     * @return calls the classic getTweets function passing -1 as a parameter for maxId.
     */
    private List<Status> getTweets(){
        return getTweets(-1);
    }

    /**
     * Parse the words dictionary and stores it into an hashmap
     */
    private void parseDictionary(){
        DictionaryParser parser = new DictionaryParser();
        parser.parseFile(DICTIONARY);

        this.sentimentWords = parser.getSentimentMap();
    }

    /**
     * Compute a tweet score
     *
     * @param tweet a String representing the tweet content
     * @param mode 1 to use a Python script for words not contained in the dictionary, to retrieve their lemma
     *
     * @return an integer representing the tweet score
     */
    private int score(String tweet, int mode) {
        String[] words = tweet.split("[ ][ ]*");

        int finalScore = 0;

        for(String word : words){

            if (this.sentimentWords.containsKey(word)){//first we check if the word is contained in our dictionary
                finalScore += this.sentimentWords.get(word);
            }
            else{//if not we check through a Python program what's the lemma of the word, only if we are in mode 1
                if(mode == 1) {
                try {
                    Process p = Runtime.getRuntime().exec("python lemmatizer.py " + word);
                    p.waitFor();
                    BufferedReader stdInput = new BufferedReader(new
                            InputStreamReader(p.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new
                            InputStreamReader(p.getErrorStream()));

                    // read the output from the command
                    StringBuffer outputBuffer = new StringBuffer();
                    String line, output;
                    while ((line = stdInput.readLine()) != null) {
                        outputBuffer.append(line);
                    }

                    output = outputBuffer.toString();

                    if(output.length()>0){
                        System.out.println("Lemma of '" + word + "' is " + output);

                        //first we check that the python script was able to identify the meaning of the abbreviation
                        //this means that output has to be different from word
                        if(output.equals(word)){
                            //the script didn't resolve the word, so we have to add it to the dictionary
                            int clazz = DictionaryParser.addWordToMap(word);
                            int score = clazz;

                            this.sentimentWords.put(word, score);
                        }
                        if (!output.equals(word) && this.sentimentWords.containsKey(output)) {
                            finalScore += this.sentimentWords.get(output);
                        } else {
                            this.sentimentWords.put(output, 0); // Consider neutral
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int weight = DictionaryParser.addWordToMap(word);
                sentimentWords.put(word, weight);
                }
            }
        }

        return finalScore;
    }

    /**
     * Return the tweet class based on its score. If the tweet score is not classifiable cause it's higher than the lower
     * bound and lower than the upper bound, it asks the user to classify it.
     *
     * @param tweet the tweet content, used in case the tweet has to be manually classified by the user
     * @param score the tweet score
     * @param mode integer stating the function mode, with 1 asks for user input in case of unclassified tweets, otherwise
     *             it leaves them neutral
     * @return the tweet class 1 for positive, -1 for negative and 0 for neutrals
     */
    private int getTweetClass(String tweet, int score, int mode){
        if(score >= UPPER_BOUND)
            return 1; //the tweet score is higher than the upper bound: the tweet is positive
        else if(score <= LOWER_BOUND)
            return -1; //the tweet score is lower than the lower bound: the tweet is negative
        else{//the tweet has a neutral score
            if(mode == 1){//if we are in user input mode we ask to the user to classify the tweet
                Scanner reader = new Scanner(System.in);  // Reading from System.in
                System.out.println("Is this tweet positive (0) or negative (1), 2 to ignore: " + tweet);
                return reader.nextInt();
                }
            else return 0;//otherwise we leave the tweet as neutral
        }
    }

    /**
     * Based on the testing set of tweets, automatically classified, we lear our machine learning algorithm and
     * we then verify its performances
     *
     * @param testingSetPath the path to the file containing the testing set
     */
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

            String tweet = new TweetCleaner().cleanString(row.getKey());
            int outputClass = getTweetClass(tweet, score(tweet, 0), 0);

//            System.out.println("Analysing: --- " + row.getKey() + " --- (" + row.getValue() + ")");
//            System.out.println("---> Score found:" + outputClass);

            if (outputClass == 1) { // Positive
                if (row.getValue() == 0) {
                    nTruePositive++;
                } else {
                    nFalsePositive++;
                }
            } else if (outputClass == -1) { // Negative
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

    /**
     * Parse the testing set CSV into a map <Tweet, Class>
     * @param testingSetPath the path to the file containing the testing set of tweets
     * @return the map of classified tweets
     */
    private Map<String, Integer> getTestingSet(String testingSetPath) {
        int maxLines = 10, count = 0;

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

                int sentimentClass = record.get("sentiment").equals("0") ? -1 : 1; // 0 in test set is negative
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