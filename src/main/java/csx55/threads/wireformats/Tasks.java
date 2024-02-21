package csx55.threads.wireformats;

import java.io.IOException;
import java.util.ArrayList;

import csx55.threads.hashing.Task;

public class Tasks implements Protocol, Event{

    public Tasks(ArrayList<Task> taskList) {
        //TODO Auto-generated constructor stub
    }

    @Override
    public int getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    }

    @Override
    public byte[] getBytes() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBytes'");
    }

    public ArrayList<Task> getTaskList() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTaskList'");
    }
    
}
