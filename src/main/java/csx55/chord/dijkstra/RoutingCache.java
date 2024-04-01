package csx55.chord.dijkstra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RoutingCache {

    private final HashMap<String, ArrayList<String>> routes;
    private final Random rand = new Random();
    private final String[] names;
    private final String nodeID;
    private final int[][] linkWeights;
    
    public RoutingCache(HashMap<String, ArrayList<String>> routes, String[] names, String nodeID, int[][] linkWeights){
        this.routes = routes;
        this.names = names;
        this.nodeID = nodeID;
        this.linkWeights = linkWeights;
    }

    synchronized public ArrayList<String> getRoute(){
        while(true){
            int randomNode = rand.nextInt(names.length);
            if(!names[randomNode].equals(nodeID)){
                return routes.get(names[randomNode]);
            }
        }
    }

    synchronized public ArrayList<String> get(String name){
        return routes.get(name);
    }
}
