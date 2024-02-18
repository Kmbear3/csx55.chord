package csx55.overlay.node;

import java.io.IOException;
import java.net.Socket;

import csx55.overlay.transport.TCPSender;
import csx55.overlay.transport.TCPServerThread;
import csx55.overlay.util.CLIHandler;
import csx55.overlay.util.StatisticsCollectorAndDisplay;
import csx55.overlay.util.StatusCodes;
import csx55.overlay.util.Vertex;
import csx55.overlay.util.VertexList;
import csx55.overlay.wireformats.Deregister;
import csx55.overlay.wireformats.DeregisterResponse;
import csx55.overlay.wireformats.Event;
import csx55.overlay.wireformats.Protocol;
import csx55.overlay.wireformats.TaskComplete;
import csx55.overlay.wireformats.TaskSummaryRequest;

public class Registry implements Node {
    int port;
    VertexList vertexList;
    StatisticsCollectorAndDisplay stats;

    public Registry(int port){
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
                case Protocol.TASK_INITIATE:
                    vertexList.sendAllNodes(event);
                    break;
                case Protocol.TASK_COMPLETE:
                    checkNodesStatus(event);
                    break;
                case Protocol.TRAFFIC_SUMMARY:
                    stats.nodeStats(event);

                    if(stats.receivedAllStats()){
                        stats.displayTotalSums();
                    }
                    break;
                case Protocol.DEREGISTER_REQUEST:
                    Deregister deregister = new Deregister(event.getBytes());
                    deregisterNode(deregister, socket);
                    break;
                default:
                    System.out.println("Protocol Unmatched!");
                    System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public void deregisterNode(Deregister deregister, Socket socket){
        try {

            String additionalInfo = vertexList.deregisterVertex(deregister.getID(), deregister.getIP(), socket);
            DeregisterResponse deregisterResponse = new DeregisterResponse(deregister.getStatus(), additionalInfo);

            TCPSender send = new TCPSender(socket);
            send.sendData(deregisterResponse.getBytes());


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public void checkNodesStatus(Event event){
        try {
            TaskComplete task = new TaskComplete(event.getBytes());
            Vertex vertex = this.vertexList.get(task.getID());
            vertex.setTaskComplete();

            if(vertexList.allTasksAreComplete()){
                Thread.sleep(15000);
                
                TaskSummaryRequest summaryRequest = new TaskSummaryRequest();
                vertexList.sendAllNodes(summaryRequest);
            }
        
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    synchronized public void sendAllNodes(Event event){
        vertexList.sendAllNodes(event);
    }

    public void closedConnection(String socketId){
        vertexList.removeFromList(socketId);
    }   

    public void printRegistry(){
        vertexList.printVertexList();
    }

    public static void main(String[] args){
        int port = Integer.parseInt(args[0]);
        Registry registry = new Registry(port);

        CLIHandler cli = new CLIHandler(registry);
        while(true){
            cli.readInstructionsRegistry();
        }
    }
}
