package csx55.chord.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import csx55.chord.util.StatisticsCollectorAndDisplay;

public class TaskSummaryResponse implements Event, Protocol {
    String ip;
    int port;

    int numberOfGeneratedTasks;
    int numberOfPulledTasks;
    int numberOfPushedTasks;
    int numberOfCompletedTasks;


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

        this.numberOfGeneratedTasks = din.readInt();
        this.numberOfPulledTasks = din.readInt();
        this.numberOfPushedTasks = din.readInt();
        this.numberOfCompletedTasks = din.readInt();

        baInputStream.close();
        din.close();
    }   

    public TaskSummaryResponse(String ip, int port, StatisticsCollectorAndDisplay stats) {
        this.ip = ip;
        this.port = port;

        this.numberOfGeneratedTasks = stats.getNumberOfGeneratedTasks();
        this.numberOfPulledTasks = stats.getNumberOfPulledTasks();
        this.numberOfPushedTasks = stats.getNumberOfPushedTasks();
        this.numberOfCompletedTasks = stats.getNumberOfCompletedTasks();
    }

    public ArrayList<String> getStats() {
        ArrayList<String> stats = new ArrayList<>();

        stats.add("" + numberOfGeneratedTasks);
        stats.add("" + numberOfPulledTasks);
        stats.add("" + numberOfPushedTasks);
        stats.add("" + numberOfCompletedTasks);
        stats.add("" + getName());

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

        dout.writeInt(this.numberOfGeneratedTasks);
        dout.writeInt(this.numberOfPulledTasks);
        dout.writeInt(this.numberOfPushedTasks);
        dout.writeInt(this.numberOfCompletedTasks);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;

    }
}
