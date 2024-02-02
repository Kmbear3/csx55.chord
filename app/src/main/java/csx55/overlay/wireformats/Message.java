package csx55.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Message implements Event, Protocol {
    final int MESSAGE_TYPE = Protocol.MESSAGE;
    int payload;
    byte[] marshalledBytes;
    ArrayList<String> routePlan = new ArrayList<>();

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
       
        int messageType = din.readInt();
        payload = din.readInt();

        System.out.println("Inside Message() Type: " + messageType + "---- Payload: " + payload);

        baInputStream.close();
        din.close();
    }

    @Override
    public int getType() {
        return Protocol.MESSAGE;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(Protocol.MESSAGE);
        this.payload = createPayload();
        dout.writeInt(this.payload);

        System.out.println("Inside Message.getBytes() Type: " + Protocol.MESSAGE + "---- Payload: " + this.payload);

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
