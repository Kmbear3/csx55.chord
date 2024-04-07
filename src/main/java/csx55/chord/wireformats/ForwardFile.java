package csx55.chord.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ForwardFile implements Protocol, Event {

    String filename;
    byte[] file; 
    int MESSAGE_TYPE = Protocol.FORWARD_FILE;

    public ForwardFile(String filename, byte[] file){
        this.file = file;
        this.filename = filename;
    }

    public ForwardFile(byte[] marshalledBytes){
        try{
            ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

            int type = din.readInt();

            if(type != this.MESSAGE_TYPE){
                System.err.println("Type mismatch in ForwardFile");
            }

            int filenamelength = din.readInt();
            byte[] filenameBytes = new byte[filenamelength];
            din.readFully(filenameBytes);
            this.filename = new String(filenameBytes);

            int filelength = din.readInt();
            this.file = new byte[filelength];
            din.readFully(file);
            
            baInputStream.close();
            din.close();

        }catch(IOException e){
            System.err.println("Exception inside ForwardFile: " + e.getMessage());
        }
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

        //write file name
        byte[] filenameBytes = this.filename.getBytes();
        int elementLength = filenameBytes.length;
        dout.writeInt(elementLength);
        dout.write(filenameBytes);

        //write file
        int fileLength = this.file.length;
        dout.writeInt(fileLength);
        dout.write(file);    

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }

    public String getFilename(){
        return this.filename;
    }

    public byte[] getFile(){
        return this.file;
    }
    
}
