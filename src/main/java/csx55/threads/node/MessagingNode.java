package csx55.threads.node;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.threads.dijkstra.ShortestPath;
import csx55.threads.transport.TCPReceiverThread;
import csx55.threads.transport.TCPSender;
import csx55.threads.transport.TCPServerThread;
import csx55.threads.util.CLIHandler;
import csx55.threads.util.MessageSender;
import csx55.threads.util.OverlayCreator;
import csx55.threads.util.StatisticsCollectorAndDisplay;
import csx55.threads.util.Vertex;
import csx55.threads.util.VertexList;
import csx55.threads.wireformats.*;

public class MessagingNode implements Node{

    private String messagingNodeIP;
    private int messagingNodePort;

    private TCPServerThread server;
    private TCPSender registrySender;

    private VertexList peerList = new VertexList();
    private ConcurrentLinkedQueue<Message> messagesToProcess = new ConcurrentLinkedQueue<>();

    private int[][] linkWeights;

    private String[] names;

    private MessageSender sender;

    StatisticsCollectorAndDisplay stats = new StatisticsCollectorAndDisplay();

    public MessagingNode(String registryIP, int registryPort){
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
                case Protocol.MESSAGE:
                    Message message = new Message(event.getBytes());
                    messagesToProcess.add(message);
                    break;
                case Protocol.REGISTER_RESPONSE:
                    RegisterationResponse regRes = new RegisterationResponse(event.getBytes());
                    regRes.getInfo();
                    break;
                case Protocol.INITIATE_PEER_CONNECTION:
                    initiatePeerConnections(event, socket);
                    break;
                case Protocol.MESSAGING_NODES_LIST:
                    createNodeList(event);
                    break;
                case Protocol.TASK_INITIATE:
                    TaskInitiate task = new TaskInitiate(event.getBytes());
                    sendMessages(task.getNumberOfRounds());
                    break;
                case Protocol.POKE:
                    Poke poke = new Poke(event.getBytes());
                    poke.printPoke();
                    break;
                case Protocol.Link_Weights:
                    LinkWeights linkWeights = new LinkWeights(event.getBytes());
                    this.linkWeights = linkWeights.getConnections();
                    this.names = linkWeights.getNames();
                    
                    this.sender = new MessageSender(this, this.messagesToProcess, this.linkWeights, this.names, this.stats);
                    System.out.println("Link weights received and processed. Ready to send messages.");
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

    public void sendMessages(int numberOfRounds){
        // this.sender = new MessageSender(this, this.messagesToProcess, numberOfRounds, this.linkWeights, this.names, this.stats);
        this.sender.setNumberOfRound(numberOfRounds);
        Thread senderThread = new Thread(sender);
        senderThread.start();
    }

    synchronized public void initiatePeerConnections(Event event, Socket socket) throws IOException{
        InitiatePeerConnection peerConnection = new InitiatePeerConnection(event.getBytes());
        Vertex vertex = new Vertex(peerConnection.getIP(), peerConnection.getPort(), socket);

        this.peerList.addToList(vertex);
    }

    synchronized public void createNodeList(Event event) throws IOException{
        MessagingNodesList nodesList = (MessagingNodesList)event;

        ArrayList<Vertex> peers = nodesList.getPeers();
        
        for (Vertex peer : peers) {
            this.peerList.addToList(peer);
            sendInitiateConnectionRequest(peer);
        }
        
        System.out.println("All connections are established. Number of connections: " + peers.size());
    }

    public ConcurrentLinkedQueue<Message> getMessagesToProcess(){
        return this.messagesToProcess;
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

    public void printShortestPaths() {
        this.sender.printShortestPaths();
    }

    public MessageSender getSender(){
        return this.sender;
    }

    public static void main(String[] args){
        String registryName = args[0];
        int registryPort = Integer.parseInt(args[1]);
        MessagingNode messagingNode = new MessagingNode(registryName, registryPort);

         CLIHandler cliHandler = new CLIHandler(messagingNode);

         while(true){
              cliHandler.readInstructionsMessagingNode();
         }
    }
}