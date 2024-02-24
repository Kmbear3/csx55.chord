package csx55.threads.node;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.threads.transport.TCPReceiverThread;
import csx55.threads.transport.TCPSender;
import csx55.threads.transport.TCPServerThread;
import csx55.threads.util.CLIHandler;
import csx55.threads.util.MessageSender;
import csx55.threads.util.StatisticsCollectorAndDisplay;
import csx55.threads.util.Vertex;
import csx55.threads.util.VertexList;
import csx55.threads.wireformats.*;
import csx55.threads.balancing.BalanceLoad;
import csx55.threads.computing.TaskManager;
import csx55.threads.computing.TaskPool;
import csx55.threads.hashing.*;

public class ComputeNode implements Node{

    private String messagingNodeIP;
    private int messagingNodePort;

    private TCPServerThread server;
    private TCPSender registrySender;

    private VertexList peerList = new VertexList();
    private ConcurrentLinkedQueue<Task> tasks = new ConcurrentLinkedQueue<>();

    private MessageSender sender;
    private TaskPool threadPool;
    private Vertex clockwiseNeighbor;

    private BalanceLoad balancer;


    StatisticsCollectorAndDisplay stats = new StatisticsCollectorAndDisplay();

    public ComputeNode(String registryIP, int registryPort){
        try {
            Socket registrySocket = new Socket(registryIP, registryPort);
            this.registrySender = new TCPSender(registrySocket);
            TCPReceiverThread registryReceiver = new TCPReceiverThread(this, registrySocket);
            Thread registryReceiverThread = new Thread(registryReceiver);
            registryReceiverThread.start();

            configureServer(this);

            this.messagingNodeIP = this.server.getIP();
            this.messagingNodePort = this.server.getPort();

            RegistrationRequest regReq = new RegistrationRequest(messagingNodeIP, messagingNodePort);
            registrySender.sendData(regReq.getBytes());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(Event event, Socket socket) {
        try {
            switch(event.getType()){
                case Protocol.REGISTER_RESPONSE:
                    RegisterationResponse regRes = new RegisterationResponse(event.getBytes());
                    regRes.getInfo();
                    break;
                case Protocol.INITIATE_PEER_CONNECTION:
                    initiatePeerConnections(event, socket);
                    break;
                case Protocol.MESSAGING_NODES_LIST:
                    createConnectionsAndThreadPool(event);
                    break;
                case Protocol.TASK_INITIATE:
                    TaskInitiate task = new TaskInitiate(event.getBytes());
                    createTasks(task.getNumberOfRounds());
                    break;
                case Protocol.POKE:
                    Poke poke = new Poke(event.getBytes());
                    poke.printPoke();
                    break;
                case Protocol.PULL_TRAFFIC_SUMMARY:
                    TaskSummaryResponse summaryResponse = new TaskSummaryResponse(messagingNodeIP, messagingNodePort, this.stats);
                    registrySender.sendData(summaryResponse.getBytes());
                    stats.resetCounters();
                    break;
                case Protocol.DEREGISTER_RESPONSE:
                    DeregisterResponse deResponse = new DeregisterResponse(event.getBytes());

                    System.out.println(deResponse.getAdditionalInfo());

                    if(deResponse.exitOverlay()){
                        System.exit(0);
                    }
                    break;
                case Protocol.NODE_TASKS:
                    balancer.addToSum((NodeTasks)event);
                    break;
                case Protocol.TASKS:
                    balancer.receiveTasks((Tasks)event);
                    break;
                default:
                    System.out.println("Protocol Unmatched! " + event.getType());
                    System.out.println("Please try again");
                    break;
            }
        } catch (IOException e) {
            System.err.println("Error: MessagingNode.onEvent()");
            e.printStackTrace();
        }
    }

    private void createTasks(int numberOfRounds) {
        TaskManager taskManager = new TaskManager(numberOfRounds, this, this.tasks, this.balancer);
        Thread taskManagerThread = new Thread(taskManager);
        taskManagerThread.start();
    }

    synchronized public void initiatePeerConnections(Event event, Socket socket) throws IOException{
        InitiatePeerConnection peerConnection = new InitiatePeerConnection(event.getBytes());
        Vertex vertex = new Vertex(peerConnection.getIP(), peerConnection.getPort(), socket);

        this.peerList.addToList(vertex);
    }

    synchronized public void createConnectionsAndThreadPool(Event event) throws IOException{
        // Sketchy below:
        // The code to initiate clockwise neighbor is not very good. Refactor and re-do. 
        // There isn't necesarily a garantee that there is only 1 value in peers object. 
        // change logic!!! 
        // TODO: Be better, noob

        MessagingNodesList nodesList = (MessagingNodesList)event;

        ArrayList<Vertex> peers = nodesList.getPeers();
        
        for (Vertex peer : peers) {
            this.peerList.addToList(peer);
            sendInitiateConnectionRequest(peer);
        }
        
        System.out.println("All connections are established. Number of connections: " + peers.size());
        System.out.println("Clockwise neighbor: " + peers.get(0).getID());
        this.clockwiseNeighbor = peers.get(0); 

        this.balancer = new BalanceLoad(nodesList.getNumberOfNodes(), this, this.tasks);

        this.threadPool = new TaskPool(this, tasks, nodesList.getNumberOfThreads());
        threadPool.createThreads();
    }


    synchronized public void sendClockwise(byte[] marshalledBytes){
        clockwiseNeighbor.sendMessage(marshalledBytes);
    }

    synchronized public void sendInitiateConnectionRequest(Vertex vertex) throws IOException {
        InitiatePeerConnection peerConnection = new InitiatePeerConnection(this.messagingNodeIP, this.messagingNodePort);

        TCPReceiverThread receiver = new TCPReceiverThread(this,  vertex.getSocket());
        Thread receiverThread = new Thread(receiver);
        receiverThread.start();

        vertex.sendMessage(peerConnection.getBytes());
    }

    public void configureServer(Node node){
        this.server = new TCPServerThread(node); 
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    public String getMessagingNodeIP(){
        return this.messagingNodeIP;
    }

    public int getMessagingNodePort(){
        return this.messagingNodePort;
    }

    public VertexList getPeerList(){
        return this.peerList;
    }

    synchronized public String getRandomPeerID(){
        Random rand = new Random();
        int randomPeer = rand.nextInt(peerList.size());
        ArrayList<String> peerNames = peerList.getVertexNames();
        return peerNames.get(randomPeer);
    }

    public String getID(){
        return this.messagingNodeIP + ":" + this.messagingNodePort;
    }


    synchronized public void printConnections(int[][] matrix){

        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix.length; j ++){
                System.out.print(" | " + matrix[i][j] + " | ");
            }
            System.out.println();
        }

    }

    synchronized public void sendRegistryMessage(Event event) throws IOException{
        this.registrySender.sendData(event.getBytes());
    }


    public MessageSender getSender(){
        return this.sender;
    }

    synchronized public boolean originated(Task task){
        if(task.getIp().equals(messagingNodeIP) && task.getPort() == messagingNodePort){
            return true;
        }
        else{
            return false;
        }
    }

    public static void main(String[] args){
        String registryName = args[0];
        int registryPort = Integer.parseInt(args[1]);
        ComputeNode messagingNode = new ComputeNode(registryName, registryPort);

         CLIHandler cliHandler = new CLIHandler(messagingNode);

         while(true){
              cliHandler.readInstructionsMessagingNode();
         }
    }
}
