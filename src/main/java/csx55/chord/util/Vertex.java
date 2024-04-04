package csx55.chord.util;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import csx55.chord.transport.TCPSender;

public class Vertex implements Comparable<Vertex>{
    private Socket socket;
    private final String IP;
    private int port;

    private int peerID;

    public Vertex(int peerID, String IP, int port, Socket socket){
        this.IP = IP;
        this.port = port;
        this.socket = socket;
        this.peerID = peerID;
    }

    public Vertex(int peerID, String IP, int port){
        this.IP = IP;
        this.port = port;
        this.peerID = peerID;
    }


    public int getID(){
        return peerID;
    }

    public void setID(int id){
        this.peerID = id;
    }

    public void setPort(int port){
        this.port = port;
    }

    synchronized public Socket getSocket(){
        return this.socket;
    }

    synchronized public void sendMessage(byte[] marshalledBytes){
        // Todo - Maybe add check to see if the socket is null first.
        try {
    
            TCPSender send = new TCPSender(this.socket);
            send.sendData(marshalledBytes);
        
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
    }

    public String getIP(){
        return this.IP;
    }

    public int getPort(){
        return this.port;
    }

    synchronized public void printVertex(){
        System.out.println("--- Vertex Id: " + getID() + " ---");
    }

    public boolean equals(Vertex vertex){
        if(this.peerID == vertex.getID() && this.port == vertex.getPort() && this.IP.equals(vertex.getIP())) {
            return true;
        }else{
            return false;
        }
    }
    @Override
    public int compareTo(Vertex otherVertex) {
        return Integer.compare(getID(), otherVertex.getID());
    }
}
