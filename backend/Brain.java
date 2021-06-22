package backend;

import java.util.*;
import java.io.*;

public class Brain implements Serializable {
    
    private static final long serialVersionUID = 1174424660568316968L;
    private Parser parser;
    private LinkedHashMap<String,LinkedHashMap<String,Integer>> memory;
    List<String> memoryArray;
    private int memorySize;
    private LinkedHashMap<String,Integer> initialFrequency;
    List<String> initialFrequencyArray;
    
    public Brain(File directory) {
        try{
            parser = new Parser(directory);
            parser.getData();
            memory = parser.getTokenPairs();
            memoryArray = new ArrayList<String>(memory.keySet());
            memorySize = memory.size();
            initialFrequency = parser.getInitialFrequency();
            initialFrequencyArray = new ArrayList<String>(initialFrequency.keySet());
        }
        catch(IOException ex) {
            System.out.println("Error reading from directory within Brain.");
            System.exit(1);
        }
    }

    public void addMemory(File directory) {
        try{
            parser = new Parser(directory);
            parser.getData();
            memory.putAll(parser.getTokenPairs());
            memoryArray.addAll(memory.keySet());
            memorySize = memory.size();
            initialFrequency = parser.getInitialFrequency();
            initialFrequencyArray.addAll(initialFrequency.keySet());
        }
        catch(IOException ex) {
            System.out.println("Error reading from directory within Brain.");
            System.exit(1);
        }
    } 


    public int getMemorySize(){
        return memorySize;
    }
    
    //todo
    //2) prioritization system for words that show up more often

    public String createSentence(){
        List<String> currentWordArray = new ArrayList<>(parser.getWordFrequency().keySet());
        String sentence, currentWord;
        int sentenceLength, min, max, maxSentenceLength;
        sentence = "";
        min = 5;
        max = 20;
        maxSentenceLength = (int) ((Math.random() * (max - min)) + min); //max length the sentence can be
        
        currentWord = initialFrequencyArray.get(selectIndex(initialFrequencyArray.size()));
        sentence = currentWord;
        sentenceLength = 1;
        
        while(sentenceLength < maxSentenceLength && memory.get(currentWord) != null) { //keeps going until it hits the max sentence length OR it runs out of word connections
            
            
            currentWordArray = new ArrayList<String>(memory.get(currentWord).keySet());
            currentWord = currentWordArray.get(selectIndex(currentWordArray.size()));
            sentence += " " + currentWord;
            sentenceLength++;   
        }

        sentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1) + "."; //capitalize first letter and add a period
        return sentence;
    }

    private int selectIndex(int size){
        Random rng = new Random();
        if(size < 4) return rng.nextInt(size); //if only 3 connections or less, just return random word, ignoring frequency
        int frequent = rng.nextInt(1); //0 for top 25%, 1 for bottom 75%
        int quarter = size/4;
        int index = 0;

        if(frequent == 0) { //select an index from the first quarter of indexes
            index = rng.nextInt(quarter);
        }
        else { //select an index from bottom three quarters of indexes
            index = (int) ((Math.random() * (size - quarter) + quarter));
        }
        
        return index;
    }
}
