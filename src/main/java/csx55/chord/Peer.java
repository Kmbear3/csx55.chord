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
import csx55.chord.util.FingerTable;
import csx55.chord.util.PeerEntry;
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
    private FingerTable fingerTable; 

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
                    handleRegisterationResponse(regRes);
                    break;
                case Protocol.INSERT_REQUEST:
                    this.fingerTable.handleNodeAdditionRequest(new InsertRequest(event.getBytes()));
                    break;
                case Protocol.INSERT_RESPONSE:
                    this.fingerTable.createFingerTableWithSuccessorInfo(new InsertResponse(event.getBytes()));
                    break;
                case Protocol.NEW_SUCCESSOR:
                    this.fingerTable.newSucessor(new NewSuccessor(event.getBytes()));
                    break;
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

    public void configureServer(Node node){
        this.server = new TCPServerThread(node); 
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    public void handleRegisterationResponse(RegisterationResponse regRes){
        int registeredPeerID = regRes.getPeerID();
        System.out.println("Registered ID: " + registeredPeerID);

        if(registeredPeerID != this.peerID){
            this.peerID = registeredPeerID;
        }

        // Handle creating findertable
        Vertex responsePeer = regRes.getVertex();

        PeerEntry me = new PeerEntry(this.peerIP, this.peerPort, this.peerID);

        if(responsePeer.getID() == this.peerID){
            this.fingerTable = new FingerTable(me);
        }
        else this.fingerTable = new FingerTable(responsePeer, me);
    }

    public String getMessagingNodeIP(){
        return this.peerIP;
    }

    public int getMessagingNodePort(){
        return this.peerPort;
    }

    public String getName(){
        return this.peerIP + ":" + this.peerPort;
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

    public void printFingerTable() {
        this.fingerTable.print();
    }
}
