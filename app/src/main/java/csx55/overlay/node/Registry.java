package csx55.overlay.node;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import csx55.overlay.transport.TCPServerThread;
import csx55.overlay.util.CLIHandler;
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
    public void onEvent(Event event, Socket socket) {
        System.out.println("Inside Registry.onEvent() --- Type: " + event.getType());

        switch(event.getType()){
            case Protocol.MESSAGE:
                System.out.println("MESSAGE");
                break;
            case Protocol.REGISTER_REQUEST:
                vertexList.registerVertex(event, socket);
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

    // TODO: BAD BAD BAD NOT THREADSAFE FIXXXX MEEEEEEE  
    public VertexList getRegistry(){
        return vertexList;
    }

    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);
        Registry registry = new Registry(port);

        CLIHandler cli = new CLIHandler(registry);
        while(true){
            cli.readInstructions();
        }
    }
}
