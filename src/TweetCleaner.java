import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import twitter4j.*;

/**
 * Class used to clean tweets. It performs the following cleaning activities:
 *
 * 		- Remove URLs
 * 		- Remove punctuation and numbers
 * 		- Remove stop words
 * 		- Replace abbreviations
 *
 * Stop words and abbreviations are stored in two files whose names are stored in the variables STOPWORDS and ABBREVIATIONS.
 *
 */
public class TweetCleaner {
	//StopWords from Longlist : http://www.ranks.nl/stopwords

	private final String STOPWORDS = "stopwords.txt";
	private final String ABBREVIATIONS = "abbreviations.txt";

	private HashMap<String, String> stopwords;
	private HashMap<String, String> abbr;

	/**
	 * Constructor: parses the stopwords and abbreviations files, saving the entries in two ad hoc hash maps
	 */
	public TweetCleaner(){
	    stopwords = new HashMap<>();
	    abbr = new HashMap<>();
		try{
			//parse the stopwords file
		    File file = new File(STOPWORDS);
		    Scanner input = new Scanner(file);
		    while(input.hasNextLine()){
		    	String line = input.nextLine();
		    	stopwords.put(line, line);
		    }
		    //parse the abbreviations file
		    File file2 = new File(ABBREVIATIONS);
		    input = new Scanner(file2);
		    while(input.hasNextLine()){
		    	String line = input.nextLine();
		    	line.toLowerCase();
		    	String[] pair = line.split("[ ][ ]*");
		    	if(pair.length == 2)
		    		abbr.put(pair[0], pair[1]);
		    }
		}catch(FileNotFoundException e){e.printStackTrace();}
	}

	/**
	 * Clean a list of tweets
	 *
	 * @param tweets list of tweets to be cleaned
	 * @return arraylist of cleaned strings
     */
	public ArrayList<String> cleanTweets(List<Status> tweets){
        ArrayList<String> cleanArray = new ArrayList<>();
		for(Status tweet : tweets){
        	cleanArray.add(cleanString(tweet.getText()));
        }
		return cleanArray;
	}

	/**
	 * Clean a single string, removing URLs, stop words, spaces and punctuation and replacing abbreviations with their meaning.
	 *
	 * @param s String to be cleaned
	 * @return cleaned result
     */
	public String cleanString(String s){
		String clean = s.toLowerCase();
		
		//Remove url 
		clean = clean.replaceAll("\\b(https?|ftp|file)://[-a-z0-9+&@#/%?=~_|!:,.;]*[-a-z0-9+&@#/%=~_|]", "");
		
		//Remove punctuation & numbers
		clean = clean.replaceAll("[^a-z ]", "");

		//Remove stop words & replace abbreviations
		String[] words = clean.split("[ ][ ]*");
		StringBuilder build = new StringBuilder("");
		for(int i = 0; i < words.length; ++i){
			if(this.abbr.containsKey(words[i])){
				words[i] = this.abbr.get(words[i]); //Abbreviations replacement
			}
			if(!this.stopwords.containsKey(words[i].replaceAll(" ", ""))){ //Stop words removal
				build.append(words[i]);
				build.append(" ");
			}
		}
		clean = build.toString();
		return clean;
	}

}

