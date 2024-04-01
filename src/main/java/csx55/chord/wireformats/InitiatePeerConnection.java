package csx55.chord.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class InitiatePeerConnection implements Event, Protocol{
   
    int MESSAGE_TYPE = Protocol.INITIATE_PEER_CONNECTION;
    String IP;
    int port;

    public InitiatePeerConnection(String IP, int port){
        this.IP = IP;
        this.port = port;
    }

    public InitiatePeerConnection(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();

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
