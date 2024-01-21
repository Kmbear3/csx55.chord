package csx55.overlay.wireformats;

import java.io.IOException;

import javax.net.ssl.SSLEngineResult.Status;

public class RegisterationResponse implements Event, Protocol{

    // Message Type (int): REGISTER_RESPONSE
    // Status Code (byte): SUCCESS or FAILURE
    // Additional Info (String): 

    // “Registration request
    // successful. The number of messaging nodes currently constituting the overlay is (5)”. 
    

    public RegisterationResponse(byte[] unmarshalledBytes) {
        //TODO Auto-generated constructor stub
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
