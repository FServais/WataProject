import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Parses a text file containing a list of words, with information about whether they are positive or negative.
 * It saves the result in an hash map <String, Integer> where the String is the word and the integer is a weight given to
 * the word: 1 for positive and -1 for negative ones.
 *
 * @author Fabrice Servais (fabrice.servais@gmail.com)
 * @author Michele Imperiali
 * @author Laurent Vanosmael
 *
 * @since 23/04/2016
 */
public class DictionaryParser {

    final static String DICTIONARY_FILE = "sentiment-dict.txt";

    Map<String, Integer> sentimentMap;

    public DictionaryParser() {
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

    /**
     * If a word doesn't belong to the original list we add it after asking to the user to say whether it's positive,
     * negative or neutral. It also returns the weight of the word.
     *
     * @param word word to be added to the dictionary
     * @return the word weight (1 for positive, -1 for negative, 0 for neutral)
     */
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

        int clazz;
        if (n == 1) {clazz = 1;}
        else if (n == 2) {clazz = -1;}
        else {clazz = 0;}

        return clazz;
    }
}
