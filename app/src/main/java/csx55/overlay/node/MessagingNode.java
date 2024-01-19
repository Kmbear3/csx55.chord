package csx55.overlay.node;

public class MessagingNode {
    int sendTracker = 0;  // number of messages sent
    int receiveTracker = 0;  // number of messages that were received
    int relayTracker = 0; // Number of messages that were relayed.
    long sendSummation = 0; // Sum of value that it has sent 
    long receiveSummation = 0;  // Sum of the payloads that it has received 


}
