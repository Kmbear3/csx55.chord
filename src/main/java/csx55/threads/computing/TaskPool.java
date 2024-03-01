package csx55.threads.computing;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.threads.ComputeNode;
import csx55.threads.hashing.Task;

public class TaskPool {
    ConcurrentLinkedQueue<Task> tasks;
    int numberOfThreads;
    ComputeNode node;

    public TaskPool(ComputeNode node, ConcurrentLinkedQueue<Task> tasks, int numberOfThreads) {
        this.tasks = tasks;
        this.numberOfThreads = numberOfThreads;
        this.node = node;
    }

    public void createThreads(){

        for(int i = 0; i < numberOfThreads; i ++){
            TaskThread task = new TaskThread(tasks, node.getStats());
            Thread taskThread = new Thread(task);
            taskThread.start();
        }
    }
}
