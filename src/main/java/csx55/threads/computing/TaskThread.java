package csx55.threads.computing;

import java.util.concurrent.ConcurrentLinkedQueue;

import csx55.threads.hashing.Miner;
import csx55.threads.hashing.Task;

public class TaskThread implements Runnable {
    ConcurrentLinkedQueue<Task> tasks;

    public TaskThread(ConcurrentLinkedQueue<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void run() {
        Miner miner = new Miner();

        while(true){
            Task task = tasks.poll();
            if(task != null){
                miner.mine(task);
            }
        }
    }
    
}
