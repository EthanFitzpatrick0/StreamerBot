package memory;

import java.util.*;
import java.io.*;

public class Brain {
    
    
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

    public int getMemorySize(){
        return memorySize;
    }

    public String createSentence(){
        String sentence, currentWord;
        int sentenceLength, min, max, maxSentenceLength;
        sentence = "";
        min = 10;
        max = 20;
        maxSentenceLength = (int) ((Math.random() * (max - min)) + min); //max length the sentence can be
        
        currentWord = selectWord(initialFrequency);
        sentence = currentWord;
        sentenceLength = 1;
        
        while(sentenceLength < maxSentenceLength && memory.get(currentWord) != null) { //keeps going until it hits the max sentence length OR it runs out of word connections
            
            currentWord = selectWord(memory.get(currentWord));
            sentence += " " + currentWord;
            sentenceLength++;   
        }

        sentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1) + "."; //capitalize first letter and add a period
        return sentence;
    }

    private String selectWord(LinkedHashMap<String,Integer> list){
        List<String> currentList = new ArrayList<>(list.keySet());
        int weightedSize = list.values().stream().reduce(0, Integer::sum);
        Random rng = new Random();
        int bound;
        int weightedIndex = 0;
        if(weightedSize < 4) bound = 1;
        else if(weightedSize < 5) bound = 3;
        else if(weightedSize < 10) bound = 4;
        else if(weightedSize < 20) bound = 5;
        else bound = 6;

        int frequency = rng.nextInt(bound);
        //0 = full size, 1: bottom 75%, 2: top 25%, 3: top 20%, 4: top 10%, 5: top 5%
        switch(frequency) {
            case 0: //full weightedSize
                weightedIndex = rng.nextInt(weightedSize);
                break;
            case 1: //bottom 75%
                weightedIndex = (int) ((Math.random() * (weightedSize - (weightedSize/4)) + (weightedSize/4)));
                break;
            case 2: //top 25%
                weightedIndex = rng.nextInt(weightedSize/4);
                break;
            case 3: //top 20%
                weightedIndex = rng.nextInt(weightedSize/5);
                break;
            case 4: //top 10%
                weightedIndex = rng.nextInt(weightedSize/10);
                break;
            case 5: //top 5%
                weightedIndex = rng.nextInt(weightedSize/20);
                break;
            default: //full weightedSize (should never reach)
                weightedIndex = rng.nextInt(weightedSize);
                break;
        }
        int currentWeight = weightedIndex;
        for (String word : currentList) {
            currentWeight -= list.get(word);
            if(currentWeight <= 0) return word;
        }
        return null;
    }
}
