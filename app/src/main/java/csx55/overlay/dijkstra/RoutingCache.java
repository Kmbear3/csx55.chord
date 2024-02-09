package csx55.overlay.dijkstra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RoutingCache {

    private final HashMap<String, ArrayList<String>> routes;
    private final Random rand = new Random();
    private final String[] names;
    private final String nodeID;
    
    public RoutingCache(HashMap<String, ArrayList<String>> routes, String[] names, String nodeID){
        this.routes = routes;
        this.names = names;
        this.nodeID = nodeID;
    }

    public ArrayList<String> getRoute(){
        int randomNode = rand.nextInt(names.length);
        return routes.get(names[randomNode]);
    }
}
