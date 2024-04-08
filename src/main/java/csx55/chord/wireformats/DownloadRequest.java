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

public class DownloadRequest implements Protocol, Event{

    ArrayList<PeerEntry> hops = new ArrayList<>();
    String filename;
    final int MESSAGE_TYPE = Protocol.DOWNLOAD_REQUEST;
    

    public DownloadRequest(String filename, PeerEntry me) {
        this.filename = filename;
        hops.add(me);
    }

    //This is the case where you add yourself to the hops after getting the ref to the hops list
    // Not the first node
    public DownloadRequest(String filename, ArrayList<PeerEntry> hops){
        this.filename = filename;
        this.hops = hops;
    }

    public DownloadRequest(byte[] marshalledBytes){
         try {
            ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

            if(din.readInt() != this.MESSAGE_TYPE){
                System.err.println("Type mismatch in DownloadRequest!!");
            }

            hops = new ArrayList<>();
            int numberOfhops = din.readInt();
            for(int i = 0; i < numberOfhops; i++){
                PeerEntry peer = PeerEntry.unmarshallPeer(din);
                hops.add(peer);
            }

            int filenameLength = din.readInt();
            byte[] filenamebytes = new byte[filenameLength];
            din.readFully(filenamebytes);
            this.filename = new String(filenamebytes);
            
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

        dout.writeInt(hops.size());

        for(int i = 0; i < hops.size(); i++){
            hops.get(i).marshallPeer(dout);
        }

        byte[] fileBytes = this.filename.getBytes();
        int elementLength = fileBytes.length;
        dout.writeInt(elementLength);
        dout.write(fileBytes);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }

    public String getFilename(){
        return this.filename;
    }

    public ArrayList<PeerEntry> getHops(){
        return this.hops;
    }
    
}
