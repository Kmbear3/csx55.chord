package csx55.threads.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NodeTasks implements Protocol, Event{
    String id;
    int numberOfTasks;
    int roundNumber;

    public NodeTasks(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();

        if(type != Protocol.NODE_TASKS){
            System.err.println("Type Mismatch inside NodeTasks.java");
        }

        int IPlenth = din.readInt();
        byte[] IPBytes = new byte[IPlenth];
        din.readFully(IPBytes);
        this.id = new String(IPBytes);

        this.numberOfTasks = din.readInt();
        this.roundNumber = din.readInt();


        baInputStream.close();
        din.close();

    }

    public NodeTasks(String id, int numberOfTasks, int roundNumber){
        this.id = id;
        this.numberOfTasks = numberOfTasks;
        this.roundNumber = roundNumber;
    }

    public int getNumberOfTasks(){
        return this.numberOfTasks;
    }

    public String getId(){
        return this.id;
    }
    
    @Override
    public int getType() {
       return Protocol.NODE_TASKS;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(Protocol.NODE_TASKS);

        byte[] IPBytes = this.id.getBytes();
        int elementLength = IPBytes.length;
        dout.writeInt(elementLength);
        dout.write(IPBytes);

        dout.writeInt(this.numberOfTasks);
        dout.writeInt(this.roundNumber);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }    
}
