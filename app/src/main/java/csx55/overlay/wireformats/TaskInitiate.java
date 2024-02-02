package csx55.overlay.wireformats;

import java.io.*;

public class TaskInitiate implements Protocol, Event{

    // Message Type: TASK_INITIATE
    // Rounds: X
    int numberOfRounds;

    public TaskInitiate(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int messageType = din.readInt();
        this.numberOfRounds = din.readInt();

        if(messageType != Protocol.TASK_INITIATE){
            System.err.println("Inside TaskInitiate: Mismatched Type!");
        }

        baInputStream.close();
        din.close();

    }

    public TaskInitiate(int numberOfRounds){
        this.numberOfRounds = numberOfRounds;
    }

    @Override
    public int getType() {
        return Protocol.TASK_INITIATE;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(Protocol.TASK_INITIATE);
        dout.writeInt(this.numberOfRounds);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }
}
