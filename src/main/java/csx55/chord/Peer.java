package csx55.chord;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.chord.node.Node;
import csx55.chord.transport.TCPReceiverThread;
import csx55.chord.transport.TCPSender;
import csx55.chord.transport.TCPServerThread;
import csx55.chord.util.CLIHandler;
import csx55.chord.util.StatisticsCollectorAndDisplay;
import csx55.chord.util.Vertex;
import csx55.chord.util.VertexList;
import csx55.chord.wireformats.*;

public class Peer implements Node{

    private String peerIP;
    private int peerPort;

    private int peerID; 

    private TCPServerThread server;
    private TCPSender registrySender;

    private VertexList peerList = new VertexList();
    // private ConcurrentLinkedQueue<Task> tasks = new ConcurrentLinkedQueue<>();

    StatisticsCollectorAndDisplay stats = new StatisticsCollectorAndDisplay();

    public Peer(String registryIP, int registryPort){
        try {
            Socket registrySocket = new Socket(registryIP, registryPort);
            this.registrySender = new TCPSender(registrySocket);
            TCPReceiverThread registryReceiver = new TCPReceiverThread(this, registrySocket);
            Thread registryReceiverThread = new Thread(registryReceiver);
            registryReceiverThread.start();

            configureServer(this);

            this.peerIP = this.server.getIP();
            this.peerPort = this.server.getPort();
            this.peerID = getName().hashCode();


            System.out.println("My IP: " + this.peerIP + "\nMy Port: " + this.peerPort + "\nMy PeerID: " + this.peerID);

            RegistrationRequest regReq = new RegistrationRequest(peerIP, peerPort, peerID);
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
                    // Deal with collisions here

                    break;
                // case Protocol.INITIATE_PEER_CONNECTION:
                //     initiatePeerConnections(event, socket);
                //     break;
                case Protocol.POKE:
                    Poke poke = new Poke(event.getBytes());
                    poke.printPoke();
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

    // synchronized public void initiatePeerConnections(Event event, Socket socket) throws IOException{
    //     InitiatePeerConnection peerConnection = new InitiatePeerConnection(event.getBytes());
    //     Vertex vertex = new Vertex(peerConnection.getIP(), peerConnection.getPort(), socket);

    //     this.peerList.addToList(vertex);
    // }


    // synchronized public void sendInitiateConnectionRequest(Vertex vertex) throws IOException {
    //     InitiatePeerConnection peerConnection = new InitiatePeerConnection(this.messagingNodeIP, this.messagingNodePort);

    //     TCPReceiverThread receiver = new TCPReceiverThread(this,  vertex.getSocket());
    //     Thread receiverThread = new Thread(receiver);
    //     receiverThread.start();

    //     vertex.sendMessage(peerConnection.getBytes());
    // }

    public void configureServer(Node node){
        this.server = new TCPServerThread(node); 
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    public String getMessagingNodeIP(){
        return this.peerIP;
    }

    public int getMessagingNodePort(){
        return this.peerPort;
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

    public String getName(){
        return this.peerIP + ":" + this.peerPort;
    }

    synchronized public StatisticsCollectorAndDisplay getStats(){
        return this.stats;
    }

    synchronized public void sendRegistryMessage(Event event) throws IOException{
        this.registrySender.sendData(event.getBytes());
    }

    public static void main(String[] args){
        String registryName = args[0];
        int registryPort = Integer.parseInt(args[1]);
        Peer peerNode = new Peer(registryName, registryPort);

        CLIHandler cliHandler = new CLIHandler(peerNode);

         while(true){
              cliHandler.readInstructionsMessagingNode();
         }
    }
}
