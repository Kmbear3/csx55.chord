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
                System.exit(0);
                break;
            case "exit-overlay":
                sendDeregisterRequest(StatusCodes.EXIT_OVERLAY);
                break;
            case "deregister":
                sendDeregisterRequest(StatusCodes.DEREGISTER);
                break;
            case "finger-table":
                node.printFingerTable();
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
