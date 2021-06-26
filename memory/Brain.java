package memory;

import java.util.*;
import java.io.*;

public class Brain implements Serializable {
    
    private static final long serialVersionUID = 1174424660568316968L;
    
    private Parser parser;
    private ArrayList<String> parsedFiles;

    private LinkedHashMap<String,LinkedHashMap<String,Integer>> memory;
    List<String> memoryArray;
    private int memorySize;
    
    private LinkedHashMap<String,Integer> initialFrequency;
    List<String> initialFrequencyArray;
    
    public Brain(File directory) {
        try{
            parser = new Parser(directory);
            if(parsedFiles == null) parsedFiles = new ArrayList<String>();
            parser.getData(parsedFiles);
           
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
            if(parsedFiles == null) parsedFiles = new ArrayList<String>();
            parser.getData(parsedFiles);
           
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
        List<String> currentWordArray;
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
        int bound;
        int index = 0;
        if(size < 4) bound = 1;
        else if(size < 5) bound = 3;
        else if(size < 10) bound = 4;
        else if(size < 20) bound = 5;
        else bound = 6;

        int frequency = rng.nextInt(bound);
        //0 = full size, 1: bottom 75%, 2: top 25%, 3: top 20%, 4: top 10%, 5: top 5%
        switch(frequency) {
            case 0: //full size
                index = rng.nextInt(size);
                break;
            case 1: //bottom 75%
                index = (int) ((Math.random() * (size - (size/4)) + (size/4)));
                break;
            case 2: //top 25%
                index = rng.nextInt(size/4);
                break;
            case 3: //top 20%
                index = rng.nextInt(size/5);
                break;
            case 4: //top 10%
                index = rng.nextInt(size/10);
                break;
            case 5: //top 5%
                index = rng.nextInt(size/20);
                break;
            default: //full size (should never reach)
                index = rng.nextInt(size);
                break;
        }
        return index;
    }
}
