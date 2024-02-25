package csx55.threads.balancing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.threads.hashing.Task;
import csx55.threads.node.ComputeNode;
import csx55.threads.wireformats.NodeTasks;
import csx55.threads.wireformats.Tasks;

public class BalanceLoad {
    int numberOfNodesInOverlay;
    ComputeNode computeNode;
    ConcurrentLinkedQueue<Task> tasks;
    int totalTasksInOverlay = 0;
    int roundNumber; 
    int receivedNodeMessage;
    int tasksCreatedForRound;
    int average;
    int accumulatedTasksForRound = 0;

    public BalanceLoad(int numberOfNodesInOverlay, ComputeNode computeNode, ConcurrentLinkedQueue<Task> tasks){
        this.numberOfNodesInOverlay = numberOfNodesInOverlay;
        this.computeNode = computeNode;
        this.tasks = tasks;
    }
    
    synchronized public void addToSum(NodeTasks nodeTasks){
        // System.out.println("NodeTask received: " + receivedNodeMessage + "Round: " + nodeTasks.getRoundNumber());
        try {
            receivedNodeMessage = receivedNodeMessage + 1;
            totalTasksInOverlay += nodeTasks.getNumberOfTasks();

            if(!nodeTasks.getId().equals(computeNode.getID())){
                computeNode.sendClockwise(nodeTasks.getBytes());
            }
    
            if(receivedNodeMessage == numberOfNodesInOverlay){
                // New round needs to be calculated, thus receivedNodeMessage needs to be reset
                receivedNodeMessage = 0;
                calculateAverage();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public void setNewRound(int tasksCreated){
        this.tasksCreatedForRound = tasksCreated;
        this.accumulatedTasksForRound = 0;
    }

    synchronized public void calculateAverage(){
        this.average = totalTasksInOverlay / numberOfNodesInOverlay;
        System.out.println("Average messages: " + this.average);
        System.out.println("TotalTasks in Overlay messages: " + this.totalTasksInOverlay);
        this.totalTasksInOverlay = 0;


        //Using numberOftasks here might cause issues.... This is the number of tasks that the node created, not the number that it currently has. 

        // System.out.println("number of messages: " + this.tasks.size());
        // System.out.println("number of created tasks: " + this.tasksCreatedForRound);

        if((this.tasksCreatedForRound + accumulatedTasksForRound) > average){
            int difference = (this.tasksCreatedForRound + accumulatedTasksForRound) - average;
            this.accumulatedTasksForRound = accumulatedTasksForRound - difference;
            sendTasksClockwise(difference);
        }
    }

    synchronized private void sendTasksClockwise(int difference) {
        ArrayList<Task> taskList = new ArrayList<>();
        
        for(int i = 0; i < difference; i++){
            Task task = tasks.poll();

            if(task != null){
                taskList.add(task);
            }
        }

        // System.out.println("Number of tasks sending clockwise: " + taskList.size());
        // System.out.println("Tasks For this round:" + (this.tasksCreatedForRound + this.accumulatedTasksForRound));

        Tasks taskMessage = new Tasks(taskList);

        try {
            computeNode.sendClockwise(taskMessage.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    synchronized public void receiveTasks(ArrayList<Task> taskList) throws IOException{
        // ArrayList<Task> taskList = receivedTasks.getTaskList();
        ArrayList<Task> relayTasks = new ArrayList<>();

        // System.out.println("Received: " + taskList.size());

        
        for(int i = 0; i < taskList.size(); i++){
            Task task = taskList.get(i);

            if((this.tasksCreatedForRound + this.accumulatedTasksForRound) < average){
                this.accumulatedTasksForRound ++;
                this.tasks.add(task);  // TAsk list may have fewer tasks than necessary. 
            }
            else if(this.computeNode.originated(task)){
                this.accumulatedTasksForRound ++;
                System.out.println("Suppressing messages");
                tasks.add(task);
            }else{
                relayTasks.add(task);
            }
        }

        if(relayTasks.size() != 0){
            System.out.println("Relaying: " + relayTasks.size());
            Tasks relayTasksMessage = new Tasks(relayTasks);
            computeNode.sendClockwise(relayTasksMessage.getBytes());
        }

        System.out.println("Tasks for round after shifting: " + (this.tasksCreatedForRound + this.accumulatedTasksForRound));
        System.out.println("Total tasks for round after shifting: " + this.tasks.size());

        // System.out.println("Percentage: " + (this.tasks.size() / (double)this.totalTasksInOverlay)  * 100);

    }
}
