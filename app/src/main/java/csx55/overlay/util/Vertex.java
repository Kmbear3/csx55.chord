package csx55.overlay.util;

import java.net.Socket;
import java.util.ArrayList;

public class Vertex {
    Socket socket;
    String IP;
    int port;
    String id;

    int numberOfConnections = 0; 
    ArrayList<Vertex> vertexConnections;

    public Vertex(String IP, int port, Socket socket){
        this.IP = IP;
        this.port = port;
        this.id = IP + ":" + port;
        this.socket = socket;
    }

    public String getID(){
        return this.id;
    }

    public Socket getSocket(){
        return this.socket;
    }

    public String getIP(){
        return this.IP;
    }

    public void printVertex(){
        System.out.println("--- Vertex Id: " + getID() + " ---");
        System.out.println("--- Vertex Socket: " + getSocket() + " ---");
        System.out.println("--- Neighbors: " + vertexConnections.toString() + " ---");
    }

    public void addNeighbor(Vertex vertex){
        this.vertexConnections.add(vertex);
        this.numberOfConnections = numberOfConnections + 1;
    }

    public int getNumberOfConnections(){
        return numberOfConnections;
    }
}
