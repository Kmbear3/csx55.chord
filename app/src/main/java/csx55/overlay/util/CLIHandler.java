package csx55.overlay.util;

import java.util.Scanner;

public class CLIHandler {
    Scanner scan;

    public CLIHandler(){
       this.scan = new Scanner(System.in);
    }
    
    public void readInstructions(){
        while(true){
            String instruction = scan.nextLine();
            switch(instruction){
                case "exit":
                    System.exit(0);
            }
        }
    }
}
