package csx55.chord.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class RegistrationRequest implements Event, Protocol {

    // Message Type (int): REGISTER_REQUEST
    // IP address (String)
    // Port number (int)
    
    final int MESSAGE_TYPE = Protocol.REGISTER_REQUEST;
    String IP;
    int port;
    int peerID;

    byte[] marshalledBytes;

    public RegistrationRequest(String IP, int port, int peerID){
        try {
            this.IP = IP;
            this.port = port;
            this.peerID = peerID;

            marshalledBytes = getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RegistrationRequest(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();

        int IPlenth = din.readInt();
        byte[] IPBytes = new byte[IPlenth];
        din.readFully(IPBytes);
        this.IP = new String(IPBytes);

        this.port = din.readInt();

        this.peerID = din.readInt();
        
        baInputStream.close();
        din.close();
    }

    @Override
    public int getType() {
        return Protocol.REGISTER_REQUEST;
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

    public String getIP(){
        return this.IP;
    }

    public int getPort(){
        return this.port;
    }

    public int getPeerId(){
        return this. peerID;
    }
}
