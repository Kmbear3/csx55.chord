package csx55.chord.computing;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.chord.Peer;
import csx55.chord.hashing.Task;

public class TaskPool {
    ConcurrentLinkedQueue<Task> tasks;
    int numberOfThreads;
    Peer node;

    public TaskPool(Peer node, ConcurrentLinkedQueue<Task> tasks, int numberOfThreads) {
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
