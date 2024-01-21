package csx55.overlay.wireformats;

import java.io.IOException;

import javax.sound.sampled.Port;

public class RegistrationRequest implements Event, Protocol {

    // Message Type (int): REGISTER_REQUEST
    // IP address (String)
    // Port number (int)
    
    final int MESSAGE_TYPE = Protocol.REGISTER_REQUEST;
    String IP;
    int port;
    byte[] marshalledBytes;

    public RegistrationRequest(byte[] data) {
        //TODO Auto-generated constructor stub
    }

    @Override
    public int getType() {
        return Protocol.REGISTER_REQUEST;
    }
    
    @Override
    public byte[] getBytes() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBytes'");
    }
    
    
}
