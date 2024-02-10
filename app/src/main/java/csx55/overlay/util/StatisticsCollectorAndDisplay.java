package csx55.overlay.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import csx55.overlay.wireformats.Event;
import csx55.overlay.wireformats.Message;
import csx55.overlay.wireformats.TaskSummaryRequest;
import csx55.overlay.wireformats.TaskSummaryResponse;

public class StatisticsCollectorAndDisplay {
    // Add all summations

    private int sendTracker;  // number of messages sent
    private int receiveTracker;  // number of messages that were received
    private int relayTracker; // Number of messages that were relayed.
    private long sendSummation; // Sum of value that it has sent
    private long receiveSummation;  // Sum of the payloads that it has received

    ConcurrentHashMap<String, ArrayList<String>> nodes;

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
            
            TaskSummaryResponse nodeResponse;
            nodeResponse = new TaskSummaryResponse(event.getBytes());
            nodes.put(nodeResponse.getName(), nodeResponse.getStats());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean receivedAllStats() {
        for(String key : registry.vertexIDs){
            if(!nodes.contains(key)){
                return false;
            }
        }
        return true; 
    }

    public void displayTotalSums() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'displayTotalSums'");
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
