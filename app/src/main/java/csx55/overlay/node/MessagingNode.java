package csx55.overlay.node;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.overlay.transport.TCPReceiverThread;
import csx55.overlay.transport.TCPSender;
import csx55.overlay.transport.TCPServerThread;
import csx55.overlay.util.CLIHandler;
import csx55.overlay.util.MessageSender;
import csx55.overlay.util.Vertex;
import csx55.overlay.util.VertexList;
import csx55.overlay.wireformats.*;

public class MessagingNode implements Node{
    // Maybe move to StatisticsCollectorAndDisplay (?)

    private String messagingNodeIP;
    private int messagingNodePort;

    private TCPServerThread server;
    private TCPSender registrySender;

    private VertexList peerList = new VertexList();
    private ConcurrentLinkedQueue<Message> messagesToProcess = new ConcurrentLinkedQueue<>();

    private int[][] linkWeights;

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

            System.out.println("Inside MessagingNode(IP, port) --- IP: " + this.messagingNodeIP + " --- Port: " + this.messagingNodePort);

            RegistrationRequest regReq = new RegistrationRequest(messagingNodeIP, messagingNodePort);
            registrySender.sendData(regReq.getBytes());

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(Event event, Socket socket) {
        try {
            // System.out.println("Inside MessagingNode.onEvent() --- Type: " + event.getType());
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
                    break;
                default:
                    System.out.println("Protocol Unmatched! " + event.getType());
                    System.exit(0);
                    break;
            }
        } catch (IOException e) {
            System.err.println("Error: MessagingNode.onEvent()");
            e.printStackTrace();
        }
    }

    public void sendMessages(int numberOfRounds){
        MessageSender sender = new MessageSender(this, this.messagesToProcess, numberOfRounds);
        Thread senderThread = new Thread(sender);
        senderThread.start();
    }

    synchronized public void initiatePeerConnections(Event event, Socket socket) throws IOException{
        InitiatePeerConnection peerConnection = new InitiatePeerConnection(event.getBytes());
        Vertex vertex = new Vertex(peerConnection.getIP(), peerConnection.getPort(), socket);
        this.peerList.addToList(vertex);
    }

    synchronized public void createNodeList(Event event) throws IOException{
        MessagingNodesList nodesList = new MessagingNodesList(event.getBytes());

        ArrayList<Vertex> peers = nodesList.getPeers();

        if (peers.size() > 0) {
            for (Vertex peer : peers) {
                this.peerList.addToList(peer);
                sendInitiateConnectionRequest(peer);
            }
        }
    }

    public ConcurrentLinkedQueue<Message> getMessagesToProcess(){
        return this.messagesToProcess;
    }
    synchronized public void sendInitiateConnectionRequest(Vertex vertex) throws IOException {
        InitiatePeerConnection peerConnection = new InitiatePeerConnection(this.messagingNodeIP, this.messagingNodePort);
        Socket peerSocket = vertex.getSocket();

        TCPReceiverThread receiver = new TCPReceiverThread(this, peerSocket);
        Thread receiverThread = new Thread(receiver);
        receiverThread.start();

        TCPSender tcpSender = new TCPSender(peerSocket);
        tcpSender.sendData(peerConnection.getBytes());
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
