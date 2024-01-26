package csx55.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.net.ssl.SSLEngineResult.Status;

public class RegisterationResponse implements Event, Protocol{

    // Message Type (int): REGISTER_RESPONSE
    // Status Code (byte): SUCCESS or FAILURE
    // Additional Info (String): 

    // “Registration request
    // successful. The number of messaging nodes currently constituting the overlay is (5)”.

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    

    public RegisterationResponse(byte[] marshalledBytes) {
        try {
            ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

            int type = din.readInt();

            byte statusCode = din.readByte();
        
            int infoLength = din.readInt();
            byte[] infoBytes = new byte[infoLength];
            din.readFully(infoBytes);
            this.additionalInfo = new String(infoBytes);

            baInputStream.close();
            din.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
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

        System.out.println("Inside RegResponse.getBytes() Type: " + Protocol.REGISTER_RESPONSE + "---- status: " + this.statusCode);
        
        byte[] additionalInfoBytes = this.additionalInfo.getBytes();
        int elementLength = additionalInfoBytes.length;
        dout.writeInt(elementLength);
        dout.write(additionalInfoBytes);
        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        System.out.println("Made it to bottom getBytes()");
        return marshalledBytes;
    }

    public void getInfo(){
        System.out.println(additionalInfo);
    }
}
