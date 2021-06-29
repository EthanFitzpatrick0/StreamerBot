package memory;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class Driver {
    
    public static void main(String[] args){
        
        if(args.length == 0) {
            System.out.println("Must input a streamer name.");
            System.exit(1);
        }

        Brain brain = null;
    
        String streamer = args[0];
        File directory = new File(Paths.get("").toAbsolutePath().toString() + "\\Streamers\\" + streamer + "\\Transcriptions");
       
        if(!directory.exists() || !directory.isDirectory() || directory.list().length == 0) { //if no serialization OR autosub output exists, terminate
            System.out.println("No transcriptions available for this streamer.");
            System.exit(1);
        }

        try {
            brain = new Brain(directory);
            Scanner input = new Scanner(System.in);
            boolean exit = false;
            String inputLine;
            System.out.println("Press enter for a generated sentence.");
            while(exit == false) { //read user input
                inputLine = input.nextLine();
                if(inputLine.equals("exit")) exit = true;
                else if(inputLine.equals("help")) System.out.println("Press enter for a generated sentence.\n\tType \"exit\" to exit program.\n\tType \"help\" to generate this message.");
                else if(inputLine.equals("memory size")) System.out.println(brain.getMemorySize());
                else if(inputLine.equals("")) System.out.println(brain.createSentence());
                else System.out.println("Invalid input.\n\tPress enter for a generated sentence.\n\tType \"exit\" to exit program.\n\tType \"help\" to generate this message.");
            }
            input.close();
            System.exit(1);
        }
        catch(NullPointerException ex){
            System.out.println("Expected a streamer name to be passed via command line.");
            System.exit(1);
        }
    }
}
