package csx55.chord.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import csx55.chord.node.Node;

public class TCPServerThread implements Runnable{
    ServerSocket serverSocket;
    Node node;

    String IP;
    int port;

    public TCPServerThread(Node node){
        try{
            
            this.node = node;
            serverSocket = new ServerSocket(0);

        }catch(IOException ioe){
            System.err.println("TCPServerThread: Error in default constructor");
        }
    }

    public TCPServerThread(Node node, int port){
        try{
            this.node = node;
            serverSocket = new ServerSocket(port);
        }catch(IOException ioe){
            System.err.println("TCPServerThread: Error in constructor");
        }
    }

    public int getPort(){
        return serverSocket.getLocalPort();
    }

    public String getIP(){
        try {

            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "Error inside TCPServerThread.getIP()";
    }

    @Override
    public void run() {
        try{
            while(true){
                Socket socket = serverSocket.accept();
                TCPReceiverThread tcpr = new TCPReceiverThread(this.node, socket);
                Thread tcprThread = new Thread(tcpr);
                tcprThread.start();
            }   

        }catch(IOException IOe){
            System.err.println("TCPServerThread: Error in run");
        }
    }


    
    
}
