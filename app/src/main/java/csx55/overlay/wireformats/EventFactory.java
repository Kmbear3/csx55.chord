package csx55.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import csx55.overlay.wireformats.*;

public class EventFactory {
    //Singleton instance

    // Check message type --> Have functionality differ in switch statemnets

     //use getShape method to get object of type shape 

    public static Event getEvent(byte[] marshalledBytes){
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        
        try {
            int messageType = din.readInt();

            System.out.println("Inside EventFactory.getEvent() -- Message Type: " + messageType);
            switch(messageType){
                case Protocol.MESSAGE:
                    return new Message(marshalledBytes);
                case Protocol.REGISTER_REQUEST:
                    return new RegistrationRequest(marshalledBytes);
                case Protocol.REGISTER_RESPONSE:
                    return new RegisterationResponse(marshalledBytes);
                case Protocol.MESSAGING_NODES_LIST:
                    return new MessagingNodesList(marshalledBytes);
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
