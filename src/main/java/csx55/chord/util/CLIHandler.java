package csx55.chord.util;

import java.io.IOException;
import java.util.Scanner;

import csx55.chord.Discovery;
import csx55.chord.Peer;
import csx55.chord.wireformats.Deregister;

public class CLIHandler {
    private Scanner scan;
    private Discovery discovery;
    private Peer node;


    public CLIHandler(Discovery discovery){
       this.scan = new Scanner(System.in);
       this.discovery = discovery;
    }

    public CLIHandler(Peer messagingNode){
        this.scan = new Scanner(System.in);
        this.node = messagingNode;
    }

    public void readInstructionsRegistry(){
        String instruction = scan.nextLine(); // need parser
        String[] result = instruction.split("\\s");

        // System.out.println("Instruction: " + result[0]);

        switch(result[0]){
            case "peer-nodes":
                discovery.printPeerNodes();
                break;
            default:
                System.out.println("Incorrect Instruction. Please try again.");
        }
    }

    public void readInstructionsMessagingNode(){
        String instruction = scan.nextLine(); // need parser
        String[] result = instruction.split("\\s");

        switch(result[0]){
            case "exit":
                node.exitGracefully();
                break;
            case "upload":
                if(result.length != 2){
                    System.err.println("Incorrect upload command - please try again");
                }
                else{
                    node.uploadFile(result[1]);
                }
                break;
            case "files":
                node.printFiles();
                break; 
            case "finger-table":
                node.printFingerTable();
                break;
            case "neighbors":
                node.printNeighbors();
                break;
            case "download":
                if(result.length != 2){
                    System.err.println("Incorrect download command - please try again");
                }
                else{
                    node.downloadFile(result[1]);
                }
                break;
            default:
                System.out.println("Incorrect Instruction. Please try again.");
        }
    }

    private void sendDeregisterRequest(int status){
        try {

            Deregister deregister = new Deregister(node.getMessagingNodeIP(), node.getMessagingNodePort(), status);
            node.sendRegistryMessage(deregister);
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
