package csx55.chord.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TaskSummaryRequest implements Event, Protocol{
    // Message Type: PULL_TRAFFIC_SUMMARY

    public TaskSummaryRequest(){
        
    }

    public TaskSummaryRequest(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();

        if(type != Protocol.PULL_TRAFFIC_SUMMARY){
            System.err.println("Mismatch Types in TaskSummaryRequest");
        }

        baInputStream.close();
        din.close();
    }

    @Override
    public int getType() {
        return Protocol.PULL_TRAFFIC_SUMMARY;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(Protocol.PULL_TRAFFIC_SUMMARY);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }
}
