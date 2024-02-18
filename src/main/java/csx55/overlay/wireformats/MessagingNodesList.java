package csx55.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import csx55.overlay.transport.TCPReceiverThread;
import csx55.overlay.transport.TCPSender;
import csx55.overlay.util.Vertex;

public class MessagingNodesList implements Event, Protocol {
    // Message Type: MESSAGING_NODES_LIST
    // Number of peer messaging nodes: X
    // Messaging node1 Info
    // Messaging node2 Info
    // …..
    // Messaging nodeX Info

    // messagingnode_hostname:portnum

    final int MESSAGE_TYPE = Protocol.MESSAGING_NODES_LIST;
    byte[] marshalledBytes;
    int numberOfPeers;
    ArrayList<Vertex> vertexPeers = new ArrayList<>();


    public MessagingNodesList(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        this.numberOfPeers = din.readInt();

        for(int i = 0; i < this.numberOfPeers; i++){
            int IDLength = din.readInt();
            byte[] IDBytes = new byte[IDLength];
            din.readFully(IDBytes);
            String vertexID = new String(IDBytes);

            String IP = vertexID.substring(0, vertexID.indexOf(":"));
            int port = Integer.parseInt(vertexID.substring(vertexID.indexOf(":") + 1, vertexID.length()));

            Socket peerSocket = new Socket(IP, port);
            
            Vertex peer = new Vertex(IP, port, peerSocket);
            vertexPeers.add(peer);
        }

        baInputStream.close();
        din.close();

    }

    
    public MessagingNodesList(ArrayList<Vertex> vertexPeers){
        this.vertexPeers = vertexPeers;
        this.numberOfPeers = vertexPeers.size();
    }

    @Override
    public int getType() {
        return MESSAGE_TYPE;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(this.MESSAGE_TYPE);   

        dout.writeInt(this.numberOfPeers);   

        for(int i = 0; i < this.numberOfPeers; i++) {
            Vertex vertex = this.vertexPeers.get(i);
            String vertexID = vertex.getID();

            byte[] IDBytes = vertexID.getBytes();

            int elementLength = IDBytes.length;
            dout.writeInt(elementLength);
            dout.write(IDBytes);
        }


        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }

    public ArrayList<Vertex> getPeers() {
        return vertexPeers;
    }
}
