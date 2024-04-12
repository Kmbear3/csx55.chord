package csx55.chord.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import csx55.chord.wireformats.*;

public class EventFactory {
    //Singleton instance

    // Check message type --> Have functionality differ in switch statemnets

     //use getShape method to get object of type shape 

    public static Event getEvent(byte[] marshalledBytes){
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        
        try {
            int messageType = din.readInt();

            // System.out.println("Inside EventFactory.getEvent() -- Message Type: " + messageType);
            switch(messageType){
                case Protocol.MESSAGE:
                    return new Message(marshalledBytes);
                case Protocol.REGISTER_REQUEST:
                    return new RegistrationRequest(marshalledBytes);
                case Protocol.REGISTER_RESPONSE:
                    return new RegisterationResponse(marshalledBytes);
                case Protocol.INITIATE_PEER_CONNECTION:
                    return new InitiatePeerConnection(marshalledBytes);
                case Protocol.POKE:
                    return new Poke(marshalledBytes);
                case Protocol.PULL_TRAFFIC_SUMMARY:
                    return new TaskSummaryRequest(marshalledBytes);
                case Protocol.TRAFFIC_SUMMARY:
                    return new TaskSummaryResponse(marshalledBytes);
                case Protocol.DEREGISTER_REQUEST:
                    return new Deregister(marshalledBytes);
                case Protocol.DEREGISTER_RESPONSE:
                    return new DeregisterResponse(marshalledBytes);
                case Protocol.INSERT_REQUEST:
                    return new InsertRequest(marshalledBytes);
                case Protocol.INSERT_RESPONSE:
                    return new InsertResponse(marshalledBytes);
                case Protocol.NEW_SUCCESSOR:
                    return new NewSuccessor(marshalledBytes);
                case Protocol.SUCCESSOR_REQUEST:
                    return new SuccessorRequest(marshalledBytes);
                case Protocol.SUCCESSOR_RESPONSE:
                    return new SuccessorResponse(marshalledBytes);
                case Protocol.NEW_ADDITION:
                    return new NewAddition(marshalledBytes);
                case Protocol.FORWARD_FILE:
                    return new ForwardFile(marshalledBytes);
                case Protocol.DOWNLOAD_REQUEST:
                    return new DownloadRequest(marshalledBytes);
                case Protocol.DOWNLOAD_RESPONSE:
                    return new DownloadResponse(marshalledBytes);
                case Protocol.MIGRATE_FILE:
                    return new MigrateFile(marshalledBytes);
                case Protocol.EXIT_NOTIFICATION:
                    return new ExitNotification(marshalledBytes);
                case Protocol.NOTIFY_SUCCESSOR:
                    return new NotifySuccessor(marshalledBytes);
                default:
                    System.err.println("Didn't have an event!" + messageType);
                    return null;
            
            }
        }  catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }
        return null;
    }
}
