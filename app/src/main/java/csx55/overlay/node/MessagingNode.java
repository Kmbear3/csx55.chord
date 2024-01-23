package csx55.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import csx55.overlay.transport.TCPReceiverThread;
import csx55.overlay.transport.TCPSender;
import csx55.overlay.transport.TCPServerThread;
import csx55.overlay.wireformats.Event;
import csx55.overlay.wireformats.Message;
import csx55.overlay.wireformats.RegistrationRequest;

public class MessagingNode implements Node{
    // Maybe move to StatisticsCollectorAndDisplay (?)
    int sendTracker = 0;  // number of messages sent
    int receiveTracker = 0;  // number of messages that were received
    int relayTracker = 0; // Number of messages that were relayed.
    long sendSummation = 0; // Sum of value that it has sent 
    long receiveSummation = 0;  // Sum of the payloads that it has received 

    Socket registrySocket;
    String messagingNodeIP;
    int messagingNodePort; 

    TCPServerThread server;

    public MessagingNode(String registryIP, int registryPort){
        try {
            this.registrySocket = new Socket(registryIP, registryPort);
            server = new TCPServerThread(this);

            this.messagingNodeIP = server.getIP();
            this.messagingNodePort = server.getPort();
        
            RegistrationRequest regReq = new RegistrationRequest(messagingNodeIP, messagingNodePort);

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(Event event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onEvent'");
    }

    public static void configureServer(Node node){
        TCPServerThread server = new TCPServerThread(node); 
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    public static void sendData(String server, int port){
        try { 
            System.out.println("Sending Data");
            Socket socket = new Socket(server, port);

            // Thread.sleep(5000);
            for(int i  = 0; i < 5; i++){
                Message message = new Message();
                TCPSender tcps = new TCPSender(socket);
                tcps.sendData(message.getMessage());
            }

            System.out.println("finished printing messages: sleeping for 5s");
            Thread.sleep(5000);
            
        } catch (IOException e) {
            System.err.println("MessagingNode: error in main");

        } catch (InterruptedException e) {
            e.printStackTrace();

        }
    }


    public static void main(String[] args){
        String registryName = args[0];
        int registryPort = Integer.parseInt(args[1]);
        
        // sendData(registryName, registryPort);

    }
}
