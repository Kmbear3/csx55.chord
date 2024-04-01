package csx55.chord.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import csx55.chord.wireformats.Event;
import csx55.chord.wireformats.TaskSummaryResponse;

public class StatisticsCollectorAndDisplay {
    // Add all summations

    private int numberOfGeneratedTasks;  // number of messages sent
    private int numberOfPulledTasks;  // number of messages that were received
    private int numberOfPushedTasks; // Number of messages that were relayed.
    private int numberOfCompletedTasks; // Sum of value that it has sent
    // private long percentTotalTasksPerformed;  // Sum of the payloads that it has received

    ConcurrentHashMap<String, ArrayList<String>> nodes = new ConcurrentHashMap<>();

    private VertexList registry;


    public StatisticsCollectorAndDisplay(){
        this.numberOfGeneratedTasks = 0;
        this.numberOfPulledTasks = 0;
        this.numberOfPushedTasks = 0;
        this.numberOfCompletedTasks = 0;
    }

    public StatisticsCollectorAndDisplay(VertexList vertexList) {
        this.registry = vertexList;
    }

    synchronized public int getNumberOfGeneratedTasks(){
        return numberOfGeneratedTasks;
    }
    synchronized public int getNumberOfPulledTasks(){
        return numberOfPulledTasks;
    }

    synchronized public int getNumberOfPushedTasks(){
        return numberOfPushedTasks;
    }

    synchronized public int getNumberOfCompletedTasks(){
        return numberOfCompletedTasks;
    }

    synchronized public void displayStats(){
        System.out.println("Generated Tasks: " + numberOfGeneratedTasks);
        System.out.println("Pulled Tasks: " + numberOfPulledTasks);
        System.out.println("Pushed Tasks: " + numberOfPushedTasks);
        System.out.println("Completed Tasks: " + numberOfCompletedTasks);
    }

    synchronized public void incrementGeneratedTasks(int generatedTasks){
        this.numberOfGeneratedTasks = this.numberOfGeneratedTasks + generatedTasks;
    }

    synchronized public void incrementPulledTasks(){
        this.numberOfPulledTasks = this.numberOfPulledTasks + 1;
    }

    synchronized public void incrementPushedTasks(){
        this.numberOfPushedTasks = this.numberOfPushedTasks + 1;
    }

    synchronized public void incrementCompletedTasks(){
        this.numberOfCompletedTasks = this.numberOfCompletedTasks + 1;
    }

    synchronized public void nodeStats(Event event) {
        try {

            TaskSummaryResponse nodeResponse = new TaskSummaryResponse(event.getBytes());
            nodes.put(nodeResponse.getName(), nodeResponse.getStats());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    synchronized public boolean receivedAllStats() {
        // registry.printVertexList();
        
        for(Vertex registeredVertex : registry.getValues()){

            // System.out.println(registeredVertex.getID());
            if(!nodes.containsKey(registeredVertex.getID())){
                return false;
            }
        }
        return true; 
    }

    synchronized public void displayTotalSums() {

        int totalNumberOfGeneratedTasks = 0;
        int totalNumberOfPulledTasks = 0;
        int totalNumberOfPushedTasks = 0;
        int totalNumberOfCompletedTasks = 0;
        double totalPercentageOfCompletedTasks = 0;

        for(ArrayList<String> nodeStats : nodes.values()){
            totalNumberOfGeneratedTasks =  totalNumberOfGeneratedTasks + Integer.parseInt(nodeStats.get(0));
            totalNumberOfPulledTasks = totalNumberOfPulledTasks + Integer.parseInt(nodeStats.get(1));
            totalNumberOfPushedTasks = totalNumberOfPushedTasks + Integer.parseInt(nodeStats.get(2));
            totalNumberOfCompletedTasks = totalNumberOfCompletedTasks + Integer.parseInt(nodeStats.get(3));
        }


        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("                                                                    Registry Traffic Summary                                                       ");
        System.out.println("                                                                                                                                      ");

        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(String.format("| %-20s | %20s | %22s | %22s | %22s | %22s |", "Node" ,"Generated Tasks","Pulled Tasks", "Pushed Tasks", "Completed Tasks",  "Task Completion %"));
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------");
        
        int i = 0;
        for(ArrayList<String> nodeStats : nodes.values()){
            i ++;
            String nodeName =  nodeStats.get(4);
            double percentageOfCompleteTask = Double.parseDouble(nodeStats.get(3)) / totalNumberOfCompletedTasks * 100;
            totalPercentageOfCompletedTasks = totalPercentageOfCompletedTasks + percentageOfCompleteTask;

            System.out.println(String.format("| %-20s | %20s | %22s | %22s | %22s | %22f |", nodeName, nodeStats.get(0), nodeStats.get(1), nodeStats.get(2), nodeStats.get(3), percentageOfCompleteTask));

        }

        System.out.println(String.format("| %-20s | %20d | %22d | %22d | %22d | %22f |", "Totals: ", totalNumberOfGeneratedTasks, totalNumberOfPulledTasks, totalNumberOfPushedTasks, totalNumberOfCompletedTasks, totalPercentageOfCompletedTasks));

        System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    synchronized public void resetCounters(){
        this.numberOfGeneratedTasks = 0;
        this.numberOfPulledTasks = 0;
        this.numberOfPushedTasks = 0;
        this.numberOfCompletedTasks = 0;
    }
    
}
