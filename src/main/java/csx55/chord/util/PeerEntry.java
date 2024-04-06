package csx55.chord.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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

    public void print(){
        System.out.println(this.peerID + " " + this.IP + ":" + this.port);
    }

    public String toString(){
        return this.peerID + " " + this.IP + ":" + this.port;
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


    public void marshallPeer(DataOutputStream dout){
        try {
            String peerIP = this.getIP();

            byte[] peerBytes = peerIP.getBytes();
            int length = peerBytes.length;
            dout.writeInt(length);
            dout.write(peerBytes);

            int peerPort = this.getPort();
            int ftPeerID = this.getID();

            dout.writeInt(peerPort);
            dout.writeInt(ftPeerID);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static PeerEntry unmarshallPeer(DataInputStream din){
        try{
            int peerIPLength = din.readInt();
            byte[] peerIPBytes = new byte[peerIPLength];
            din.readFully(peerIPBytes);
            String peerIP = new String(peerIPBytes);

            int peerPort = din.readInt();
            int peerID = din.readInt();

            PeerEntry peer = new PeerEntry(peerIP, peerPort, peerID);
            return peer;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
        return null;
    }


    public boolean equals(PeerEntry peer){
        return peer.IP.equals(this.IP) && peer.port == this.port && peer.peerID == this.peerID;
    }
}
