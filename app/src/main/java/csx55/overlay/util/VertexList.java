package csx55.overlay.util;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import csx55.overlay.wireformats.Event;
import csx55.overlay.wireformats.RegisterationResponse;
import csx55.overlay.wireformats.RegistrationRequest;

public class VertexList {
    ConcurrentHashMap<String, Vertex> registeredVertex;  

    public VertexList(){
        this.registeredNodes = new ConcurrentHashMap<>();
    }

    public void registerVertex(Event event){
        
        try {
            RegistrationRequest regReq = new RegistrationRequest(event.getBytes());
            Vertex vertex = new Vertex(regReq.getIP(), regReq.getPort());
            if(inList(vertex) == false){
                registeredVertex.put(vertex.getID(), vertex);
                RegisterationResponse registerationResponse = new RegisterationResponse();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deregisterVertex(){
        // TODO: implement
    }

    public boolean inList(Vertex vertex){
        //TODO: implement
        return false;
    }
}
