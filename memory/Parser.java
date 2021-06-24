package memory;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Parser implements Serializable{

    private static final long serialVersionUID = -8856780667768139876L;
    private File directory;
    
    private ArrayList<String> tokens = new ArrayList<String>();
    private LinkedHashMap<String,LinkedHashMap<String,Integer>> tokenPairs = new LinkedHashMap<String,LinkedHashMap<String,Integer>>();
    
    private LinkedHashMap<String,Integer> initialFrequency = new LinkedHashMap<String,Integer>();

    private LinkedHashMap<String,Integer> wordFrequency = new LinkedHashMap<String,Integer>();
    
    //mainly to replace censored words from pyTranscriber output
    
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
        put("c***", "cunt");
        put("f***", "fuck");
        put("f******", "fucking");
        put("i'm", "I'm");
        put("i", "I");
        put("m***********", "motherfuckin");
        put("m************", "motherfucking");
        put("p****", "pussy");
        put("s***", "shit");
        put("s*****", "shitty");
    }};

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
    
    public LinkedHashMap<String,Integer> getWordFrequency(){
        return wordFrequency;
    }

    public static void makeLowercase(List<String> strings) {
        ListIterator<String> iterator = strings.listIterator();
        while (iterator.hasNext()) {
            iterator.set(iterator.next().toLowerCase());
        }
    }

    //retrieves data from all files in directory
    public void getData() throws IOException {

        //file that contains list of transcriptions already added to brain's memory
        String streamerParsedFileName = "Records\\" + directory.getName() + "\\" + directory.getName() + "_parsed.txt";
        File streamerParsed = new File(streamerParsedFileName);
        streamerParsed.createNewFile(); //create file if not already there
        List<String> streamerParsedFiles = Files.readAllLines(Paths.get(streamerParsedFileName)); //list of transcriptions already added to brain's memory

        boolean changed = false; //keep track of whether memory was changed
        for(File file : directory.listFiles()) {
            if(!streamerParsedFiles.contains(file.getName())) {
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
                Writer w = new FileWriter(streamerParsed, true);
                w.append(file.getName());
                w.append(System.getProperty("line.separator"));
                w.close(); 
            }
        }
        if(changed) {
            wordFrequency = MapUtil.sortByValue(wordFrequency); //sort map of words' occurrence frequencies
            initialFrequency = MapUtil.sortByValue(initialFrequency); //sort map of words frequently at beginning of sentences
            for(LinkedHashMap<String,Integer> map: tokenPairs.values()) { //sort all token's maps by frequency of occurrence
                map = MapUtil.sortByValue(map);
            }
        }
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
                
                wordFrequency.put(currentWord, wordFrequency.get(currentWord) + 1); //record that word has appeared again
            }

            //keep track of word occurrence frequency
            if(wordFrequency.containsKey(currentWord)) wordFrequency.put(currentWord, wordFrequency.get(currentWord) + 1);
            else wordFrequency.put(currentWord, 1);
            if(i == tokens.size() - 2) { //need to keep track of last word as well
                String lastWord = tokens.get(i + 1);
                if(wordFrequency.containsKey(lastWord)) wordFrequency.put(lastWord, wordFrequency.get(lastWord) + 1);
                else wordFrequency.put(lastWord, 1);
            }
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