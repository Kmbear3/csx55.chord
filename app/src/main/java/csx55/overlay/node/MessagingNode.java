package csx55.overlay.node;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import csx55.overlay.transport.TCPReceiverThread;
import csx55.overlay.transport.TCPSender;
import csx55.overlay.transport.TCPServerThread;
import csx55.overlay.util.CLIHandler;
import csx55.overlay.util.Vertex;
import csx55.overlay.util.VertexList;
import csx55.overlay.wireformats.*;

public class MessagingNode implements Node{
    // Maybe move to StatisticsCollectorAndDisplay (?)
    int sendTracker = 0;  // number of messages sent
    int receiveTracker = 0;  // number of messages that were received
    int relayTracker = 0; // Number of messages that were relayed.
    long sendSummation = 0; // Sum of value that it has sent 
    long receiveSummation = 0;  // Sum of the payloads that it has received 

    String messagingNodeIP;
    int messagingNodePort; 

    TCPServerThread server;
    TCPSender registrySender;

    VertexList peerList = new VertexList();

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
                    System.out.println("MESSAGE");
                    Message message = new Message(event.getBytes());
                    receivedMessage(message, socket);
                    break;
                case Protocol.REGISTER_RESPONSE:
                    RegisterationResponse regRes = new RegisterationResponse(event.getBytes());
                    regRes.getInfo();
                    break;

                case Protocol.INITIATE_PEER_CONNECTION:
                    InitiatePeerConnection peerConnection = new InitiatePeerConnection(event.getBytes());
                    Vertex vertex = new Vertex(peerConnection.getIP(), peerConnection.getPort(), socket);
                    this.peerList.addToList(vertex);
                    break;

                case Protocol.MESSAGING_NODES_LIST:
                    MessagingNodesList nodesList = new MessagingNodesList(event.getBytes());
                    
                    ArrayList<Vertex> peers = nodesList.getPeers();

                    if(peers.size() > 0){
                        for(Vertex peer : peers){
                            this.peerList.addToList(peer);
                            sendInitiateConnectionRequest(peer);
                        }
                    }

                    peerList.printVertexList();
                    System.out.println("All connections are established. Number of connections: " + peers.size());

                    Thread.sleep(3000);

                    System.out.print("Connection: " );
                    peerList.printVertexList();
                    break;
                case Protocol.TASK_INITIATE:
                    TaskInitiate task = new TaskInitiate(event.getBytes());
                    System.out.print("TASK: " + task.getNumberOfRounds());
                    sendMessages(task.getNumberOfRounds());
                    break;
                default:
                    System.out.println("Protocol Unmatched!");
                    System.exit(0);
            }
        } catch (IOException e) {
            System.err.println("Error: MessagingNode.onEvent()");
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendInitiateConnectionRequest(Vertex vertex) throws IOException {
        InitiatePeerConnection peerConnection = new InitiatePeerConnection(this.messagingNodeIP, this.messagingNodePort);
        Socket peerSocket = vertex.getSocket();
        TCPSender tcpSender = new TCPSender(peerSocket);
        tcpSender.sendData(peerConnection.getBytes());
    }

    public void configureServer(Node node){
        this.server = new TCPServerThread(node); 
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    public void sendMessages(int numberOfRounds){

    }

    public void receivedMessage(Message message, Socket socket){

    }


    public static void main(String[] args){
        String registryName = args[0];
        int registryPort = Integer.parseInt(args[1]);
        MessagingNode messagingNode = new MessagingNode(registryName, registryPort);

        // CLIHandler cliHandler = new CLIHandler(messagingNode);
        // cliHandler.readInstructions();
    }
}
