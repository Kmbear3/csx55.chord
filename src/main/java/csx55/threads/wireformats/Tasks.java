package csx55.threads.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import csx55.threads.hashing.Task;

public class Tasks implements Protocol, Event{
    ArrayList<Task> taskList = new ArrayList<>();

    public Tasks(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }

    public Tasks(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        int type = din.readInt();
        if(type != Protocol.TASKS){
            System.err.println("type mismatch in Tasks.java");
        }

        int numberOfTasks = din.readInt();

        for(int i = 0; i < numberOfTasks; i++){
            int IPLength = din.readInt();
            byte[] IPBytes = new byte[IPLength];
            din.readFully(IPBytes);
            String taskIP = new String(IPBytes);

            int port = din.readInt();
            int roundNumber = din.readInt();
            int payload = din.readInt();

            Task task = new Task(taskIP, port, roundNumber, payload);
            taskList.add(task);
        }

        baInputStream.close();
        din.close();

    }

    @Override
    public int getType() {
        return Protocol.TASKS;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(Protocol.TASKS);   

        dout.writeInt(this.taskList.size());   


        for(int i = 0; i < this.taskList.size(); i++) {
            Task task = this.taskList.get(i);
            String ip = task.getIp();

            byte[] IDBytes = ip.getBytes();
            int elementLength = IDBytes.length;
            dout.writeInt(elementLength);
            dout.write(IDBytes);

            dout.writeInt(task.getPort());
            dout.writeInt(task.getRoundNumber());
            dout.writeInt(task.getPayload());
    
        }


        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }

    public ArrayList<Task> getTaskList() {
       return this.taskList;
    }
    
}
