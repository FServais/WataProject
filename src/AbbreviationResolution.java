import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Imperiali
 * @sine 09/04/2016.
 */
public class AbbreviationResolution {

    private static Map<String, String> abbreviations;

    public AbbreviationResolution(){
        abbreviations = new HashMap<String, String>();
    }

    /**
     * Checks if a string is an abbreviation and if so it returns the actual word, otherwise it returns null
     *
     * @param abbr the possible abbreviated string
     * @return if abbr is an abbreviation it returns the actual string, otherwise null
     */
    public static String resolveAbbreviation(String abbr){
        if(abbreviations.containsKey(abbr))
            return abbreviations.get(abbr);
        else return null;
    }
}
