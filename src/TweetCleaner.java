import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import twitter4j.*;

public class TweetCleaner {
	//StopWords from Longlist : http://www.ranks.nl/stopwords

	private HashMap<String, String> stopwords;
	
	public TweetCleaner(){
		try{
		    File file = new File("stopwords.txt");
		    Scanner input = new Scanner(file);
		    
		    stopwords = new HashMap<>();
		    
		    while(input.hasNextLine()){
		    	String line = input.nextLine();
		    	stopwords.put(line, line);
		    }
		}catch(FileNotFoundException e){e.printStackTrace();}
	}

	
	public ArrayList<String> cleanTweets(List<Status> tweets){
        ArrayList<String> cleanArray = new ArrayList<>();
		for(Status tweet : tweets){
        	cleanArray.add(cleanString(tweet.getText()));
        }
		return cleanArray;
	}
	
	public String cleanString(String s){
		String clean = s.toLowerCase();
		
		//Remove url 
		clean = clean.replaceAll("\\b(https?|ftp|file)://[-a-z0-9+&@#/%?=~_|!:,.;]*[-a-z0-9+&@#/%=~_|]", "");
		
		//Remove punctuation & numbers
		clean = clean.replaceAll("[^a-z ]", "");
		
		//Remove stopwords
		String[] words = clean.split(" ");
		StringBuilder build = new StringBuilder("");
		for(int i = 0; i < words.length; ++i){
			if(!this.stopwords.containsKey(words[i].replaceAll(" ", ""))){
				build.append(words[i]);
				build.append(" ");
			}
		}
		
		return build.toString();
	}

}

