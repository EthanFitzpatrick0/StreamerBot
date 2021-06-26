package memory;

import java.util.*;
import java.io.*;

public class Parser implements Serializable{

    private static final long serialVersionUID = -8856780667768139876L;
    private File directory;
    
    private ArrayList<String> tokens = new ArrayList<String>();
    private LinkedHashMap<String,LinkedHashMap<String,Integer>> tokenPairs = new LinkedHashMap<String,LinkedHashMap<String,Integer>>();
    
    private LinkedHashMap<String,Integer> initialFrequency = new LinkedHashMap<String,Integer>();
    
    //replace censored words from autosub output
    
    LinkedHashMap<String,String> censored = new LinkedHashMap<String,String>() {/**
         *
         */
        private static final long serialVersionUID = 2852739457885646239L;

    {
        put("a**", "ass");
        put("a******", "asshole");
        put("b****", "bitch");
        put("b*****", "bitchy");
        put("b*******", "bitching");
        put("b******", "bitchin'");
        put("c***", "cunt");
        put("f***", "fuck");
        put("f*****", "fuckin'");
        put("f******", "fucking");
        put("i'm", "I'm");
        put("i", "I");
        put("m***********", "motherfuckin'");
        put("m************", "motherfucking");
        put("p****", "pussy");
        put("s***", "shit");
        put("s*****", "shitty");
        put("s*******", "shitting");
        put("s******", "shittin'");
    }};

    public Parser(File directory) {
        this.directory = directory;
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

    //retrieves data from all files in directory
    public List<String> getData(List<String> parsedFiles) throws IOException {

        boolean changed = false; //keep track of whether memory was changed
        for(File file : directory.listFiles()) {
            if(!parsedFiles.contains(file.getName())) {
                changed = true;
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
                parsedFiles.add(file.getName());
            }
        }
        if(changed) {
            initialFrequency = MapUtil.sortByValue(initialFrequency); //sort map of words frequently at beginning of sentences
            for(String word : tokenPairs.keySet()) {
                tokenPairs.replace(word, MapUtil.sortByValue(tokenPairs.get(word))); //sort all tokens' maps by frequency of occurrence
            }
        }
        return parsedFiles;
    }

    //pairs line's tokens together
    private void pairTokens(List<String> tokens) {
        if(initialFrequency.containsKey(tokens.get(0))) initialFrequency.put(tokens.get(0), initialFrequency.get(tokens.get(0)) + 1);
        else initialFrequency.put(tokens.get(0), 1);
        for(int i = 0; i < tokens.size() - 1; i++){
            String currentWord = tokens.get(i);
            if(tokenPairs.get(currentWord) == null) { //check to see if key already has a frequency map
                LinkedHashMap<String,Integer> newTokenFrequencyList = new LinkedHashMap<String,Integer>(); //calling it a "list" because I'm treating it as a list of strings that happens to keep track of their frequency of usage
                newTokenFrequencyList.put(tokens.get(i+1),1); //add new token with frequency 1
                tokenPairs.put(currentWord, newTokenFrequencyList); //add new frequency list to token mapping
            }
            else { //just add value to the key's list if already present
                if(tokenPairs.get(currentWord).containsKey(tokens.get(i+1))) {
                    tokenPairs.get(currentWord).put(tokens.get(i+1), tokenPairs.get(currentWord).get(tokens.get(i+1))+1);
                }
                else tokenPairs.get(currentWord).put(tokens.get(i+1), 1);
            }
        }

    }

    /*
        autosub, or more likely the Google API, censors swear words. This is unnecessary for our purposes,
        and it also messes with the punctuation removal regex
    */
    private void uncensorTokens(List<String> line) {
        makeLowercase(line);
        for(String censoredSwear: censored.keySet()){
            Collections.replaceAll(line, censoredSwear, censored.get(censoredSwear));
        }
    }
}