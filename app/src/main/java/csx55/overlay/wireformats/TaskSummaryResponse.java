package csx55.overlay.wireformats;

import java.io.IOException;
import java.util.ArrayList;

import csx55.overlay.util.StatisticsCollectorAndDisplay;

public class TaskSummaryResponse implements Event, Protocol {

    public TaskSummaryResponse(byte[] marshalledBytes) {
        //TODO Auto-generated constructor stub
    }   
    // Message Type: TRAFFIC_SUMMARY
    // Node IP address:
    // Node Port number:
    // Number of messages sent
    // Summation of sent messages
    // Number of messages received
    // Summation of received messages
    // Number of messages relayed

    public TaskSummaryResponse(StatisticsCollectorAndDisplay stats) {
        //TODO Auto-generated constructor stub
    }

    public ArrayList<String> getStats() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getStats'");
    }

    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    @Override
    public int getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    }

    @Override
    public byte[] getBytes() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBytes'");
    }

    // REset counters!! After sneding 

    
}
