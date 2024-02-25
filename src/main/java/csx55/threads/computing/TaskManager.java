package csx55.threads.computing;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.threads.balancing.BalanceLoad;
import csx55.threads.hashing.Task;
import csx55.threads.node.ComputeNode;
import csx55.threads.wireformats.NodeTasks;

public class TaskManager implements Runnable{

    private final int numberOfRounds;
    Random rand = new Random();
    ComputeNode node;
    ConcurrentLinkedQueue<Task> tasks;
    BalanceLoad balancer;
    

    public TaskManager(int numberOfRounds, ComputeNode node, ConcurrentLinkedQueue<Task> tasks, BalanceLoad balancer){
        this.numberOfRounds = numberOfRounds;
        this.node = node;
        this.tasks = tasks;
        this.balancer = balancer;
    }

    public void createTasks(int roundNumber, int numberOfTasks){

        for(int i = 0; i < numberOfTasks; i ++){
            int payload = rand.nextInt();
            tasks.add(new Task(node.getMessagingNodeIP(), node.getMessagingNodePort(), roundNumber, payload));
        }
    }

    @Override
    public void run() {
        try {

            int roundNumber = 0;
            while(roundNumber < numberOfRounds){
                if(tasks.size() == 0){
                    int numberOfTasks = rand.nextInt(1000) + 1;

                    createTasks(roundNumber, numberOfTasks);

                    NodeTasks nodeTask = new NodeTasks(node.getID(), numberOfTasks, roundNumber);
                    node.sendClockwise(nodeTask.getBytes());

                    System.out.println("Tasks created: " + numberOfTasks);

                    balancer.setNewRound(roundNumber, numberOfTasks);
                    roundNumber++;
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
