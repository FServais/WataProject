/**
 *
 * Retrieve a set of tweets, classifies them automatically into positive, negative and neutral and saves the result
 * into an output csv file. The same file is then used to train a machine learning algorithm and automatically classify
 * other tweets. The results are investigated to identify true/false positives and negatives.
 *
 * For the purpose of this project the retrieved tweets are referring to the recent scandal of Panama Papers.
 * The used function tries to get rid of all tweets published by news channels, since they tend to be very hard to clas-
 * -sify, obviously because news are supposed to be impartial.
 * Also, we have written the code to obtain a training set but we have finally used a much bigger set provided by Standford
 * university. This way we had much more data to train our algorithm and get better results.
 *
 * @author Fabrice Servais (fabrice.servais@gmail.com)
 * @author Michele Imperiali
 * @author Laurent Vanosmael
 *
 * @since 23/04/2016
 *
 */
public class Main {

    private static final String HASHTAG = "#iphoneSE";
    private static final String TRAINING_OUTPUT_FILE = "training_data_file.csv";
    private static final String EVALUATION_INPUT_FILE = "training.1600000.processed.noemoticon.csv";

    public static void main(String[] args) {
        SentimentAnalyser analyser = new SentimentAnalyser(HASHTAG, TRAINING_OUTPUT_FILE);
        analyser.analyseTweets(true);
        //analyser.evaluateMethod(EVALUATION_INPUT_FILE);
    }

}
