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
	private HashMap<String, String> abbr;
	
	public TweetCleaner(){
	    stopwords = new HashMap<>();
	    abbr = new HashMap<>();
	    
		try{
		    File file = new File("stopwords.txt");
		    Scanner input = new Scanner(file);
		    
		    
		    
		    while(input.hasNextLine()){
		    	String line = input.nextLine();
		    	stopwords.put(line, line);
		    }
		   
		    File file2 = new File("abbreviations.txt");
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
		
		
		
		//Remove stopwords & replace abbreviations
		String[] words = clean.split("[ ][ ]*");
		StringBuilder build = new StringBuilder("");
		for(int i = 0; i < words.length; ++i){
			if(this.abbr.containsKey(words[i])){
				words[i] = this.abbr.get(words[i]); //Abbreviations replacment
			}
			if(!this.stopwords.containsKey(words[i].replaceAll(" ", ""))){ //Stopwords removal
				build.append(words[i]);
				build.append(" ");
			}
		}
		clean = build.toString();
		
		return clean;
	}

}

