package csx55.chord.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import csx55.chord.util.PeerEntry;

public class SuccessorResponse implements Event, Protocol {

    final int MESSAGE_TYPE = Protocol.SUCCESSOR_RESPONSE;
    PeerEntry peer;

    public SuccessorResponse(PeerEntry peer) {
        this.peer = peer;
    }

    public SuccessorResponse(byte[] marshalledBytes) {
        try {
            ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

            if(din.readInt() != this.MESSAGE_TYPE){
                System.err.println("Type mismatch in SuccessorResponse!!");
            }

            this.peer = PeerEntry.unmarshallPeer(din);
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
        this.peer.marshallPeer(dout);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }

    public PeerEntry getSuccessor(){
        return  this.peer;
    }
    
}

