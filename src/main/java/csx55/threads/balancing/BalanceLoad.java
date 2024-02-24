package csx55.threads.balancing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
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
    int numberOfTasks;
    int average;
    String nodes = "";

    public BalanceLoad(int numberOfNodesInOverlay, ComputeNode computeNode, ConcurrentLinkedQueue<Task> tasks){
        this.numberOfNodesInOverlay = numberOfNodesInOverlay;
        this.computeNode = computeNode;
        this.tasks = tasks;
    }
    
    synchronized public void addToSum(NodeTasks nodeTasks){
        try {
            receivedNodeMessage = receivedNodeMessage + 1;
            totalTasksInOverlay += nodeTasks.getNumberOfTasks();
            nodes += nodeTasks.getId();

            if(!nodeTasks.getId().equals(computeNode.getID())){
                computeNode.sendClockwise(nodeTasks.getBytes());
            }
    
            if(receivedNodeMessage == numberOfNodesInOverlay){
                // New round needs to be calculated, thus rreceivedNodeMessage needs to be reset
                receivedNodeMessage = 0;
                calculateAverage();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public void setNewRound(int roundNumber, int numberOfTasks) {
        this.roundNumber = roundNumber;
        this.numberOfTasks = numberOfTasks;
    }

    synchronized public void calculateAverage(){
        this.average = totalTasksInOverlay / numberOfNodesInOverlay;
        System.out.println("Average messages: " + this.average);
        System.out.println("TotalTasks in Overlay messages: " + this.totalTasksInOverlay);

        //Using numberOftasks here might cause issues.... This is the number of tasks that the node created, not the number that it currently has. 

        System.out.println("number of messages: " + this.numberOfTasks);
        if(numberOfTasks > average){
            int difference = numberOfTasks - average;
            sendTasksClockwise(difference);
        }
    }

    synchronized private void sendTasksClockwise(int difference) {
        ArrayList<Task> taskList = new ArrayList<>();
        
        for(int i = 0; i < difference; i++){
            taskList.add(tasks.poll());
        }
        System.out.println("Number of tasks sending clockwise: " + taskList.size());

        Tasks taskMessage = new Tasks(taskList);

        try {
            computeNode.sendClockwise(taskMessage.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public synchronized void receiveTasks(ArrayList<Task> taskList) throws IOException{
        // ArrayList<Task> taskList = receivedTasks.getTaskList();
        ArrayList<Task> relayTasks = new ArrayList<>();
        for(Task task : taskList){  
            if(this.tasks.size() < average){
                this.tasks.add(task);  // TAsk list may have fewer tasks than necessary. 
                taskList.remove(task);
            }
            else if(this.computeNode.originated(task)){
                tasks.add(task);
            }else{
                relayTasks.add(task);
            }
        }
        if(relayTasks.size() != 0){
            Tasks relayTasksMessage = new Tasks(relayTasks);
            computeNode.sendClockwise(relayTasksMessage.getBytes());
        }
    }
}

// if(this.tasks.size() < average){
//     System.out.println("Taking tasks- size: " + tasks.size());
//     int difference = average - this.tasks.size();

//     for(int i = 0; i < difference; i++){

//         this.tasks.add(taskList.get(i));  // TAsk list may have fewer tasks than necessary. 
//         taskList.remove(i);
//     }

//     System.out.println("After tasks- size: " + tasks.size());

// }

// ArrayList<Task> relayTasks = new ArrayList<>();

// for(Task task : taskList){

//     if(this.computeNode.originated(task)){
//         tasks.add(task);
//     }else{
//         relayTasks.add(task);
//     }
// }

// if(relayTasks.size() != 0){
//     Tasks relayTasksMessage = new Tasks(relayTasks);
//     computeNode.sendClockwise(relayTasksMessage.getBytes());
// }
// System.out.println("Compute Node tasks: " + this.tasks.size());
// } 

