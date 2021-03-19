import java.util.*;
import java.io.*;

public class Parser {

    private File file;
    private ArrayList<String> tokens = new ArrayList<String>();
    private HashMap<String,ArrayList<String>> tokenPairs = new HashMap<String,ArrayList<String>>();
    
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

    public HashMap<String,ArrayList<String>> getTokenPairs() {
        return tokenPairs;
    }

    public void getData() throws IOException {
        Scanner scanner = new Scanner(file);
        while(scanner.hasNext()){ //split into separate words
            tokens.addAll(Arrays.asList(scanner.nextLine().split(" ")));
        }
        scanner.close();
        for(int i = 0; i < tokens.size(); i++){ //remove punctuation and set to lowercae
            tokens.set(i,(tokens.get(i).replaceAll("[\\p{Punct}&&[^']]|(?<![a-zA-Z])'|'(?![a-zA-Z])","").toLowerCase()));
            //tokens.set(i, (tokens.get(i).replaceAll("\\p{P}","").toLowerCase()));
        }
        pairTokens();
    }

    private void pairTokens() {
        for(int i = 0; i < tokens.size() - 1; i++){
            if(tokenPairs.get(tokens.get(i)) == null) { //check to see if key already has a list
                ArrayList<String> newTokenList = new ArrayList<String>();
                newTokenList.add(tokens.get(i+1));
                tokenPairs.put(tokens.get(i), newTokenList); //add new list to token mapping
            }
            else { //just add value to the key's list if already present
                tokenPairs.get(tokens.get(i)).add(tokens.get(i+1));
            }
        }
    }
}