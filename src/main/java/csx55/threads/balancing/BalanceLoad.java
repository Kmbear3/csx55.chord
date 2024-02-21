package csx55.threads.balancing;

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
            sendTasksClockwise();
        }
    }

    synchronized private void sendTasksClockwise() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendTasksClockwise'");
    }

    synchronized public void receiveTasks(Tasks tasks){
        if(numberOfTasks < average){
            takeTasks();
        }
        takeBackTasks();
    }

    private void takeBackTasks() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'takeBackTasks'");
    }

    private void takeTasks() {
        // Take as many tasks as needed. 
        // If my tasks have made it back to me, take them.
        takeBackTasks();
    }
}
