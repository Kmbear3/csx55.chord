package csx55.overlay.util;

import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

import csx55.overlay.node.MessagingNode;
import csx55.overlay.node.Node;
import csx55.overlay.node.Registry;
import csx55.overlay.wireformats.Deregister;
import csx55.overlay.wireformats.LinkWeights;
import csx55.overlay.wireformats.TaskInitiate;

public class CLIHandler {
    private Scanner scan;
    private Registry registry;
    private MessagingNode node;
    private OverlayCreator overlayCreator; 


    public CLIHandler(Registry registry){
       this.scan = new Scanner(System.in);
       this.registry = registry;
    }

    public CLIHandler(MessagingNode messagingNode){
        this.scan = new Scanner(System.in);
        this.node = messagingNode;
    }

    public void readInstructionsRegistry(){
        String instruction = scan.nextLine(); // need parser
        String[] result = instruction.split("\\s");

        System.out.println("Instruction: " + result[0]);

        switch(result[0]){
            case "exit":
                System.exit(0);
                break;
            case "setup-overlay":
            case "so":
                if(result.length > 1){
                    int numberOfConnections = Integer.parseInt(result[1]);
                    System.out.println("number of connections: " + numberOfConnections);
                    this.overlayCreator = new OverlayCreator(this.registry, numberOfConnections);
                    break;
                }else{
                    int numberOfConnections = 4;
                    System.out.println("number of connections: " + numberOfConnections);
                    this.overlayCreator = new OverlayCreator(this.registry, numberOfConnections);
                }
                break;
            case "start":
                if(result.length > 1) {
                    int numberOfRounds = Integer.parseInt(result[1]);
                    TaskInitiate taskInitiate = new TaskInitiate(numberOfRounds);
                    registry.onEvent(taskInitiate, null);
                }else {
                    System.out.println("Incorrect Instruction! please specify number of rounds.");
                }
                break;
            case "sendw":
            case "send-overlay-link-weights":
                if(overlayCreator != null){
                    LinkWeights linkWeights = new LinkWeights(this.overlayCreator.getConnections(), this.overlayCreator.createLinkInfo());
                    registry.sendAllNodes(linkWeights);                  
                }
                else{
                    System.out.println("Cannot Send Link Weights. Overlay is unconstructed.");
                }
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
            case "poke-neighbors":
                MessageSender sendMessages = new MessageSender(node);
                sendMessages.sendPoke();
               break;
            case "exit-overlay":
                sendDeregisterRequest(StatusCodes.EXIT_OVERLAY);
                break;
            case "deregister":
                sendDeregisterRequest(StatusCodes.DEREGISTER);
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
