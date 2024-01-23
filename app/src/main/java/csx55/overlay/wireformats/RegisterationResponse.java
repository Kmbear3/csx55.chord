package csx55.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import javax.net.ssl.SSLEngineResult.Status;

public class RegisterationResponse implements Event, Protocol{

    // Message Type (int): REGISTER_RESPONSE
    // Status Code (byte): SUCCESS or FAILURE
    // Additional Info (String): 

    // “Registration request
    // successful. The number of messaging nodes currently constituting the overlay is (5)”.

    public interface StatusCode {
        byte SUCCESS = 0;
        byte FAILURE = 1; 
    }

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
        // ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        // DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        // type = din.readInt();
        // timestamp = din.readLong();

        // int identifierLength = din.readInt();
        // byte[] identifierBytes = new byte[identifierLength];
        // din.readFully(identifierBytes);
        // identifier = new String(identifierBytes);

        // tracker = din.readInt();
        // baInputStream.close();
        // din.close();
    }

    @Override
    public int getType() {
        return Protocol.REGISTER_RESPONSE;
    }

    @Override
    public byte[] getBytes() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBytes'");
    }
}
