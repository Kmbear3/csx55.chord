package csx55.overlay.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import csx55.overlay.node.Node;
import csx55.overlay.node.Registry;
import csx55.overlay.transport.TCPSender;
import csx55.overlay.wireformats.MessagingNodesList;

public class OverlayCreator {
    // Needs registered nodes
    int numberOfConnections;
    VertexList registeredNodes;

    public OverlayCreator(Registry registry, int numberOfConnections){
        this.numberOfConnections = numberOfConnections;
        this.registeredNodes = registry.getRegistry();
        constructOverlay();
        
    }   

    public void constructOverlay(){
        try {
            
            constructRing(registeredNodes);

            for(Vertex vertex : registeredNodes.getValues()){
                vertex.printVertex();
                
                MessagingNodesList nodesList = new MessagingNodesList(vertex.getVertexConnections());
                TCPSender MNSender = new TCPSender(vertex.getSocket());
                MNSender.sendData(nodesList.getBytes());
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // public ArrayList<Vertex> constructPeerLists(Vertex messagingNode){
    //     ArrayList<Vertex> peerList = new ArrayList<>();

    //     return peerList;
    // }

    public void constructRing(VertexList registeVertexList){
        ArrayList<String> names = registeVertexList.getVertexNames();

        int connectionIndex = 1;
        for(int i = 0; i < names.size(); i ++){
            if(i == names.size() - 1){
                connectionIndex = 0;
            }
            
            Vertex node = registeVertexList.get(names.get(i)); 
            node.addNeighbor(registeVertexList.get(names.get(connectionIndex)));

            // System.out.println("Connection Index: " + connectionIndex);
            // System.out.println("Index: " + i);
            // node.printVertex();

            connectionIndex++;
        }
    }
}
