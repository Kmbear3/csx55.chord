package csx55.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import csx55.overlay.node.Node;

public class TCPServerThread implements Runnable{
    ServerSocket serverSocket;
    Node node;

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
