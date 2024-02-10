package csx55.overlay.util;

import csx55.overlay.wireformats.Message;
import csx55.overlay.wireformats.Poke;
import csx55.overlay.wireformats.TaskComplete;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.overlay.dijkstra.RoutingCache;
import csx55.overlay.dijkstra.ShortestPath;
import csx55.overlay.node.MessagingNode;
import csx55.overlay.transport.TCPSender;

public class MessageSender implements Runnable {
    private ConcurrentLinkedQueue<Message> messages;
    private int numberOfRounds;
    private MessagingNode node;
    private int[][] linkWeights;
    private String[] names;
    private StatisticsCollectorAndDisplay stats;

    public MessageSender(MessagingNode node, ConcurrentLinkedQueue<Message> messages, int numberOfRounds, int[][] linkWeights, String[] names, StatisticsCollectorAndDisplay stats){
        this.messages = messages;
        this.node = node;
        this.numberOfRounds = numberOfRounds;
        this.linkWeights = linkWeights;
        this.names = names;
        this.stats = stats;
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


    public void sendMessages(int numberOfRounds, StatisticsCollectorAndDisplay stats, RoutingCache routes) throws IOException{

        for(int i = 0; i < this.numberOfRounds; i++){
            final ArrayList<String> route = new ArrayList<String>(routes.getRoute());

            Message message = new Message(route);

            VertexList peerList = node.getPeerList();
            Vertex vertex = peerList.get(route.get(1)); // Next step in the route. 

            for(int j = 0; j < 5; j++ ){

                TCPSender send = new TCPSender(vertex.getSocket());
                send.sendData(message.getBytes());

                stats.incrementSendTracker();
                stats.addSendSum(message.getPayload());
            }
        }

        TaskComplete task = new TaskComplete(node.getMessagingNodeIP(), node.getMessagingNodePort());
        node.sendRegistryMessage(task);
    }

    public void relayOrReceiveMessages(ConcurrentLinkedQueue<Message> messages, StatisticsCollectorAndDisplay stats) throws IOException{
        for(Message message : messages){

            ArrayList<String> routePlan = message.getRoutePlan();

            if(routePlan.get(routePlan.size() - 1).equals(node.getID())){ // Last node in route --> destination node 
                stats.incrementReceivedTracker();
                stats.addReceiveSum(message.getPayload());
            }
            else{

                int nextNode = routePlan.indexOf(node.getID()) + 1;

                VertexList peerList = node.getPeerList();
                Vertex vertex = peerList.get(routePlan.get(nextNode)); // Next step in the route. 

                TCPSender send = new TCPSender(vertex.getSocket());
                send.sendData(message.getBytes());
                stats.incrementRelayed();
            }
        }

    }

    @Override
    public void run() {
        ShortestPath paths = new ShortestPath(node.getID(), linkWeights, names);
        RoutingCache routingCache = new RoutingCache(paths.calculateShortestPaths(), names, node.getID());
        
        try {

            sendMessages(numberOfRounds, this.stats, routingCache);

            relayOrReceiveMessages(messages, this.stats);

            
            // stats.displayStats();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
    }

}
