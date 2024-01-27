package csx55.overlay.util;

import java.util.Scanner;
import java.util.StringTokenizer;

import csx55.overlay.node.Node;

public class CLIHandler {
    Scanner scan;
    // Node node;

    public CLIHandler(){
       this.scan = new Scanner(System.in);
    }
    
    public void readInstructions(){
        String instruction = scan.nextLine(); // need parser
        String[] result = instruction.split("\\s");

        System.out.println("Instruction: " + result[0]);

        switch(result[0]){
            case "exit":
                System.exit(0);
                break;
            case "setup-overlay":
                if(result.length > 1){
                    int numberOfConnections = Integer.parseInt(result[1]);
                    System.out.println("number of connections: " + numberOfConnections);
                    // node.setUpOverlay();
                    // Parse input -- to produce number of connections
                    OverlayCreator overlayCreator = new OverlayCreator(numberOfConnections);
                    break;
                }
                else{
                    System.err.println("Incorrect Instruction! Please try again!");
                }
                break;
            default:
                System.out.println("Incorrect Instruction. Please try again.");
        }
    }
}
