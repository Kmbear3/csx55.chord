package csx55.threads.balancing;

import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.threads.hashing.Task;
import csx55.threads.node.ComputeNode;
import csx55.threads.wireformats.NodeTasks;

public class BalanceLoad {
    int numberOfNodesInOverlay;
    ComputeNode computeNode;
    ConcurrentLinkedQueue<Task> tasks;
    int totalTasksInOverlay = 0;

    public BalanceLoad(int numberOfNodesInOverlay, ComputeNode computeNode, ConcurrentLinkedQueue<Task> tasks){
        this.numberOfNodesInOverlay = numberOfNodesInOverlay;
        this.computeNode = computeNode;
        this.tasks = tasks;
    }
    
    synchronized public void calculateAverage(NodeTasks nodeTasks){
        
    }

}
