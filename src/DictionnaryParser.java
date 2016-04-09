import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Fabrice Servais (fabrice.servais@gmail.com)<br/>
 *         Date : 09/04/16
 */
public class DictionnaryParser {

    Map<String, Integer> sentimentMap;

    public DictionnaryParser() {
        this.sentimentMap = new HashMap<String, Integer>();
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
}
