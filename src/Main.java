
public class Main {

    public static void main(String[] args) {
        SentimentAnalyser analyser = new SentimentAnalyser("#panamapapers", "training_data_file.csv");
        analyser.analyseTweets();
    }

}
