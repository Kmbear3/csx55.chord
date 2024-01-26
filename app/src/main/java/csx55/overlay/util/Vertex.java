package csx55.overlay.util;

import java.net.Socket;

public class Vertex {
    Socket socket;
    String IP;
    int port;
    String id;

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

    public void printVertex(){
        System.out.println("--- Vertex Id: " + getID() + "---");
        System.out.println("--- Vertex Socket: " + getSocket() + "---");
    }
}
