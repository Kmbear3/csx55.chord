package csx55.chord;

import java.net.Socket;
import csx55.chord.node.Node;
import csx55.chord.transport.TCPServerThread;
import csx55.chord.util.CLIHandler;
import csx55.chord.util.StatisticsCollectorAndDisplay;
import csx55.chord.util.VertexList;
import csx55.chord.wireformats.Event;
import csx55.chord .wireformats.Protocol;
import csx55.chord .wireformats.Deregister;


public class Discovery implements Node {
    int port;
    VertexList vertexList;
    StatisticsCollectorAndDisplay stats;

    public Discovery(int port){
        System.out.println("Creating Registry, Listening for Connections. Port: " + port);
        this.port = port;
        configureServer(this, port);
        vertexList = new VertexList();
        this.stats = new StatisticsCollectorAndDisplay(vertexList);
    }

    @Override
    public void onEvent(Event event, Socket socket) {
        try {
            switch(event.getType()){
                case Protocol.REGISTER_REQUEST:
                    vertexList.registerVertex(event, socket);
                    break;
                case Protocol.DEREGISTER_REQUEST:
                    Deregister deregister = new Deregister(event.getBytes());
                    deregisterNode(deregister, socket);
                    break;
                default:
                    System.out.println("Protocol Unmatched!");
                    System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void deregisterNode(Deregister deregister, Socket socket){
        vertexList.deregisterVertex(deregister.getPeerID());
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

    synchronized public void sendAllNodes(Event event){
        vertexList.sendAllNodes(event);
    }

    public void closedConnection(String socketId){
        vertexList.removeFromList(socketId);
    }   

    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);
        Discovery registry = new Discovery(port);

        CLIHandler cli = new CLIHandler(registry);
        while(true){
            cli.readInstructionsRegistry();
        }
    }

    public void printPeerNodes() {
        vertexList.printVertexList();
    }
}
