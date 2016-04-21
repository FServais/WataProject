
public class Main {

    public static void main(String[] args) {
        SentimentAnalyser analyser = new SentimentAnalyser("#panamapapers", "training_data_file.csv");
        analyser.analyseTweets(false);
//        analyser.evaluateMethod("scoring/training.1600000.processed.noemoticon.csv");
    }

}
