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

public class DownloadResponse implements Event, Protocol{

    ArrayList<PeerEntry> hops;
    boolean fileFound;
    byte[] file;
    String fileName;
    final int MESSAGE_TYPE = Protocol.DOWNLOAD_RESPONSE;

    public DownloadResponse(String fileName, ArrayList<PeerEntry> hops, boolean fileFound, byte[] file) {
        this.hops = hops;
        this.fileFound = fileFound;
        this.file = file;
        this.fileName = fileName;
    }

    public DownloadResponse(byte[] marshalledBytes){
        try {
            ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

            if(din.readInt() != this.MESSAGE_TYPE){
                System.err.println("Type mismatch in DownloadResponse!!");
            }

            hops = new ArrayList<>();
            int numberOfhops = din.readInt();
            for(int i = 0; i < numberOfhops; i++){
                PeerEntry peer = PeerEntry.unmarshallPeer(din);
                hops.add(peer);
            }

            this.fileFound = din.readBoolean();

            if(fileFound){
                int fileLength = din.readInt();
                this.file = new byte[fileLength];
                din.readFully(file);
            }
            else{
                this.file = null;
            }

            int filenameLength = din.readInt();
            byte[] filenamebytes = new byte[filenameLength];
            din.readFully(filenamebytes);
            this.fileName = new String(filenamebytes);
            
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

        dout.writeBoolean(fileFound);

        int elementLength = file.length;
        dout.writeInt(elementLength);
        dout.write(file);

        byte[] fileBytes = this.fileName.getBytes();
        int filenameLength = fileBytes.length;
        dout.writeInt(filenameLength);
        dout.write(fileBytes);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }

    public byte[] getFile(){
        return this.file;
    }

    public ArrayList<PeerEntry> getHops(){
        return this.hops;
    }

    public boolean getFileStatus(){
        return this.fileFound;
    }

    public String getFilename(){
        return this.fileName;
    }


}
