package csx55.threads.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import csx55.threads.wireformats.Event;
import csx55.threads.wireformats.TaskSummaryResponse;

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

    synchronized public int getReceiveTracker(){
        return receiveTracker;
    }
    synchronized public int getRelayTracker(){
        return relayTracker;
    }

    synchronized public long getSendSum(){
        return sendSummation;
    }

    synchronized public long getReceivedSum(){
        return receiveSummation;
    }

    synchronized public int getSendTracker(){
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
        this.sendTracker = this.sendTracker + 1;
    }

    synchronized public void incrementReceivedTracker(){
        this.receiveTracker = this.receiveTracker + 1;
    }

    synchronized public void addSendSum(long messageVal){
        this.sendSummation = this.sendSummation + messageVal;
    }

    synchronized public void addReceiveSum(long messageVal){
        this.receiveSummation = this.receiveSummation + messageVal;
    }

    synchronized public void incrementRelayed(){
        this.relayTracker = this.relayTracker + 1;
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
        // registry.printVertexList();
        
        for(Vertex registeredVertex : registry.getValues()){

            // System.out.println(registeredVertex.getID());
            if(!nodes.containsKey(registeredVertex.getID())){
                return false;
            }
        }
        return true; 
    }

    synchronized public void displayTotalSums() {

        long sendMessagesSum = 0;
        long receivedMessagesSum = 0;
        long relayedSum = 0;
        long sendSummationTotal = 0;
        long receivedSummationTotal = 0;


        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("                                                          Registry Traffic Summary                                                    ");
        System.out.println("                                                                                                                                      ");

        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(String.format("| %-10s | %20s | %22s | %22s | %22s | %22s |", "Node" ,"Messages Sent", "Messages Received", "Sent Messages Sum", "Received Messages Sum", "Messages Relayed"));
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");
        
        int i = 0;
        for(ArrayList<String> nodeStats : nodes.values()){
            i ++;
            String nodeName = "Node " + i;

            System.out.println(String.format("| %-10s | %20s | %22s | %22s | %22s | %22s |", nodeName, nodeStats.get(0), nodeStats.get(1), nodeStats.get(2), nodeStats.get(3), nodeStats.get(4)));

        }

        for(ArrayList<String> nodeStats : nodes.values()){
            sendMessagesSum =  sendMessagesSum + Long.parseLong(nodeStats.get(0));
            receivedMessagesSum = receivedMessagesSum + Long.parseLong(nodeStats.get(1));
            sendSummationTotal = sendSummationTotal + Long.parseLong(nodeStats.get(2));
            receivedSummationTotal = receivedSummationTotal + Long.parseLong(nodeStats.get(3));
            relayedSum = relayedSum + Long.parseLong(nodeStats.get(4));
        }

        System.out.println(String.format("| %-10s | %20d | %22d | %22d | %22d | %22d |", "Totals: ", sendMessagesSum, receivedMessagesSum, sendSummationTotal, receivedSummationTotal, relayedSum));

        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");
    }

    synchronized public void resetCounters(){
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
