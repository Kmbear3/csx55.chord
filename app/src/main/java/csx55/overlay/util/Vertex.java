package csx55.overlay.util;

import java.net.Socket;

public class Vertex {
    String IP;
    int port;
    String id;

    public Vertex(String IP, int port){
        this.IP = IP;
        this.port = port;
        this.id = IP + ":" + port;
    }

    public String getID(){
        return id;
    }
}
