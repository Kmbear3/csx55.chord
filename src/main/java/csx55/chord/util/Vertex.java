package csx55.chord.util;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import csx55.chord.transport.TCPSender;

public class Vertex {
    private final Socket socket;
    private final String IP;
    private final int port;
    private final String id;
    private int numberOfConnections = 0;
    private boolean taskComplete = false;

    ArrayList<Vertex> vertexConnections;

    public Vertex(String IP, int port, Socket socket){
        this.IP = IP;
        this.port = port;
        this.id = IP + ":" + port;
        this.socket = socket;
        this.vertexConnections = new ArrayList<>();
    }

    public String getID(){
        return this.id;
    }

    synchronized public Socket getSocket(){
        return this.socket;
    }

    synchronized public void sendMessage(byte[] marshalledBytes){
        // Todo - Maybe add check to see if the socket is null first.
        try {
    
            TCPSender send = new TCPSender(this.socket);
            send.sendData(marshalledBytes);
        
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
    }

    public String getIP(){
        return this.IP;
    }

    synchronized public void printVertex(){
        System.out.println("--- Vertex Id: " + getID() + " ---");
        // System.out.println("--- Vertex Socket: " + getSocket() + " ---");
        System.out.print("--- Neighbors: ");
        for(Vertex neighbor : vertexConnections){
            System.out.print(neighbor.getID() + ", ");
        }
        System.out.println(" ---");

        // System.out.println("--- socket: " + this.socket + " ---");
    }

    synchronized public void addNeighbor(Vertex vertex){
        if(this.equals(vertex)){
            System.err.println("Trying to connect to self");
        }

        this.vertexConnections.add(vertex);
        this.numberOfConnections = numberOfConnections + 1;
    }

    public boolean equals(Vertex vertex){
        if(this.id.equals(vertex.getID())) {
            return true;
        }else{
            return false;
        }
    }

    public int getNumberOfConnections(){
        return this.numberOfConnections;
    }

    public ArrayList<Vertex> getVertexConnections(){
        return this.vertexConnections;
    }

    public void setTaskComplete(){
        this.taskComplete = true;
    }

    public boolean isTaskComplete(){
        return this.taskComplete;
    }
}