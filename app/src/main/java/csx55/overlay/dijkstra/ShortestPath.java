package csx55.overlay.dijkstra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class ShortestPath {
    private final int[][] weights;

    public ShortestPath(int[][] linkWeights){
        this.weights = linkWeights;
    }


    public String nodeWithMinDistance(Map<String, Integer> nodeDistances, ArrayList<String> unvisited ){
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


        return neighbors;
    }

    public int getIndex(String node){
        int indexOfNode = -1;

        // Given names, what index maps the name correctly into linkWeight??? 

        return indexOfNode;
    }

    public HashMap<String, Integer> calculateShortestPaths(int[][] linkWeights, String nodeSource, ArrayList<String> nodesInGraph){
        
        HashMap<String, Integer> nodeDistances = new HashMap<String, Integer>();
        ArrayList<String> unvisited = new ArrayList<>();

        nodeDistances.put(nodeSource, 0);

        for(int i = 0; i < nodesInGraph.size(); i ++){
            if(!nodesInGraph.get(i).equals(nodeSource)){
                nodeDistances.put(nodesInGraph.get(i), Integer.MAX_VALUE);
            }

            unvisited.add(nodesInGraph.get(i));
        }

        while(!unvisited.isEmpty()){
            // NEeds to be start node (source)
            String visiting = nodeWithMinDistance(nodeDistances, unvisited);
            unvisited.remove(visiting);

            for(String neighbor : getNeighbors(visiting)){
                int pathDistance = nodeDistances.get(visiting) + linkWeights[getIndex(visiting)][getIndex(neighbor)];
                
                if(pathDistance < nodeDistances.get(neighbor)){
                    nodeDistances.put(neighbor, pathDistance);
                }
            }
        }

        return nodeDistances;
    }



//     function Dijkstra(Graph, source):
//        dist[source]  := 0                     // Distance from source to source is set to 0
//        for each vertex v in Graph:            // Initializations
//            if v ≠ source
//                dist[v]  := infinity           // Unknown distance function from source to each node set to infinity
//            add v to Q                         // All nodes initially in Q

//       while Q is not empty:                  // The main loop
//           v := vertex in Q with min dist[v]  // In the first run-through, this vertex is the source node
//           remove v from Q 

//           for each neighbor u of v:           // where neighbor u has not yet been removed from Q.
//               alt := dist[v] + length(v, u)
//               if alt < dist[u]:               // A shorter path to u has been found
//                   dist[u]  := alt            // Update distance of u 

//       return dist[]
//   end function

    
}
