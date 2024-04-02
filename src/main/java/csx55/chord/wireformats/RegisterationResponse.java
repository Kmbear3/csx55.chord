package csx55.chord.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import csx55.chord.util.Vertex;

public class RegisterationResponse implements Event, Protocol{

    // Message Type (int): REGISTER_RESPONSE
    // Status Code (byte): SUCCESS or FAILURE
    // Additional Info (String): 

    int MESSAGE_TYPE = Protocol.REGISTER_RESPONSE;
    byte statusCode;
    String additionalInfo;
    int peerID; 

    Vertex connectingPeer;


    
    public RegisterationResponse(byte statusCode, String additionalInfo, int peerID, Vertex randomPeer){      
        this.statusCode = statusCode;
        this.additionalInfo = additionalInfo;
        this.peerID = peerID;
        this.connectingPeer = randomPeer;
    }
    

    public RegisterationResponse(byte[] marshalledBytes) {
        try {
            ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

            int type = din.readInt();

            this.statusCode = din.readByte();
        
            int infoLength = din.readInt();
            byte[] infoBytes = new byte[infoLength];
            din.readFully(infoBytes);
            this.additionalInfo = new String(infoBytes);

            this.peerID = din.readInt();


            // Reading Random Peer node
            int peerId = din.readInt();
            int peerPort = din.readInt();

            int IPlenth = din.readInt();
            byte[] ipbytes = new byte[IPlenth];
            din.readFully(ipbytes);
            String peerIP = new String(ipbytes);

            this.connectingPeer = new Vertex(peerId, peerIP, peerPort);

            baInputStream.close();
            din.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getType() {
        return Protocol.REGISTER_RESPONSE;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(Protocol.REGISTER_RESPONSE);
        dout.writeByte(this.statusCode);

        byte[] additionalInfoBytes = this.additionalInfo.getBytes();
        int elementLength = additionalInfoBytes.length;
        dout.writeInt(elementLength);
        dout.write(additionalInfoBytes);
       
        dout.writeInt(this.peerID);

        // Writing connecting peer information

        dout.writeInt(connectingPeer.getID());
        dout.writeInt(connectingPeer.getPort());

        byte[] peernodeIP = this.connectingPeer.getIP().getBytes();
        int IPlenth = peernodeIP.length;
        dout.writeInt(IPlenth);
        dout.write(peernodeIP);
       
        // Done writing connection peer 

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }

    public void getInfo(){
        System.out.println(additionalInfo);
    }

    public int getPeerID(){
        return this.peerID;
    }

    public Vertex getVertex(){
        return this.connectingPeer;
    }
}
