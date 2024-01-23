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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    }

    @Override
    public byte[] getBytes() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBytes'");
    }
}
