package csx55.overlay.node;

import java.io.IOException;
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
import csx55.overlay.wireformats.Event;
import csx55.overlay.wireformats.InitiatePeerConnection;
import csx55.overlay.wireformats.Message;
import csx55.overlay.wireformats.MessagingNodesList;
import csx55.overlay.wireformats.Protocol;
import csx55.overlay.wireformats.RegisterationResponse;
import csx55.overlay.wireformats.RegistrationRequest;

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

            this.server = new TCPServerThread(this);

            this.messagingNodeIP = server.getIP();
            this.messagingNodePort = server.getPort();

            System.out.print("Inside MessagingNode(IP, port) --- IP: " + this.messagingNodeIP + " --- Port: " + this.messagingNodePort);

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
            System.out.println("Inside Registry.onEvent() --- Type: " + event.getType());
            switch(event.getType()){
                case Protocol.MESSAGE:
                    System.out.println("MESSAGE");
                    break;
                case Protocol.REGISTER_RESPONSE:
                    System.out.println("Received registration Response");
                    RegisterationResponse regRes = new RegisterationResponse(event.getBytes());
                    regRes.getInfo();
                    break;

                case Protocol.INITIATE_PEER_CONNECTION:
                    System.out.println("Received a Peerconnection Request");
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
                        }
                    }

                    System.out.println("All connections are established. Number of connections: " + peerList.size());
                    break;
                default:
                    System.out.println("Protocol Unmatched!");
                    System.exit(0);
            }
        } catch (IOException e) {
            System.err.println("Error: MessagingNode.onEvent()");
            e.printStackTrace();
        }
    }

    public static void configureServer(Node node){
        TCPServerThread server = new TCPServerThread(node); 
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    // public static void sendData(String server, int port){
    //     try { 
    //         System.out.println("Sending Data");
    //         Socket socket = new Socket(server, port);

    //         // Thread.sleep(5000);
    //         for(int i  = 0; i < 5; i++){
    //             Message message = new Message();
    //             TCPSender tcps = new TCPSender(socket);
    //             tcps.sendData(message.getMessage());
    //         }

    //         System.out.println("finished printing messages: sleeping for 5s");
    //         Thread.sleep(5000);
            
    //     } catch (IOException e) {
    //         System.err.println("MessagingNode: error in main");

    //     } catch (InterruptedException e) {
    //         e.printStackTrace();

    //     }
    // }


    public static void main(String[] args){
        String registryName = args[0];
        int registryPort = Integer.parseInt(args[1]);
        MessagingNode messagingNode = new MessagingNode(registryName, registryPort);

        // CLIHandler cliHandler = new CLIHandler(messagingNode);
        // cliHandler.readInstructions();
    }
}
