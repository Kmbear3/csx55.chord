package csx55.threads.computing;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.threads.balancing.BalanceLoad;
import csx55.threads.hashing.Task;
import csx55.threads.node.ComputeNode;
import csx55.threads.util.StatisticsCollectorAndDisplay;
import csx55.threads.wireformats.NodeTasks;
import csx55.threads.wireformats.RoundIncrement;
import csx55.threads.wireformats.TaskComplete;

public class TaskManager implements Runnable{

    private final int numberOfRounds;
    Random rand = new Random();
    ComputeNode node;
    ConcurrentLinkedQueue<Task> tasks;
    BalanceLoad balancer;
    boolean createNewRound = true; // Set to true initially because the nodes need create messages first.
    int numberOfNodesInOverlay;
    int receivedRoundInrementMessage = 0;
    boolean receivedPermission = true;
    StatisticsCollectorAndDisplay stats;

    public TaskManager(int numberOfRounds, ComputeNode node, ConcurrentLinkedQueue<Task> tasks, BalanceLoad balancer, int numberOfNodesInOverlay, StatisticsCollectorAndDisplay stats){
        this.numberOfRounds = numberOfRounds;
        this.node = node;
        this.tasks = tasks;
        this.balancer = balancer;
        this.numberOfNodesInOverlay = numberOfNodesInOverlay;
        this.stats = stats;
    }

    synchronized public void createTasks(int roundNumber, int numberOfTasks){

        for(int i = 0; i < numberOfTasks; i ++){
            int payload = rand.nextInt();
            tasks.add(new Task(node.getMessagingNodeIP(), node.getMessagingNodePort(), roundNumber, payload));
        }
    }


    synchronized public void beginNewRound(int roundNumber){
        try {

            int numberOfTasks = rand.nextInt(1000) + 1;
            createTasks(roundNumber, numberOfTasks);

            stats.incrementGeneratedTasks(numberOfTasks);

            NodeTasks nodeTask = new NodeTasks(node.getID(), numberOfTasks, roundNumber);
            node.sendClockwise(nodeTask.getBytes());
            balancer.setNewRound(numberOfTasks);

            System.out.println("Tasks created: " + numberOfTasks);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    synchronized public void receiveRoundCompleteMessage(RoundIncrement roundIncrement) throws IOException{
        this.receivedRoundInrementMessage  = receivedRoundInrementMessage + 1;
        System.out.println("Recievd Round increment: " + roundIncrement.getID());
        System.out.println("ME: " + node.getID());

        
        if(!roundIncrement.getID().equals(node.getID())){
            System.out.println("Sending: " + roundIncrement.getID());
            node.sendClockwise(roundIncrement.getBytes());
        }

        if(receivedRoundInrementMessage == numberOfNodesInOverlay){
            createNewRound = true;
            this.receivedRoundInrementMessage = 0;
        }
    }

    @Override
    public void run() {
        try {

            int roundNumber = 0;
            while(roundNumber < numberOfRounds){
                if(createNewRound){
                    beginNewRound(roundNumber);
                    createNewRound = false;
                    receivedPermission = true;
                    roundNumber++;
                }
                else if(tasks.size() == 0 && createNewRound == false && receivedPermission){
                    System.out.println("Tasks are completed... Sending Round Increment Directive");
                    RoundIncrement roundIncrement = new RoundIncrement(node.getMessagingNodeIP(), node.getMessagingNodePort(), roundNumber);
                    node.sendClockwise(roundIncrement.getBytes());
                    receivedPermission = false;
                }
            }
            while(tasks.size() != 0){
                
            }
            
            TaskComplete complete = new TaskComplete(node.getMessagingNodeIP(), node.getMessagingNodePort());
            node.sendRegistryMessage(complete);
            System.out.println("Tasks are complete! - " + node.getID());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
