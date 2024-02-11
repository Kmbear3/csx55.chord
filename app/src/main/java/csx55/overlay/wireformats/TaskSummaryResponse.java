package csx55.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import csx55.overlay.util.StatisticsCollectorAndDisplay;

public class TaskSummaryResponse implements Event, Protocol {
    // Message Type: TRAFFIC_SUMMARY
    // Node IP address:
    // Node Port number:
    // Number of messages sent
    // Summation of sent messages
    // Number of messages received
    // Summation of received messages
    // Number of messages relayed

    String ip;
    int port;

    int messagesSent;
    long messageSum;

    int messagesReceived;
    long receivedSum;
    int relayed;


    public TaskSummaryResponse(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
       
        int messageType = din.readInt();

        if(messageType != Protocol.TRAFFIC_SUMMARY){
            System.err.println("Mismatch Messagetype! TrafficSummary != " + messageType);
        }

        int IPlength = din.readInt();
        byte[] IPBytes = new byte[IPlength];
        din.readFully(IPBytes);
        this.ip = new String(IPBytes);

        this.port = din.readInt();
        this.messagesSent = din.readInt();
        this.messageSum = din.readLong();
        this.messagesReceived = din.readInt();
        this.receivedSum = din.readLong();
        this.relayed = din.readInt();

        baInputStream.close();
        din.close();
    }   

    public TaskSummaryResponse(String ip, int port, StatisticsCollectorAndDisplay stats) {
        this.ip = ip;
        this.port = port;

        this.messagesSent = stats.getSendTracker();
        this.messageSum = stats.getSendSum();
        this.messagesReceived = stats.getReceiveTracker();
        this.receivedSum = stats.getReceivedSum();
        this.relayed = stats.getRelayTracker();
    }

    public ArrayList<String> getStats() {
        ArrayList<String> stats = new ArrayList<>();

        stats.add("" + messagesSent);
        stats.add("" + messagesReceived);
        stats.add("" + messageSum);
        stats.add("" + receivedSum);
        stats.add("" + relayed);

        return stats;
    }

    public String getName() {
        return ip + ":" + port;
    }

    @Override
    public int getType() {
        return Protocol.TRAFFIC_SUMMARY;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(Protocol.TRAFFIC_SUMMARY);
       
        byte[] IPBytes = this.ip.getBytes();
        int elementLength = IPBytes.length;
        dout.writeInt(elementLength);
        dout.write(IPBytes);

        dout.writeInt(this.port);
        dout.writeInt(this.messagesSent);
        dout.writeLong(this.messageSum);
        dout.writeInt(this.messagesReceived);
        dout.writeLong(this.receivedSum);
        dout.writeInt(this.relayed);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;

    }
}
