package csx55.overlay.util;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.plaf.nimbus.State;

import csx55.overlay.transport.TCPSender;
import csx55.overlay.wireformats.Event;
import csx55.overlay.wireformats.RegisterationResponse;
import csx55.overlay.wireformats.RegistrationRequest;

public class VertexList {
    ConcurrentHashMap<String, Vertex> registeredVertexs;  

    public VertexList(){
        this.registeredVertexs = new ConcurrentHashMap<>();
    }

    public void registerVertex(Event event, Socket socket){
        
        try {
            RegistrationRequest regReq = new RegistrationRequest(event.getBytes());
            Vertex vertex = new Vertex(regReq.getIP(), regReq.getPort());

            RegisterationResponse registerationResponse;
            
            if(inList(vertex) == false){
                registeredVertexs.put(vertex.getID(), vertex);
                byte statusCode = StatusCodes.SUCCESS;
                String additionalInfo = registrationInfo(StatusCodes.SUCCESS);
                registerationResponse = new RegisterationResponse(statusCode, additionalInfo);
            }   
            else{
                byte statusCode = StatusCodes.FAILURE;
                String additionalInfo = registrationInfo(StatusCodes.FAILURE);
                registerationResponse = new RegisterationResponse(statusCode, additionalInfo);
            }
            
            TCPSender tcpSender = new TCPSender(null);
            tcpSender.sendData(registerationResponse.getBytes());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deregisterVertex(){
        // TODO: implement
    }

    public boolean inList(Vertex vertex){
        // Lets do correctness verification here.
        return false;
    }

    public String registrationInfo(byte statusCode){
        switch(statusCode){
            case StatusCodes.SUCCESS:
                return "Registration request successful. The number of messaging nodes currently constituting the overlay is " + registeredVertexs.size();
            case StatusCodes.FAILURE:
                return "Registration request unsuccessful. Node already in overlay";
            default:
                return "Issue with registration";
        }
    }
}
