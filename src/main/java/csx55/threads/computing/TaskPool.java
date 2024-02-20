package csx55.threads.computing;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.threads.hashing.Task;
import csx55.threads.node.ComputeNode;

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
        // Prolly going to need to save a reference to the threads 

        System.out.println("TaskPool: Number of threads: " + this.numberOfThreads);
        for(int i = 0; i < numberOfThreads; i ++){
            TaskThread task = new TaskThread(tasks);
            Thread taskThread = new Thread(task);
            taskThread.start();
        }
    }
}
