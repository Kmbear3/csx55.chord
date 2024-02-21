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
    int numberOfTasks;
    int average;

    public BalanceLoad(int numberOfNodesInOverlay, ComputeNode computeNode, ConcurrentLinkedQueue<Task> tasks){
        this.numberOfNodesInOverlay = numberOfNodesInOverlay;
        this.computeNode = computeNode;
        this.tasks = tasks;
    }
    
    synchronized public void addToSum(NodeTasks nodeTasks){
        totalTasksInOverlay += nodeTasks.getNumberOfTasks();
        receivedNodeMessage ++;

        if(receivedNodeMessage == numberOfNodesInOverlay){
            calculateAverage();
        }
    }

    synchronized public void setNewRound(int roundNumber, int numberOfTasks) {
        this.roundNumber = roundNumber;
        this.receivedNodeMessage = 0;
        this.numberOfTasks = numberOfTasks;
    }

    synchronized public void calculateAverage(){
        this.average = totalTasksInOverlay / numberOfNodesInOverlay;

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

        Tasks taskMessage = new Tasks(taskList);

        try {
            computeNode.sendClockwise(taskMessage.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    synchronized public void receiveTasks(Tasks receivedTasks){
        ArrayList<Task> taskList = receivedTasks.getTaskList();

        if(numberOfTasks < average){
            int difference = numberOfTasks - average;

            for(int i = 0; i < difference; i++){
                tasks.add(taskList.get(i));
            }
        }
        
    }

}
