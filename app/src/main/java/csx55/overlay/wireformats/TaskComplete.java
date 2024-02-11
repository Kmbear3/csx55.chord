package csx55.overlay.wireformats;

import java.io.*;
import java.util.ArrayList;

// Message Type: TASK_COMPLETE
// Node IP address:
// Node Port number:

public class TaskComplete implements Event, Protocol{
    final int MESSAGE_TYPE = Protocol.TASK_COMPLETE;
    String IP;
    int port;

    public TaskComplete(String IP, int port){
        this.IP = IP;
        this.port = port;
    }

    public TaskComplete(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();

        if(type != Protocol.TASK_COMPLETE){
            System.err.println("Error! type mismatch in TaskComplete! ");
        }

        int IPlenth = din.readInt();
        byte[] IPBytes = new byte[IPlenth];
        din.readFully(IPBytes);
        this.IP = new String(IPBytes);

        this.port = din.readInt();

        baInputStream.close();
        din.close();

    }

    public String getIP() {
        return this.IP;
    }

    public int getPort() {
        return this.port;
    }

    public String getID(){
        return this.IP + ":" + this.port;
    }

    @Override
    public int getType() {
        return this.MESSAGE_TYPE;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(this.MESSAGE_TYPE);

        byte[] IPBytes = this.IP.getBytes();
        int elementLength = IPBytes.length;
        dout.writeInt(elementLength);
        dout.write(IPBytes);

        dout.writeInt(this.port);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

}
