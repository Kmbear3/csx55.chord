package csx55.overlay.dijkstra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.swing.RowFilter.Entry;

public class ShortestPath {
    private final int[][] weights;
    private final String[] names;
    private final String source; 

    public ShortestPath(String source, int[][] linkWeights, String[] names){
        this.weights = linkWeights;
        this.names = names;
        this.source = source;
    }

    public String nodeWithMinDistance(Map<String, Integer> nodeDistances, Set<String> unvisited){
        String minDistanceNode = ""; 

        int minDistance = Integer.MAX_VALUE;

        for(String node : unvisited){
            if(nodeDistances.get(node) < minDistance){
                minDistance = nodeDistances.get(node); 
                minDistanceNode = node;
            }
        }

        return minDistanceNode;
    }

    public ArrayList<String> getNeighbors(String visiting){
        ArrayList<String> neighbors = new ArrayList<>();
        // Return all of the CR connections that a node has
        int indexOfnode = getIndex(visiting);

        for(int i = 0; i < weights.length; i++){
           if(weights[indexOfnode][i] != 0){
                neighbors.add(names[i]);
           }
        }

        return neighbors;
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

    public HashMap<String, ArrayList<String>> calculateShortestPaths(){
        String nodeSource = this.source;
        String[] nodesInGraph = this.names;

        HashMap<String, ArrayList<String>> paths = new HashMap<String, ArrayList<String>>();
        HashMap<String, Integer> nodeDistances = new HashMap<String, Integer>();
        // ArrayList<String> unvisited = new ArrayList<>();
        // ArrayList<String> visited = new ArrayList<>();
      
        Set<String> unvisited = new HashSet<>();
        Set<String> visited = new HashSet<>();

    
        nodeDistances.put(nodeSource, 0);
        unvisited.add(nodeSource);

        for(int i = 0; i < nodesInGraph.length; i ++){
            if(!nodesInGraph[i].equals(nodeSource)){
                nodeDistances.put(nodesInGraph[i], Integer.MAX_VALUE);
            }
            paths.put(names[i],  new ArrayList<>());
        }
        

        while(!unvisited.isEmpty()){
            // NEeds to be start node (source)
            String visiting = nodeWithMinDistance(nodeDistances, unvisited);

            unvisited.remove(visiting);


            for(String neighbor : getNeighbors(visiting)){

                int pathDistance = nodeDistances.get(visiting) + weights[getIndex(visiting)][getIndex(neighbor)];
            
                if(!visited.contains(neighbor)){

                    int sourceDistance = nodeDistances.get(visiting);
                    if(sourceDistance + pathDistance < nodeDistances.get(neighbor)){
                        nodeDistances.put(neighbor, sourceDistance + pathDistance);
                        ArrayList<String> shortestPath = new ArrayList<String>(paths.get(visiting));
                        shortestPath.add(visiting);
                        paths.put(neighbor, shortestPath);
                    }

                    unvisited.add(neighbor);
                }
            }
            visited.add(visiting);

        }

        addDestinations(paths);
        // printRoutes(paths);

        // HIGHLY SUS 
        paths.remove(source);
        // AJHHH HIHGLY SUS!!! 

        
        return paths;
    }

    public void addDestinations(HashMap<String, ArrayList<String>> paths){
        for(String name: this.names){
           ArrayList<String> path = new ArrayList<>(paths.get(name));
           path.add(name);
           paths.put(name, path);
        }
    }

    public void printRoutes(HashMap<String, ArrayList<String>> paths){
        for(int i = 0; i < names.length; i++){
            ArrayList<String> route = paths.get(names[i]);
            System.out.print("Sink " + names[i]);

            for(int j = 0; j < route.size(); j++){
                System.out.print("   " + route.get(j) + " -> ");
            }

            System.out.println();
        }
    }

    public void printSet(String setName, Set<String> nodeSet){
        System.out.print("Set: " + setName + " ");
        for(String node : nodeSet){
            System.out.print(" " + node);
        }
        System.out.println();
    }

    public void printDistances(HashMap<String, Integer> distances){
        for(String name : this.names){
            System.out.println(name + " : " + " Distances: " + distances.get(name));
        }
    }
}
