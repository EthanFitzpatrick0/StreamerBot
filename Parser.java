package StreamerBot;

import java.util.*;
import java.io.*;

public class Parser {

    private File file;
    private ArrayList<String> tokens = new ArrayList<String>();
    private LinkedHashMap<String,LinkedHashMap<String,Integer>> tokenPairs = new LinkedHashMap<String,LinkedHashMap<String,Integer>>();
    
    //mainly to replace censored words from pyTranscriber output
    @SuppressWarnings("serial") 
    LinkedHashMap<String,String> censored = new LinkedHashMap<String,String>() {{
        put("f***", "fuck");
        put("f******", "fucking");
        put("m***********", "motherfuckin");
        put("m************", "motherfucking");
        put("s***", "shit");
        put("s*****", "shitty");
        put("p****", "pussy");
        put("c***", "cunt");
        put("b****", "bitch");
        put("a**", "ass");
        put("i'm", "I'm");
        put("i", "I");
    }};

    private LinkedHashMap<String,Integer> initialFrequency = new LinkedHashMap<String,Integer>();
    public static void main(String[] args){
        File file = new File("textFile.txt");
        Parser parser = new Parser(file);
        try{
            parser.getData();
        }
        catch(IOException ex){
            System.out.println("Error reading file in Parser.");
        }
    }

    public Parser(File file) {
        this.file = file;
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public LinkedHashMap<String,LinkedHashMap<String,Integer>> getTokenPairs() {
        return tokenPairs;
    }

    public LinkedHashMap<String,Integer> getInitialFrequency(){
        return initialFrequency;
    }
    
    public static void makeLowercase(List<String> strings) {
        ListIterator<String> iterator = strings.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next().toLowerCase());
        }
    }

    //retrieves data from txt file
    public void getData() throws IOException {
        Scanner scanner = new Scanner(file);
        while(scanner.hasNext()){ //split into separate words
            List<String> line = Arrays.asList(scanner.nextLine().split(" "));
            uncensorTokens(line);
            pairTokens(line); //pair tokens by each line so that end of sentence isn't paired with beginning of next sentence
        }
        scanner.close();
        for(int i = 0; i < tokens.size(); i++){ //remove punctuation
            tokens.set(i,(tokens.get(i).replaceAll("[\\p{Punct}&&[^']]|(?<![a-zA-Z])'|'(?![a-zA-Z])","")));
        }
    }

    //pairs line's tokens together
    private void pairTokens(List<String> tokens) {
        if(initialFrequency.containsKey(tokens.get(0))) initialFrequency.put(tokens.get(0), initialFrequency.get(tokens.get(0)) + 1);
        else initialFrequency.put(tokens.get(0), 1);
        for(int i = 0; i < tokens.size() - 1; i++){

            if(tokenPairs.get(tokens.get(i)) == null) { //check to see if key already has a frequency map
                LinkedHashMap<String,Integer> newTokenFrequencyList = new LinkedHashMap<String,Integer>(); //calling it a "list" because I'm treating it as a list of strings that happens to keep track of their frequency of usage
                newTokenFrequencyList.put(tokens.get(i+1),1); //add new token with frequency 1
                tokenPairs.put(tokens.get(i), newTokenFrequencyList); //add new frequency list to token mapping
            }
            else { //just add value to the key's list if already present
                if(tokenPairs.get(tokens.get(i)).containsKey(tokens.get(i+1))) {
                    tokenPairs.get(tokens.get(i)).put(tokens.get(i+1), tokenPairs.get(tokens.get(i)).get(tokens.get(i+1))+1);
                }
                else tokenPairs.get(tokens.get(i)).put(tokens.get(i+1), 1);
            }
        }
        initialFrequency = MapUtil.sortByValue(initialFrequency); //sort map of words frequently at beginning of sentences
        for(LinkedHashMap<String,Integer> map: tokenPairs.values()){ //sort all token's maps by frequency of occurrence
            map = MapUtil.sortByValue(map);
        }
    }

    //pyTranscriber, or more likely the Google API, censors swear words. This is unnecessary for our purposes,
    //and it also messes with the punctuation removal regex
    private void uncensorTokens(List<String> line) {
        makeLowercase(line);
        for(String censoredSwear: censored.keySet()){
            Collections.replaceAll(line, censoredSwear, censored.get(censoredSwear));
        }
    }
}