package csx55.overlay.wireformats;

import java.io.IOException;
import csx55.overlay.wireformats.*;

public class EventFactory {
    //Singleton instance

    // Check message type --> Have functionality differ in switch statemnets

     //use getShape method to get object of type shape 

    public static Event getEvent(int messageType, byte[] unmarshalledBytes){
        try {
            switch(messageType){
                case Protocol.MESSAGE:
                    return new Message(unmarshalledBytes);
                case Protocol.REGISTER_REQUEST:
                    return new RegistrationRequest(unmarshalledBytes);
                case Protocol.REGISTER_RESPONSE:
                    return new RegisterationResponse(unmarshalledBytes);
                default:
                    return null;
            
            }
        }  catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }
        return null;
    }
}
