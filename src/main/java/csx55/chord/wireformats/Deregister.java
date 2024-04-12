package csx55.chord.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Deregister implements Event, Protocol{


    // Message Type: DEREGISTER_REQUEST
    // Node IP address:
    // Node Port number:
   
    final int MESSAGE_TYPE = Protocol.DEREGISTER_REQUEST;
    String IP;
    int port;
    int peerID;

    public Deregister(String IP, int port, int peerID){
        this.IP = IP;
        this.port = port;
        this.peerID = peerID;
    }

    public Deregister(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();

        if(type != Protocol.DEREGISTER_REQUEST){
            System.err.println("Mismatch type in Deregister!!!");
        }

        int IPlenth = din.readInt();
        byte[] IPBytes = new byte[IPlenth];
        din.readFully(IPBytes);
        this.IP = new String(IPBytes);

        this.port = din.readInt();

        this.peerID = din.readInt();

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

    public int getPeerID(){
        return this.peerID;
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
        dout.writeInt(this.peerID);
        
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }
}
