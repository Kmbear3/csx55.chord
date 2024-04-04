package csx55.chord.util;

import java.io.IOException;
import java.net.Socket;

import csx55.chord.transport.TCPSender;

public class PeerEntry {
    String IP;
    int port; 
    Socket socket;
    int peerID; 

    public PeerEntry(String IP, int port, Socket socket, int peerID){
        this.IP = IP;
        this.port = port;
        this.socket = socket;
        this.peerID = peerID;
    }

    public PeerEntry(String IP, int port, int peerID){
        this.IP = IP;
        this.port = port;
        this.peerID = peerID;
    }

    public String getIP(){
        return this.IP;
    }

    public int getPort(){
        return this.port;
    }

    public int getID(){
        return this.peerID;
    }

   synchronized public void sendMessage(byte[] marshalledBytes){
        try {
            if(this.socket == null){
                this.socket = new Socket(this.IP, this.port);
            }
    
            TCPSender send = new TCPSender(this.socket);
            send.sendData(marshalledBytes);
        
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
    }
}
