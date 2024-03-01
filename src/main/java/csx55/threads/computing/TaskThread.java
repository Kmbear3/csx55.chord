package csx55.threads.computing;

import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.threads.hashing.Miner;
import csx55.threads.hashing.Task;
import csx55.threads.util.StatisticsCollectorAndDisplay;

public class TaskThread implements Runnable {
    ConcurrentLinkedQueue<Task> tasks;
    StatisticsCollectorAndDisplay stats;

    public TaskThread(ConcurrentLinkedQueue<Task> tasks, StatisticsCollectorAndDisplay stats) {
        this.tasks = tasks;
        this.stats = stats;
    }

    @Override
    public void run() {
        Miner miner = new Miner();

        while(true){
            Task task = tasks.poll();
            if(task != null){
                miner.mine(task);
                System.out.println(task.toString());
                stats.incrementCompletedTasks();
            }
        }
    }
    
}
