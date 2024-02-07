package csx55.overlay.util;

public class StatisticsCollectorAndDisplay {
    // Add all summations

    private int sendTracker;  // number of messages sent
    private int receiveTracker;  // number of messages that were received
    private int relayTracker; // Number of messages that were relayed.
    private long sendSummation; // Sum of value that it has sent
    private long receiveSummation;  // Sum of the payloads that it has received


    public StatisticsCollectorAndDisplay(){
        this.sendTracker = 0;
        this.receiveTracker = 0;
        this.relayTracker = 0;
        this.sendSummation = 0;
        this.receiveSummation = 0;
    }

    synchronized public void incrementSendTracker(){
        this.sendTracker = sendTracker + 1;
    }

    synchronized public void incrementReceivedTracker(){
        this.receiveTracker = receiveTracker + 1;
    }

    synchronized public void addSumTracker(long messageVal){
        this.sendSummation = this.sendSummation + messageVal;
    }

    synchronized public void addReceiveTracker(long messageVal){
        this.receiveSummation = this.receiveSummation + messageVal;
    }

    synchronized public void displayStats(){
        System.out.println("receiveTracker: " + receiveTracker);
        System.out.println("sendTracker: " + sendTracker);
    }
    
}
