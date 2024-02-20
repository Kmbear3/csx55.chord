package csx55.threads.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegisterationResponse implements Event, Protocol{

    // Message Type (int): REGISTER_RESPONSE
    // Status Code (byte): SUCCESS or FAILURE
    // Additional Info (String): 

    int MESSAGE_TYPE = Protocol.REGISTER_RESPONSE;
    byte statusCode;
    String additionalInfo;
    byte[] marshalledBytes;
    
    public RegisterationResponse(byte statusCode, String additionalInfo){
        try {
        
        this.statusCode = statusCode;
        this.additionalInfo = additionalInfo;
        this.marshalledBytes = getBytes();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }

    public void getInfo(){
        System.out.println(additionalInfo);
    }
}