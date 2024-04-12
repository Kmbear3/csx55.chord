package csx55.chord.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import csx55.chord.util.PeerEntry;

public class ExitNotification implements Event, Protocol{

    int MESSAGE_TYPE = Protocol.EXIT_NOTIFICATION;

    PeerEntry me;
    PeerEntry succ;


    public ExitNotification(PeerEntry me, PeerEntry succ){
        this.me = me;
        this.succ = succ;
    }

    public ExitNotification(byte[] marshalledBytes){
        try {
            ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

            if(din.readInt() != this.MESSAGE_TYPE){
                System.err.println("Type mismatch in ExitNotification!!");
            }

            this.me = PeerEntry.unmarshallPeer(din);
            this.succ = PeerEntry.unmarshallPeer(din);

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

        this.me.marshallPeer(dout);
        this.succ.marshallPeer(dout);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }
    
    public PeerEntry getLeavingPeer(){
        return this.me;
    }

    public PeerEntry getLeavingPeerSucc(){
        return this.succ;
    }

}
