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

public class MigrateFile implements Protocol, Event{
    String filename;
    byte[] file;
    final int MESSAGE_TYPE = Protocol.MIGRATE_FILE;


    public MigrateFile(String filename, byte[] file){
        this.file = file;
        this.filename = filename;
    }

    public MigrateFile(byte[] marshalledBytes){
         try {
            ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

            if(din.readInt() != this.MESSAGE_TYPE){
                System.err.println("Type mismatch in MigrateFile!!");
            }
        
            int filenameLength = din.readInt();
            byte[] filenamebytes = new byte[filenameLength];
            din.readFully(filenamebytes);
            this.filename = new String(filenamebytes);

            int fileLength = din.readInt();
            this.file = new byte[fileLength];
            din.readFully(this.file);
            
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

        byte[] fileBytes = this.filename.getBytes();
        int filenameLength = fileBytes.length;
        dout.writeInt(filenameLength);
        dout.write(fileBytes);

        int elementLength = file.length;
        dout.writeInt(elementLength);
        dout.write(file);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }

    public byte[] getFile(){
        return this.file;
    }

    public String getFileName(){
        return this.filename;
    }

}


