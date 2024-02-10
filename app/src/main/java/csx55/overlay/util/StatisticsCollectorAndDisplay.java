package csx55.overlay.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import csx55.overlay.wireformats.Event;
import csx55.overlay.wireformats.TaskSummaryResponse;

public class StatisticsCollectorAndDisplay {
    // Add all summations

    private int sendTracker;  // number of messages sent
    private int receiveTracker;  // number of messages that were received
    private int relayTracker; // Number of messages that were relayed.
    private long sendSummation; // Sum of value that it has sent
    private long receiveSummation;  // Sum of the payloads that it has received

    ConcurrentHashMap<String, ArrayList<String>> nodes = new ConcurrentHashMap<>();

    private VertexList registry;


    public StatisticsCollectorAndDisplay(){
        this.sendTracker = 0;
        this.receiveTracker = 0;
        this.relayTracker = 0;
        this.sendSummation = 0;
        this.receiveSummation = 0;
    }

    public StatisticsCollectorAndDisplay(VertexList vertexList) {
        this.registry = vertexList;
    }

    public int getReceiveTracker(){
        return receiveTracker;
    }
    public int getRelayTracker(){
        return relayTracker;
    }

    public long getSendSum(){
        return sendSummation;
    }

    public long getReceivedSum(){
        return receiveSummation;
    }

    public int getSendTracker(){
        return sendTracker;
    }

    synchronized public void displayStats(){
        System.out.println("receiveTracker: " + receiveTracker);
        System.out.println("sendTracker: " + sendTracker);
        System.out.println("receivedSummation: " + receiveSummation);
        System.out.println("sendSummation: " + sendSummation);
        System.out.println("relayedMessages: " + relayTracker);
    }

    synchronized public void incrementSendTracker(){
        this.sendTracker = sendTracker + 1;
    }

    synchronized public void incrementReceivedTracker(){
        this.receiveTracker = receiveTracker + 1;
    }

    synchronized public void addSendSum(long messageVal){
        this.sendSummation = this.sendSummation + messageVal;
    }

    synchronized public void addReceiveSum(long messageVal){
        this.receiveSummation = this.receiveSummation + messageVal;
    }

    synchronized public void incrementRelayed(){
        this.relayTracker = relayTracker + 1;
    }
    
    synchronized public void nodeStats(Event event) {
        try {

            TaskSummaryResponse nodeResponse = new TaskSummaryResponse(event.getBytes());
            nodes.put(nodeResponse.getName(), nodeResponse.getStats());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    synchronized public boolean receivedAllStats() {
        registry.printVertexList();
        
        for(Vertex registeredVertex : registry.getValues()){

            System.out.println(registeredVertex.getID());
            if(!nodes.containsKey(registeredVertex.getID())){
                return false;
            }
        }
        return true; 
    }

    public void displayTotalSums() {

        long sendMessagesSum = 0;
        long receivedMessagesSum = 0;
        long relayedSum = 0;
        long sendSummationTotal = 0;
        long receivedSummationTotal = 0;


        System.out.printf("----------------------------------------------------------------------------------%n");
        System.out.printf("                                Registry Traffic Summary                          %n");
        System.out.printf("                                                                                  %n");

        
        System.out.printf("-----------------------------------------------------------------------------------%n");
        System.out.printf("| %15s | %15s | %15s | %15 | %15s | %15", "Node" ,"Number of messages sent", "Number of messages received", "Summation of sent messages", "Summation of received messages", "Number of messages relayed");
        System.out.printf("-----------------------------------------------------------------------------------%n");
        
        int i = 0;
        for(ArrayList<String> nodeStats : nodes.values()){
            i ++;
            System.out.printf("| %10s | %25s | %25s | %25 | %25s | %20", "Node " + i, nodeStats.get(0), nodeStats.get(1), nodeStats.get(2), nodeStats.get(3), nodeStats.get(4));

        }

        for(ArrayList<String> nodeStats : nodes.values()){
            sendMessagesSum =  sendMessagesSum + Integer.parseInt(nodeStats.get(0));
            receivedMessagesSum = receivedMessagesSum + Integer.parseInt(nodeStats.get(1));
            sendSummationTotal = sendSummationTotal + Integer.parseInt(nodeStats.get(2));
            receivedSummationTotal = receivedSummationTotal + Integer.parseInt(nodeStats.get(3));
            relayedSum = relayedSum + Integer.parseInt(nodeStats.get(4));
        }

        System.out.printf("| %10s | %25s | %25s | %25 | %25s | %20", " Totals: ", sendMessagesSum, receivedMessagesSum, sendSummationTotal, receivedSummationTotal, relayedSum);

        System.out.printf("-------------------------------------------------------------------------------------%n");
    }

    public void resetCounters(){
        this.sendTracker = 0;
        this.receiveTracker = 0;
        this.relayTracker = 0;
        this.sendSummation = 0;
        this.receiveSummation = 0;
    }


// Message Type: TRAFFIC_SUMMARY
// Node IP address:
// Node Port number:
// Number of messages sent
// Summation of sent messages
// Number of messages received
// Summation of received messages
// Number of messages relayed
    
}
