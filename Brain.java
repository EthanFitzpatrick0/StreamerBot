import java.util.*;
import java.io.*;

public class Brain {
    
    private Parser parser;
    private HashMap<String,ArrayList<String>> memory;
    private List<String> memoryArray;
    private int memorySize;
    
    public Brain(File file) {
        try{
            parser = new Parser(file);
            parser.getData();
            memory = parser.getTokenPairs();
            memoryArray = new ArrayList<String>(memory.keySet());
            memorySize = memory.size();
        }
        catch(IOException ex) {
            System.out.println("Error reading file within Brain.");
        }
    }

    public int getMemorySize(){
        return memorySize;
    }
    
    //todo
    //2) prioritization system for words that show up more often

    public String createSentence(){
        ArrayList<String> currentWordConnections;
        String sentence, currentWord;
        int sentenceLength, min, max, maxSentenceLength;
        sentence = "";
        min = 5;
        max = 20;
        maxSentenceLength = (int) ((Math.random() * (max - min)) + min); //max length the sentence can be
        Random rng = new Random();
        
        currentWord = memoryArray.get(rng.nextInt(memoryArray.size()));
        sentence = currentWord;
        sentenceLength = 1;
        
        while(sentenceLength < maxSentenceLength && memory.get(currentWord) != null) { //keeps going until it hits the max sentence length OR it runs out of word connections
            
            currentWordConnections = memory.get(currentWord);
            currentWord = currentWordConnections.get(rng.nextInt(currentWordConnections.size()));
            sentence += " " + currentWord;
            sentenceLength++;   
        }

        sentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1) + "."; //capitalize first letter and add a period
        return sentence;
    }

}
