package csx55.threads.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import csx55.threads.node.Node;
import csx55.threads.node.Registry;
import csx55.threads.transport.TCPSender;
import csx55.threads.wireformats.MessagingNodesList;

public class OverlayCreator {
    // Needs registered nodes
    int numberOfConnections;
    VertexList registeredNodes;
    final ArrayList<String> names;
    int[][] linkWeights; 
       
    public OverlayCreator(Registry registry, int numberOfConnections){
        this.numberOfConnections = numberOfConnections;
        this.registeredNodes = registry.getRegistry();
        this.names = registeredNodes.getVertexNames();
        constructOverlay();
    }   

    synchronized public void constructOverlay(){
        try {
            
            int[][] crConnections = assignConnections();
            this.linkWeights = crConnections;

            assignNeighbors(crConnections);

            // Below Just sends the messages

            for(Vertex vertex : registeredNodes.getValues()){
                // vertex.printVertex();
                
                MessagingNodesList nodesList = new MessagingNodesList(vertex.getVertexConnections());
                vertex.sendMessage(nodesList.getBytes());
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    synchronized public void assignNeighbors(int[][] connections){
        // This assigns vertexs to other vertexs. This ensure that the connections are only made in 
        // 1 direction that is above the line of symmetry and has a link value that is non-zero. 
        // i makes a connection to j
        // Above the line of symmetry i == i, j is always greater than i. 

        for(int i = 0; i < connections.length; i ++){
            for(int j = 0; j < connections.length; j++){
                if(i < j && connections[i][j] != 0){
                    Vertex node = registeredNodes.get(names.get(i));
                    node.addNeighbor(registeredNodes.get(names.get(j)));
                }
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

    public int[][] assignConnections(){
        Random rand = new Random();
        int[][] connections = constructRing();
        // int CR = numberOfConnections;
        int CR = connections.length -2;


        while(CR > 1){
            for(int j = 0; j < connections.length; j++){
                if(!isFullyConnected(j, connections, numberOfConnections)){
                    int weight =  rand.nextInt(10) + 1;
                    // if((j + CR) % connections.length == j) {
                    //     continue; // SUS Super sus
                    // }
                    // else{
                        connections[j][(j + CR) % connections.length] = weight;
                        connections[(j + CR) % connections.length][j] = weight;
                    // }
                }
            }

            if(!isBalanced(connections)){
                rewindConnections(CR, connections);
            }

            CR--;
        }

        return connections;
    }

    synchronized public void rewindConnections(int CR, int[][] connections){
        for(int j = 0; j < connections.length; j++){
            connections[j][(j + CR) % connections.length] = 0;
            connections[(j + CR) % connections.length][j] = 0;
        }
    }

    synchronized public void printConnections(int[][] matrix){

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

    synchronized public boolean isFullyConnected(int node, int[][] connections, int CR){
        int[] nodeConnections = connections[node];
        int nodeNumberOfConnections = 0;

        for(int i = 0; i < nodeConnections.length; i ++){
            if(nodeConnections[i] != 0){
                nodeNumberOfConnections++;
            }
        }


        return nodeNumberOfConnections == CR;
    }

    synchronized public boolean isBalanced(int[][] matrix){

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

    public int[][] getConnections(){
        return linkWeights;
    }

    synchronized public ArrayList<String> createLinkInfo(){
        ArrayList<String> linkInfos = new ArrayList<>();

        for(int i = 0;  i < linkWeights.length; i ++ ){
            for(int j = 0; j < linkWeights.length; j ++){
                linkInfos.add(names.get(i) + " " + names.get(j) + " " + linkWeights[i][j]);
            }
        }

        return linkInfos;
    }

    synchronized public void listWeights() {
        for(String connection : createLinkInfo()){
            if(Integer.parseInt(connection.split(" ")[2]) != 0){
                System.out.println(connection);
            }
        }
    }
}
