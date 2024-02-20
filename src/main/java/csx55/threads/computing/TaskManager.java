package csx55.threads.computing;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.threads.hashing.Task;
import csx55.threads.node.ComputeNode;

public class TaskManager implements Runnable{

    private final int numberOfRounds;
    Random rand = new Random();
    ComputeNode node;
    ConcurrentLinkedQueue<Task> tasks;

    public TaskManager(int numberOfRounds, ComputeNode node, ConcurrentLinkedQueue<Task> tasks){
        this.numberOfRounds = numberOfRounds;
        this.node = node;
        this.tasks = tasks;
    }

    public void createTasks(int roundNumber){
        int numberOfTasks = rand.nextInt(1000) + 1;

        for(int i = 0; i < numberOfTasks; i ++){
            int payload = rand.nextInt();
            tasks.add(new Task(node.getMessagingNodeIP(), node.getMessagingNodePort(), roundNumber, payload));
        }
    }

    @Override
    public void run() {
        System.out.println("TaskManager: Number of rounds: " + this.numberOfRounds);
        
        for(int i = 0; i < numberOfRounds; i++){
            createTasks(i);
        }
    }
}
