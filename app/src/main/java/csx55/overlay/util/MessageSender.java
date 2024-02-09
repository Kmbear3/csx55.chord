package csx55.overlay.util;

import csx55.overlay.wireformats.Message;
import csx55.overlay.wireformats.Poke;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.overlay.dijkstra.ShortestPath;
import csx55.overlay.node.MessagingNode;
import csx55.overlay.transport.TCPSender;

public class MessageSender implements Runnable {
    private ConcurrentLinkedQueue<Message> messages;
    private int numberOfRounds;
    private MessagingNode node;
    private int[][] linkWeights;
    private String[] names;

    public MessageSender(MessagingNode node, ConcurrentLinkedQueue<Message> messages, int numberOfRounds, int[][] linkWeights, String[] names){
        this.messages = messages;
        this.node = node;
        this.numberOfRounds = numberOfRounds;
        this.linkWeights = linkWeights;
        this.names = names;
    }

    public MessageSender(MessagingNode node){
        this.node = node;
    }

    synchronized public void sendPoke(){
        Poke poke = new Poke(node.getMessagingNodeIP(), node.getMessagingNodePort());
        VertexList peerList = node.getPeerList();
        System.out.println("Name: " + node.getID());
        peerList.printVertexList();
        peerList.sendAllNodes(poke);
    }

    @Override
    public void run() {
        StatisticsCollectorAndDisplay stats = new StatisticsCollectorAndDisplay();
        ShortestPath paths = new ShortestPath(node.getID(), linkWeights, names);
        
        try {
            for(int i = 0; i < this.numberOfRounds; i++){
                ArrayList<String> routePlan = new ArrayList<>();
                routePlan.add(node.getID());
                String sink = node.getRandomPeerID();
                routePlan.add(sink);
                Message message = new Message(routePlan);

                VertexList peerList = node.getPeerList();
                Vertex vertex = peerList.get(sink);

                TCPSender send = new TCPSender(vertex.getSocket());
                send.sendData(message.getBytes());
                stats.incrementSendTracker();
            }

            // Thread.sleep(5000);

            for(Message message : messages){
                System.out.println("Payload: " + message.getPayload());
                stats.incrementReceivedTracker();
            }

            stats.displayStats();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        // } catch (InterruptedException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        }
    }

}
