
package csx55.chord.wireformats;

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
    final int payload;
    byte[] marshalledBytes;
    ArrayList<String> routePlan = new ArrayList<>();

    public Message() throws IOException{
        marshalledBytes = getBytes();
        this.payload = createPayload();
    }

    public Message(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
       
        int messageType = din.readInt();
        this.payload = din.readInt();

        int routeSize = din.readInt();

        for(int i = 0; i < routeSize; i ++){
            int nodeLength = din.readInt();
            byte[] IDBytes = new byte[nodeLength];
            din.readFully(IDBytes);
            String vertexID = new String(IDBytes);

            routePlan.add(vertexID);
        }

        // System.out.println("Inside Message() Type: " + messageType + "---- Payload: " + payload);

        baInputStream.close();
        din.close();
    }

    public Message(ArrayList<String> routePlan){
        this.routePlan = routePlan;
        this.payload = createPayload();
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
        dout.writeInt(this.payload);

        dout.writeInt(routePlan.size());

        for(String node : routePlan){
            byte[] nodeBytes = node.getBytes();
            int elementLength = nodeBytes.length;
            dout.writeInt(elementLength);
            dout.write(nodeBytes);
        }

        // System.out.println("Inside Message.getBytes() Type: " + Protocol.MESSAGE + "---- Payload: " + this.payload);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    } 

    public int getPayload(){
        return this.payload;
    }

    public int createPayload(){
        Random rand = new Random();
        return rand.nextInt();
    }

    public byte[] getMessage(){
        return marshalledBytes;
    }

    public ArrayList<String> getRoutePlan(){
        return this.routePlan;
    }

}
