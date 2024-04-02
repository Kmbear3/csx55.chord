package csx55.chord.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import csx55.chord.Discovery;
import csx55.chord.node.Node;
import csx55.chord.wireformats.Event;
import csx55.chord.wireformats.EventFactory;
import csx55.chord.wireformats.RegistrationRequest;

public class TCPReceiverThread implements Runnable {

    private Socket socket;
    private DataInputStream din;
    private Node node;
    private String socketName;
    
    public TCPReceiverThread(Node node, Socket socket) throws IOException {
        this.socket = socket;
        din = new DataInputStream(socket.getInputStream());
        this.node = node;
        this.socketName = socket.toString();
    }

    public void run() {
        int dataLength;

        while (socket != null) {
            try {
                
                dataLength = din.readInt();

                byte[] data = new byte[dataLength];
                din.readFully(data, 0, dataLength);

                Event event = EventFactory.getEvent(data);
                this.node.onEvent(event, socket);
                

            } catch (SocketException se) {
                System.out.println(se.getMessage());
                break;
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage()) ;
                break;
            }
        }
        System.err.println("Socket Closed " + socketName);

        if(this.node instanceof Discovery){
            ((Discovery)this.node).closedConnection(socketName);
        }
        
    }
    
}
