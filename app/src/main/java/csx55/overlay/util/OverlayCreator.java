package csx55.overlay.util;

import java.io.IOException;
import java.util.ArrayList;

import csx55.overlay.node.Node;
import csx55.overlay.node.Registry;
import csx55.overlay.transport.TCPSender;
import csx55.overlay.wireformats.MessagingNodesList;

public class OverlayCreator {
    // Needs registered nodes
    int numberOfConnections;
    Registry registry;

    public OverlayCreator(Registry registry, int numberOfConnections){
        this.numberOfConnections = numberOfConnections;
        this.registry = registry;
        constructOverlay();
    }   

    public void constructOverlay(){
        try {
            VertexList registeredList = registry.getRegistry();

            for(Vertex vertex : registeredList.getValues()){
                vertex.printVertex();
                ArrayList<Vertex> peerList = constructPeerLists(registeredList);
                
                MessagingNodesList nodesList = new MessagingNodesList(peerList);
                TCPSender MNSender = new TCPSender(vertex.getSocket());
                MNSender.sendData(nodesList.getBytes());
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public ArrayList<Vertex> constructPeerLists(VertexList vertexList){
        ArrayList<Vertex> peerList = new ArrayList<>();


        return peerList;
    }



}
