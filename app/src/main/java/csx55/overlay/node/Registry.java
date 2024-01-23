package csx55.overlay.node;

import java.util.concurrent.ConcurrentHashMap;

import csx55.overlay.transport.TCPServerThread;
import csx55.overlay.util.VertexList;
import csx55.overlay.wireformats.Event;
import csx55.overlay.wireformats.Protocol;

public class Registry implements Node {
    int port;
    VertexList vertexList;

    public Registry(int port){
        System.out.println("Creating Registry");
        this.port = port;
        configureServer(this, port);
        vertexList = new VertexList();
    }

    @Override
    public void onEvent(Event event) {
        System.out.println("Inside Registry.onEvent() --- Type: " + event.getType());

        switch(event.getType()){
            case Protocol.MESSAGE:
                System.out.println("MESSAGE");
                break;
            case Protocol.REGISTER_REQUEST:
                vertexList.registerVertex(event);
                break;
            default:
                System.out.println("Protocol Unmatched!");
                System.exit(0);
        }
    }

    public void configureServer(Node node, int port){
        TCPServerThread tcpServer = new TCPServerThread(this, port);
        Thread serverThread = new Thread(tcpServer);
        serverThread.start();
    }

    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);
        Registry registry = new Registry(port);
    }
}
