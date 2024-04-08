package csx55.chord.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import csx55.chord.Peer;
import csx55.chord.util.PeerEntry;

public class SuccessorRequest implements Event, Protocol {

    final int MESSAGE_TYPE = Protocol.SUCCESSOR_REQUEST;
    PeerEntry lookupNode;
    PeerEntry requestingNode;
    

    public SuccessorRequest(PeerEntry lookupNode, PeerEntry requestingNode) {
        this.lookupNode = lookupNode;
        this.requestingNode = requestingNode;
    }

    public SuccessorRequest(byte[] marshalledBytes) {
        try {
            ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

            if(din.readInt() != this.MESSAGE_TYPE){
                System.err.println("Type mismatch in SuccessorRequest!!");
            }

            this.lookupNode = PeerEntry.unmarshallPeer(din);
            this.requestingNode = PeerEntry.unmarshallPeer(din);

            baInputStream.close();
            din.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

        this.lookupNode.marshallPeer(dout);
        this.requestingNode.marshallPeer(dout);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }

    public PeerEntry getTargetNode(){
        return  this.lookupNode;
    }
    
    public PeerEntry getRequestingNode(){
        return this.requestingNode;
    }
}

