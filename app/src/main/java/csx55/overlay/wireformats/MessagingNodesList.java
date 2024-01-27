package csx55.overlay.wireformats;

import java.io.IOException;
import java.util.ArrayList;

import csx55.overlay.util.Vertex;

public class MessagingNodesList implements Event, Protocol {
    // Message Type: MESSAGING_NODES_LIST
    // Number of peer messaging nodes: X
    // Messaging node1 Info
    // Messaging node2 Info
    // …..
    // Messaging nodeX Info

    // messagingnode_hostname:portnum

    byte[] marshalledBytes;
    ArrayList<Vertex> vertexPeers;

    public MessagingNodesList(byte[] marshalledBytes){

    }

    public MessagingNodesList(ArrayList<Vertex> vertexPeers){
        this.vertexPeers = vertexPeers;
    }


    @Override
    public int getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    }



    @Override
    public byte[] getBytes() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBytes'");
    }
}
