package csx55.chord.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import csx55.chord.util.PeerEntry;

public class InsertResponse  implements Event, Protocol {

    // Message Type (int): REGISTER_REQUEST
    // IP address (String)
    // Port number (int)
    
    final int MESSAGE_TYPE = Protocol.INSERT_RESPONSE;
    String IP;
    int port;
    int peerID;
    PeerEntry[] fingerTable;
    
    String predecessorIP;
    int predPort;
    int predPeerID;

    public InsertResponse(String IP, int port, int peerID, PeerEntry[] fingerTable, String predIP, int predPort, int predPeerID){
        // Inserting nodes successor info
        this.IP = IP;
        this.port = port;
        this.peerID = peerID;
        this.fingerTable = fingerTable;

        // Successor's predecessors information, which is now your predecessor
        this.predecessorIP = predIP;
        this.predPort = predPort;
        this.predPeerID = predPeerID;
    }

    public InsertResponse(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();

        if(type != this.MESSAGE_TYPE){
            System.err.println("Type mismatch inside InsertResponse.");
        }

        int IPlenth = din.readInt();
        byte[] IPBytes = new byte[IPlenth];
        din.readFully(IPBytes);
        this.IP = new String(IPBytes);

        this.port = din.readInt();
        this.peerID = din.readInt();


        this.fingerTable = new PeerEntry[32];

        for(int i = 0; i < fingerTable.length; i ++){
            
            int peerIPLength = din.readInt();
            byte[] peerIPBytes = new byte[peerIPLength];
            din.readFully(peerIPBytes);
            String peerIP = new String(peerIPBytes);

            int peerPort = din.readInt();
            int ftPeerID = din.readInt();

            PeerEntry peer = new PeerEntry(peerIP, peerPort, ftPeerID);
            fingerTable[i] = peer;
        }


        int predIPlenth = din.readInt();
        byte[] predIPBytes = new byte[predIPlenth];
        din.readFully(predIPBytes);
        this.predecessorIP = new String(predIPBytes);

        this.predPort = din.readInt();
        this.predPeerID = din.readInt();
        
        baInputStream.close();
        din.close();
    }

    @Override
    public int getType() {
        return MESSAGE_TYPE;
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

        for(int i = 0; i < fingerTable.length; i++){
            PeerEntry ftentry = fingerTable[i];
            String peerIP = ftentry.getIP();

            byte[] peerBytes = peerIP.getBytes();
            int length = peerBytes.length;
            dout.writeInt(length);
            dout.write(peerBytes);

            int peerPort = ftentry.getPort();
            int ftPeerID = ftentry.getID();

            dout.writeInt(peerPort);
            dout.writeInt(ftPeerID);
        }

        byte[] predBytes = predecessorIP.getBytes();
        int predLength = predBytes.length;
        dout.writeInt(predLength);
        dout.write(predBytes);

        dout.writeInt(predPort);
        dout.writeInt(predPeerID);

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
        return this.peerID;
    }

    public PeerEntry[] getFingerTable(){
        return this.fingerTable;
    }

    public PeerEntry getSucc(){
        return new PeerEntry(this.IP, this.port, this.peerID);
    }

    public PeerEntry getPred(){
        return new PeerEntry(this.predecessorIP, this.predPort, this.predPeerID);
    }
}

    

