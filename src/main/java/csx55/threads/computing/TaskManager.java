package csx55.threads.computing;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.threads.balancing.BalanceLoad;
import csx55.threads.hashing.Task;
import csx55.threads.node.ComputeNode;
import csx55.threads.wireformats.NodeTasks;
import csx55.threads.wireformats.RoundIncrement;

public class TaskManager implements Runnable{

    private final int numberOfRounds;
    Random rand = new Random();
    ComputeNode node;
    ConcurrentLinkedQueue<Task> tasks;
    BalanceLoad balancer;
    boolean createNewRound = true; // Set to true initially because the nodes need create messages first.
    int numberOfNodesInOverlay;
    int receivedRoundInrementMessage = 0;

    public TaskManager(int numberOfRounds, ComputeNode node, ConcurrentLinkedQueue<Task> tasks, BalanceLoad balancer, int numberOfNodesInOverlay){
        this.numberOfRounds = numberOfRounds;
        this.node = node;
        this.tasks = tasks;
        this.balancer = balancer;
        this.numberOfNodesInOverlay = numberOfNodesInOverlay;
    }

    public void createTasks(int roundNumber, int numberOfTasks){

        for(int i = 0; i < numberOfTasks; i ++){
            int payload = rand.nextInt();
            tasks.add(new Task(node.getMessagingNodeIP(), node.getMessagingNodePort(), roundNumber, payload));
        }
    }


    synchronized public void beginNewRound(int roundNumber){
        try {

            int numberOfTasks = rand.nextInt(1000) + 1;
            createTasks(roundNumber, numberOfTasks);

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
        receivedRoundInrementMessage ++;
        
        if(!roundIncrement.getID().equals(node.getID())){
            node.sendClockwise(roundIncrement.getBytes());
        }

        if(receivedRoundInrementMessage == numberOfNodesInOverlay){
            createNewRound = true;
            receivedRoundInrementMessage = 0;
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
                    roundNumber++;
                }
                if(tasks.size() == 0 && createNewRound == false){
                    System.out.println("Tasks are completed... Sending Round Increment Directive");
                    RoundIncrement roundIncrement = new RoundIncrement(node.getMessagingNodeIP(), node.getMessagingNodePort(), roundNumber);
                    node.sendClockwise(roundIncrement.getBytes());
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
