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

public class MessageSender implements Runnable {
    private ConcurrentLinkedQueue<Message> messages;
    private int numberOfRounds;
    private MessagingNode node;
    private int[][] linkWeights;
    private String[] names;
    private StatisticsCollectorAndDisplay stats;
    private RoutingCache routingCache;
    private ShortestPath paths;
    
    

    public MessageSender(MessagingNode node, ConcurrentLinkedQueue<Message> messages, int[][] linkWeights, String[] names, StatisticsCollectorAndDisplay stats){
        this.messages = messages;
        this.node = node;
        this.linkWeights = linkWeights;
        this.names = names;
        this.stats = stats;
        this.paths =  new ShortestPath(node.getID(), linkWeights, names);
        this.routingCache = new RoutingCache(paths.calculateShortestPaths(), names, node.getID(), linkWeights);
    }

    public MessageSender(MessagingNode node){
        this.node = node;
    }

       
    synchronized public void setNumberOfRound(int numberOfRounds){
        this.numberOfRounds = numberOfRounds;
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

                vertex.sendMessage(message.getBytes());

                stats.incrementSendTracker();
                stats.addSendSum(message.getPayload());
            }
        }

        TaskComplete task = new TaskComplete(node.getMessagingNodeIP(), node.getMessagingNodePort());
        node.sendRegistryMessage(task);
    }

    public void relayOrReceiveMessages(ConcurrentLinkedQueue<Message> messages, StatisticsCollectorAndDisplay stats) throws IOException{

        while (true) {
            Message message = messages.poll();
            if (message != null) {
                ArrayList<String> routePlan = message.getRoutePlan();

                if(routePlan.get(routePlan.size() - 1).equals(node.getID())){ // Last node in route --> destination node 
                    stats.incrementReceivedTracker();
                    stats.addReceiveSum(message.getPayload());
                }
                else{

                    int nextNode = routePlan.indexOf(node.getID()) + 1;

                    VertexList peerList = node.getPeerList();
                    Vertex vertex = peerList.get(routePlan.get(nextNode)); // Next step in the route. 

                    vertex.sendMessage(message.getBytes());
                    stats.incrementRelayed();
                }
            }
        }
    }

    @Override
    public void run() {
        // ShortestPath paths = new ShortestPath(node.getID(), linkWeights, names);
        // this.routingCache = new RoutingCache(paths.calculateShortestPaths(), names, node.getID(), linkWeights);
        
        try {

            sendMessages(numberOfRounds, this.stats, routingCache);
            relayOrReceiveMessages(messages, this.stats);          

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
    }

    synchronized public void printShortestPaths(){
        for(int i = 0; i < names.length; i++){
            ArrayList<String> route = this.routingCache.get(names[i]);

            if(route == null){
                continue;
            }

            String shortestPath = "";
            for(int j = 0; j < route.size() - 1; j++){
                int source = getIndex(route.get(j));
                int nextStop = getIndex(route.get(j + 1));
                if(j == 0){
                    shortestPath += route.get(j) + "--" + linkWeights[source][nextStop] + "--" + route.get(j + 1);
                }
                else{
                    shortestPath += "--" + linkWeights[source][nextStop] + "--" + route.get(j + 1);
                }  
            }
            System.out.println(shortestPath);
        }
    }

    public int getIndex(String node){
        // Given names, what index maps the name correctly into linkWeight??? 

        int error = -1;

        for(int i = 0; i < this.names.length; i++){
            if(node.equals(this.names[i])){
                return i;
            }
        }
        return error;
    }
}
