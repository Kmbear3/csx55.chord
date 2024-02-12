package csx55.overlay.dijkstra;

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

    synchronized public void printRoutes(){
        for(int i = 0; i < names.length; i++){
            ArrayList<String> route = routes.get(names[i]);

            String shortestPath = "";
            for(int j = 0; j < route.size() - 1; j++){
                int source = getIndex(route.get(j));
                int nextStop = getIndex(route.get(j + 1));
                shortestPath += route.get(j) + "--" + linkWeights[source][nextStop] + "--" + route.get(j + 1);
            }
        }
    }
}
