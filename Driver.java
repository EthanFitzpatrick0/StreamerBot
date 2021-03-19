import java.io.*;

public class Driver {
    
    public static void main(String[] args){

        try { 
            File file = new File("textFile.txt");
            Brain brain = new Brain(file);
            System.out.println(brain.createSentence());
        }
        catch(NullPointerException ex){
            System.out.println("Expected a file name to be passed via command line.");
            System.exit(0);
        }
    }
}
