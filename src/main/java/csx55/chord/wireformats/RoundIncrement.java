package csx55.chord.wireformats;

import java.io.*;

public class RoundIncrement implements Event, Protocol{
    String IP;
    int port;
    int roundNumber;

    public RoundIncrement(String IP, int port, int roundNumber){
        this.IP = IP;
        this.port = port;
        this.roundNumber = roundNumber;
    }

    public RoundIncrement(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if(type != Protocol.ROUND_INCREMENT){
            System.err.println("Type mismatch in Round Increment");
        }

        int IPlenth = din.readInt();
        byte[] IPBytes = new byte[IPlenth];
        din.readFully(IPBytes);
        this.IP = new String(IPBytes);

        this.port = din.readInt();

        this.roundNumber = din.readInt();

        baInputStream.close();
        din.close();

    }

    public void printRoundIncrement(){
        System.out.println("IP: " + this.getIP());
        System.out.println("Port: " + this.getPort());
        System.out.println("Round Increment: " + this.getRoundNumber());

    }

    public String getIP() {
        return this.IP;
    }

    public int getPort() {
        return this.port;
    }

    public int getRoundNumber() {
        return this.roundNumber;
    }

    public String getID(){
        return this.IP + ":" + this.port;
    }

    @Override
    public int getType() {
        return Protocol.ROUND_INCREMENT;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(Protocol.ROUND_INCREMENT);

        byte[] IPBytes = this.IP.getBytes();
        int elementLength = IPBytes.length;
        dout.writeInt(elementLength);
        dout.write(IPBytes);

        dout.writeInt(this.port);

        dout.writeInt(this.roundNumber);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }
}
