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
        // String[] nodesInGraph = this.names;


        HashMap<String, ArrayList<String>> paths = new HashMap<String, ArrayList<String>>();
        HashMap<String, Integer> nodeDistances = new HashMap<String, Integer>();
        ArrayList<String> unvisited = new ArrayList<>();
        ArrayList<String> visited = new ArrayList<>();
    
        nodeDistances.put(nodeSource, 0);
        unvisited.add(nodeSource);

        // for(int i = 0; i < nodesInGraph.length; i ++){
        //     if(!nodesInGraph[i].equals(nodeSource)){
        //         nodeDistances.put(nodesInGraph[i], Integer.MAX_VALUE);
        //     }

        //     unvisited.add(nodesInGraph[i]);
        // }
        

        while(!unvisited.isEmpty()){
            // NEeds to be start node (source)
            String visiting = nodeWithMinDistance(nodeDistances, unvisited);
            unvisited.remove(visiting);

            for(String neighbor : getNeighbors(visiting)){
                int pathDistance = nodeDistances.get(visiting) + weights[getIndex(visiting)][getIndex(neighbor)];
            
                if(!visited.contains(neighbor)){

                    if(pathDistance < nodeDistances.get(neighbor)){

                        nodeDistances.put(neighbor, pathDistance);
                        ArrayList<String> shortestPath = paths.get(visiting);
                        shortestPath.add(visiting);
                        paths.put(neighbor, shortestPath);
                    
                    }

                    unvisited.add(neighbor);
                }
            }
            visited.add(visiting);
        }

        printRoutes(paths);
        return paths;
    }


    public void printRoutes(HashMap<String, ArrayList<String>> paths){
        for(int i = 0; i < names.length; i++){
            ArrayList<String> route = paths.get(names[i]);
            System.out.println(names[i]);

            for(int j = 0; j < route.size(); j++){
                System.out.print(route.get(j) + " -> ");
            }

            System.out.println();
        }
    }
}

    // while (unsettledNodes.size() != 0) {
    //     Node currentNode = getLowestDistanceNode(unsettledNodes);

    //     unsettledNodes.remove(currentNode);

    //     for (Entry < Node, Integer> adjacencyPair: currentNode.getAdjacentNodes().entrySet()) {
    //         Node adjacentNode = adjacencyPair.getKey();
    //         Integer edgeWeight = adjacencyPair.getValue();

    //         if (!settledNodes.contains(adjacentNode)) {
    //             calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
    //             unsettledNodes.add(adjacentNode);
    //         }
    //     }
    //     settledNodes.add(currentNode);
    // }
    // return graph;

    // private static void CalculateMinimumDistance(Node evaluationNode,Integer edgeWeigh, Node sourceNode) {
    // Integer sourceDistance = sourceNode.getDistance();


    // if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
    //     evaluationNode.setDistance(sourceDistance + edgeWeigh);
    //     LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
    //     shortestPath.add(sourceNode);
    //     evaluationNode.setShortestPath(shortestPath);
    // }
//  }

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

    