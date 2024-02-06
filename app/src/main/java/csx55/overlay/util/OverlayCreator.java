package csx55.overlay.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import csx55.overlay.node.Node;
import csx55.overlay.node.Registry;
import csx55.overlay.transport.TCPSender;
import csx55.overlay.wireformats.MessagingNodesList;

public class OverlayCreator {
    // Needs registered nodes
    int numberOfConnections;
    VertexList registeredNodes;
    final ArrayList<String> names;
       
    public OverlayCreator(Registry registry, int numberOfConnections){
        this.numberOfConnections = numberOfConnections;
        this.registeredNodes = registry.getRegistry();
        this.names = registeredNodes.getVertexNames();
        constructOverlay();
    }   

    public void constructOverlay(){
        try {
            
            System.out.println("Setting up overlay\n Number of nodes: " + names.size() + " \nConnection Directive: " + numberOfConnections);


            int[][] crConnections = assignConnections();

            printConnections(crConnections);


            assignNeighbors(crConnections);
            
        
            // Below Just sends the messages

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

    synchronized public void assignNeighbors(int[][] connections){

        // Use registeredNodes

        Vertex node = registeredNodes.get(name); 
        node.addNeighbor(registeredNodes.get(name));




    }


    public int[][] constructRing(){
        int[][] connections = new int[this.names.size()][this.names.size()];
        Random rand = new Random();

        for(int i = 0; i < connections.length; i++){
            int linkWeight = rand.nextInt(10) + 1;

            connections[i][(i + 1) % connections.length] = linkWeight;
            connections[(i + 1) % connections.length][i] = linkWeight;  
        }
        return connections;
    }

    public int[][] assignConnections(){
        Random rand = new Random();

        int[][] connections = constructRing();

        int CR = numberOfConnections;

        while(CR > 1){
            for(int j = 0; j < connections.length; j++){
                if(!isFullyConnected(j, connections, numberOfConnections)){
                    int weight =  rand.nextInt(10) + 1;
                    if((j + CR) % connections.length == j) {
                        continue;
                    }
                    else{
                        connections[j][(j + CR) % connections.length] = weight;
                        connections[(j + CR) % connections.length][j] = weight;
                    }
                }
            }

            if(!isBalanced(connections)){
                rewindConnections(CR, connections);
            }

            CR--;
        }

        return connections;
    }

    public void rewindConnections(int CR, int[][] connections){
        for(int j = 0; j < connections.length; j++){
            if(!isFullyConnected(j, connections, CR)){
                connections[j][(j + CR) % connections.length] = 0;
                connections[(j + CR) % connections.length][j] = 0;
            }
        }
    }

    public void printConnections(int[][] matrix){
        for(String name : names){
            System.out.print(" | " + name + " | ");
        }
        System.out.println();

        System.out.print(" | - | ");
        for(int i = 0; i < names.size(); i ++){
            System.out.print(" | " + i + " | ");
        }
        System.out.println();

        for(int i = 0; i < matrix.length; i++){
            System.out.print(" | " + i + " | ");
            for(int j = 0; j < matrix.length; j ++){
                System.out.print(" | " + matrix[i][j] + " | ");
            }
            System.out.println();
        }
    }

    public boolean isFullyConnected(int node, int[][] connections, int CR){
        int[] nodeConnections = connections[node];
        int nodeNumberOfConnections = 0;

        for(int i = 0; i < nodeConnections.length; i ++){
            if(nodeConnections[i] != 0){
                nodeNumberOfConnections++;
            }
        }

        return nodeNumberOfConnections == CR;
    }

    public boolean isBalanced(int[][] matrix){

        int[] nodeConnections = matrix[0];
        int nodeNumberOfConnections = 0;

        for(int i = 0; i < nodeConnections.length; i ++){
            if(nodeConnections[i] != 0){
                nodeNumberOfConnections++;
            }
        }

        for(int i = 0; i < matrix.length; i++){
            int rowCount = 0;
            for(int j = 0; j < matrix.length; j ++){
                if(matrix[i][j] != 0){
                    rowCount++;
                }
            }
            if(rowCount != nodeNumberOfConnections){
                return false;
            }
        }

        return true;
    }


     // public void constructRing(VertexList registeVertexList){
    //     ArrayList<String> names = registeVertexList.getVertexNames();

    //     int connectionIndex = 1;
    //     for(int i = 0; i < names.size(); i ++){
    //         if(i == names.size() - 1){
    //             connectionIndex = 0;
    //         }
            
    //         Vertex node = registeVertexList.get(names.get(i)); 
    //         node.addNeighbor(registeVertexList.get(names.get(connectionIndex)));

    //         connectionIndex++;
    //     }
    // }

}
