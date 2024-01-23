package csx55.overlay.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import csx55.overlay.transport.TCPReceiverThread;
import csx55.overlay.transport.TCPSender;
import csx55.overlay.transport.TCPServerThread;
import csx55.overlay.wireformats.Event;
import csx55.overlay.wireformats.Message;

public class MessagingNode implements Node{
    // Maybe move to StatisticsCollectorAndDisplay (?)
    int sendTracker = 0;  // number of messages sent
    int receiveTracker = 0;  // number of messages that were received
    int relayTracker = 0; // Number of messages that were relayed.
    long sendSummation = 0; // Sum of value that it has sent 
    long receiveSummation = 0;  // Sum of the payloads that it has received 

    @Override
    public void onEvent(Event event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onEvent'");
    }

    public static void configureServer(Node node){
        TCPServerThread server = new TCPServerThread(node); //TODO: remove initalized port
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

        } catch (IOException e) {
            System.err.println("MessagingNode: error in main");
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        }
    }

    public static void main(String[] args){
        String registryName = args[0];
        int registryPort = Integer.parseInt(args[1]);
        sendData(registryName, registryPort);

        
    }
}
