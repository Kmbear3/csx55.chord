package csx55.overlay.node;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import csx55.overlay.transport.TCPServerThread;
import csx55.overlay.wireformats.Event;

public class Registry implements Node {
    ConcurrentHashMap registeredNodes = new ConcurrentHashMap<>();


    @Override
    public void onEvent(Event event) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onEvent'");
    }

    public static void main(String[] args){
        TCPServerThread tcpServer = new TCPServerThread(7000);
        Thread serverThread = new Thread(tcpServer);
        serverThread.start();
        
        


    }
}
