package csx55.chord.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import csx55.chord.Discovery;
import csx55.chord.node.Node;
import csx55.chord.transport.TCPSender;
import csx55.chord.wireformats.MessagingNodesList;

public class OverlayCreator {
    // Needs registered nodes
    int numberOfThreads;
    VertexList registeredNodes;
    final ArrayList<String> names;
    int[][] linkWeights; 
       
    public OverlayCreator(Discovery registry, int numberOfThreads){
        this.numberOfThreads = numberOfThreads;
        this.registeredNodes = registry.getRegistry();
        this.names = registeredNodes.getVertexNames();
        constructOverlay();
    }   

    synchronized public void constructOverlay(){
        try {
            
            int[][] crConnections = constructRing();
            this.linkWeights = crConnections;

            assignNeighbors(crConnections);

            // Below Just sends the messages

            for(Vertex vertex : registeredNodes.getValues()){
                
                MessagingNodesList nodesList = new MessagingNodesList(vertex.getVertexConnections(), this.numberOfThreads, registeredNodes.size());
                vertex.sendMessage(nodesList.getBytes());
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    synchronized public void assignNeighbors(int[][] connections){

        for(int i = 0; i < connections.length; i ++){
            int clockwiseNeighbor = (i+1) % connections.length;
           
            if(connections[i][clockwiseNeighbor] != 0){
                Vertex node = registeredNodes.get(names.get(i));
                node.addNeighbor(registeredNodes.get(names.get(clockwiseNeighbor)));
            }
        }
    }

    synchronized public int[][] constructRing(){
        int[][] connections = new int[this.names.size()][this.names.size()];
        Random rand = new Random();

        for(int i = 0; i < connections.length; i++){
            int linkWeight = rand.nextInt(10) + 1;

            connections[i][(i + 1) % connections.length] = linkWeight;
            connections[(i + 1) % connections.length][i] = linkWeight;  
        }
        return connections;
    }
}

