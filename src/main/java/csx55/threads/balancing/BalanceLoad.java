package csx55.threads.balancing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.threads.ComputeNode;
import csx55.threads.hashing.Task;
import csx55.threads.util.StatisticsCollectorAndDisplay;
import csx55.threads.wireformats.NodeTasks;
import csx55.threads.wireformats.Tasks;

public class BalanceLoad {
    int numberOfNodesInOverlay;
    ComputeNode computeNode;
    ConcurrentLinkedQueue<Task> tasks;
    StatisticsCollectorAndDisplay stats;
    int totalTasksInOverlay = 0;
    int roundNumber; 
    int receivedNodeMessage;
    int tasksCreatedForRound;
    int average;
    int accumulatedTasksForRound = 0;


    public BalanceLoad(int numberOfNodesInOverlay, ComputeNode computeNode, ConcurrentLinkedQueue<Task> tasks, StatisticsCollectorAndDisplay stats){
        this.numberOfNodesInOverlay = numberOfNodesInOverlay;
        this.computeNode = computeNode;
        this.tasks = tasks;
        this.stats = stats;
    }
    
    synchronized public void addToSum(NodeTasks nodeTasks){
        try {
            receivedNodeMessage = receivedNodeMessage + 1;
            totalTasksInOverlay += nodeTasks.getNumberOfTasks();

            if(!nodeTasks.getId().equals(computeNode.getID())){
                computeNode.sendClockwise(nodeTasks.getBytes());
            }
    
            if(receivedNodeMessage == numberOfNodesInOverlay){
                // New round needs to be calculated, thus receivedNodeMessage needs to be reset
                receivedNodeMessage = 0;
                calculateAverage();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public void setNewRound(int tasksCreated){
        this.tasksCreatedForRound = tasksCreated;
        this.accumulatedTasksForRound = 0;
    }

    synchronized public void calculateAverage(){
        this.average = totalTasksInOverlay / numberOfNodesInOverlay;
        this.totalTasksInOverlay = 0;

        if((this.tasksCreatedForRound + accumulatedTasksForRound) > average){
            int difference = (this.tasksCreatedForRound + accumulatedTasksForRound) - average;
            this.accumulatedTasksForRound = accumulatedTasksForRound - difference;
            sendTasksClockwise(difference);
        }
    }

    synchronized private void sendTasksClockwise(int difference) {
        ArrayList<Task> taskList = new ArrayList<>();
        
        for(int i = 0; i < difference; i++){
            Task task = tasks.poll();

            if(task != null){
                stats.incrementPushedTasks();
                taskList.add(task);
            }
        }

        Tasks taskMessage = new Tasks(taskList);

        try {
            computeNode.sendClockwise(taskMessage.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    synchronized public void receiveTasks(ArrayList<Task> taskList) throws IOException{
        ArrayList<Task> relayTasks = new ArrayList<>();
        
        for(int i = 0; i < taskList.size(); i++){
            Task task = taskList.get(i);

            if((this.tasksCreatedForRound + this.accumulatedTasksForRound) < average){
                this.accumulatedTasksForRound ++;
                this.tasks.add(task);  // TAsk list may have fewer tasks than necessary. 
                stats.incrementPulledTasks();
            }
            else if(this.computeNode.originated(task)){
                this.accumulatedTasksForRound ++;
                tasks.add(task);
                stats.incrementPulledTasks();
            }else{
                relayTasks.add(task);
            }
        }

        if(relayTasks.size() != 0){
            Tasks relayTasksMessage = new Tasks(relayTasks);
            computeNode.sendClockwise(relayTasksMessage.getBytes());
        }
    }
}
