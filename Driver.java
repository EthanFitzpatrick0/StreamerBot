package StreamerBot;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class Driver {
    
    public static void main(String[] args){
        
        if(args.length == 0) {
            System.out.println("Must input a file name/path.");
            System.exit(1);
        }

        Brain brain = null;
        
        File file = new File(args[0]);
        String fileName = file.getName();
        int indexOfUnderscore = fileName.lastIndexOf("_");
        if (indexOfUnderscore == -1) {
            System.out.println("Incorrect file name format. \nFile name must be in the format streamerName_date.txt");
            System.exit(1);
        }
        String streamer = fileName.substring(0, indexOfUnderscore);
        Path serFolder = Paths.get(file.getAbsolutePath()).getParent();
        String serFilePath = serFolder + "\\Ser\\" + streamer + ".ser";    
        
        try {
            File serFile = new File(serFilePath);
            if (serFile.exists()) { //deserialize if .ser file exists
                FileInputStream serFileInputStream = new FileInputStream(serFilePath);
                ObjectInputStream objectInStream = new ObjectInputStream(serFileInputStream);
                
                brain = (Brain)objectInStream.readObject();

                serFileInputStream.close();
                objectInStream.close();
            }
            
        }
        catch(IOException ex) {
            System.out.println("Deserialization error.");
            System.exit(1);
        }
        catch(ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException occurred.");
            System.exit(1);
        }

        try {
            if(brain == null) brain = new Brain(file); //if no .ser file, create new brain
            else brain.addMemory(file); //otherwise add to existing brain
            Scanner input = new Scanner(System.in);
            boolean exit = false;
            String inputLine;
            while(exit == false) { //read user input
                inputLine = input.nextLine();
                if(inputLine.equals("exit")) exit = true;
                else if(inputLine.equals("help")) System.out.println("Press enter for a generated sentence.\n\tType \"exit\" to exit program.\n\tType \"help\" to generate this message.");
                else if(inputLine.equals("memory size")) System.out.println(brain.getMemorySize());
                else if(inputLine.equals("")) System.out.println(brain.createSentence());
                else System.out.println("Invalid input.\n\tPress enter for a generated sentence.\n\tType \"exit\" to exit program.\n\tType \"help\" to generate this message.");
            }
            input.close();
            
            FileOutputStream serFileOutStream = new FileOutputStream(serFilePath);
            ObjectOutputStream objectOutStream = new ObjectOutputStream(serFileOutStream);

            objectOutStream.writeObject(brain);

            objectOutStream.close();
            serFileOutStream.close();

            System.out.println("Brain successfully serialized.");
            System.exit(1);
        }
        catch(NullPointerException ex){
            System.out.println("Expected a file name to be passed via command line.");
            System.exit(1);
        }
        catch(IOException ex){
            System.out.println("Serialization error.");
            System.exit(1);
        }
    }
}
