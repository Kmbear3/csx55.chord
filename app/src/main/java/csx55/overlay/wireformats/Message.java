package csx55.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

public class Message implements Event {
    final String MESSAGE_TYPE = "MESSAGE";
    int payload;
    byte[] marshalledBytes;

    public Message(){
        try {
            marshalledBytes = getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int messageTypeLength = din.readInt();
        byte[] identifierBytes = new byte[messageTypeLength];
        din.readFully(identifierBytes);
        String messageType = new String(identifierBytes);

        payload = din.readInt();

        System.out.println("Unmarshalling: Message Type: " + messageType);
        System.out.println("Unmarshalling: Message Payload: " + payload);

        baInputStream.close();
        din.close();
    }

    @Override
    public String getType() {
        return this.MESSAGE_TYPE;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        byte[] messageTypeBytes = MESSAGE_TYPE.getBytes();
        int elementLength = messageTypeBytes.length;
        dout.writeInt(elementLength);
        dout.write(messageTypeBytes);

        this.payload = createPayload();
        dout.writeInt(this.payload);

        System.out.println("Marshalling: Message Type: " + MESSAGE_TYPE);
        System.out.println("Marshalling: Message Payload: " + payload);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    } 

    public int createPayload(){
        Random rand = new Random();
        return rand.nextInt();
    }

    public byte[] getMessage(){
        return marshalledBytes;
    }

}
