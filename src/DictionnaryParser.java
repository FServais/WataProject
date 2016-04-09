import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Fabrice Servais (fabrice.servais@gmail.com)<br/>
 *         Date : 09/04/16
 */
public class DictionnaryParser {

    final static String DICTIONARY_FILE = "sentiment-dict.txt";

    Map<String, Integer> sentimentMap;

    public DictionnaryParser() {
        this.sentimentMap = new HashMap<>();
    }

    public Map<String, Integer> getSentimentMap() {
        return sentimentMap;
    }

    public void parseFile(String filePath) {

        Scanner s = null;
        try {
            s = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (s.hasNextLine()){
            String line = s.nextLine();
            String[] wordsLine = line.split(" ");

            String wordLine = wordsLine[2];
            String[] wordSplit = wordLine.split("=");

            String word = wordSplit[1];

            String wordValueLine = wordsLine[5];
            String[] wordValueSplit = wordValueLine.split("=");

            String wordValue = wordValueSplit[1];

            if(wordValue.equals("positive")){
                this.sentimentMap.put(word, 1);
            }
            else if(wordValue.equals("negative")){
                this.sentimentMap.put(word, -1);
            }
            else{
                this.sentimentMap.put(word, 0);
            }
        }
        s.close();
    }

    public static int addWordToMap(String word){
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Word: " + word + " - Enter 1 for positive word, 2 for negative and 3 for neutral: ");
        int n = reader.nextInt(); // Scans the next token of the input as an int.
        String polarity;
        switch(n){
            case 1://positive word
                polarity = "positive";
                break;
            case 2://negative word
                polarity = "negative";
                break;
            default://neutral word
                polarity = "neutral";
                break;
        }
        try {
            BufferedWriter dictionaryFile = new BufferedWriter(new FileWriter(DICTIONARY_FILE, true));
            dictionaryFile.newLine();
            dictionaryFile.append("t=w l=1 word1=" + word + " p=v s=y priorpolarity=" + polarity);
            dictionaryFile.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return n;
    }
}
