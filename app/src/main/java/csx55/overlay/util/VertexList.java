package csx55.overlay.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.plaf.nimbus.State;

import csx55.overlay.transport.TCPSender;
import csx55.overlay.wireformats.Event;
import csx55.overlay.wireformats.RegisterationResponse;
import csx55.overlay.wireformats.RegistrationRequest;

public class VertexList {
    ConcurrentHashMap<String, Vertex> registeredVertexs;  
    ArrayList<String> vertexIDs; 

    public VertexList(){
        this.registeredVertexs = new ConcurrentHashMap<>();
        this.vertexIDs = new ArrayList<>();
    }

    public void registerVertex(Event event, Socket socket){
        try {
            RegistrationRequest regReq = new RegistrationRequest(event.getBytes());
            Vertex vertex = new Vertex(regReq.getIP(), regReq.getPort(), socket);

            RegisterationResponse registerationResponse;
            
            if(inList(vertex) == false && correctIP(vertex) == true){
                addToList(vertex);
                byte statusCode = StatusCodes.SUCCESS;
                String additionalInfo = registrationInfo(StatusCodes.SUCCESS);
                registerationResponse = new RegisterationResponse(statusCode, additionalInfo);
            }   
            else if(correctIP(vertex) == false){
                byte statusCode = StatusCodes.FAILURE_IP;
                String additionalInfo = registrationInfo(StatusCodes.FAILURE_IP);
                registerationResponse = new RegisterationResponse(statusCode, additionalInfo);
            }
            else{
                // Already in Overlay
                byte statusCode = StatusCodes.FAILURE;
                String additionalInfo = registrationInfo(StatusCodes.FAILURE);
                registerationResponse = new RegisterationResponse(statusCode, additionalInfo);
            }

            // TCPSender tcpSender = new TCPSender(vertex.getSocket());
            // tcpSender.sendData(registerationResponse.getBytes());

            vertex.sendMessage(registerationResponse.getBytes());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    synchronized public void addToList(Vertex vertex){
        registeredVertexs.put(vertex.getID(), vertex);
        vertexIDs.add(vertex.getID());

    }

    public boolean allTasksAreComplete(){
        for(Vertex vertex : this.getValues()){
            if(!vertex.isTaskComplete()){
                return false;
            }
        }
        return true; 
    }


    public Collection<Vertex> getValues(){
        return registeredVertexs.values();
    }

    public ArrayList<String> getVertexNames(){
        return vertexIDs;
    }

    public void deregisterVertex(){
        // Remove from conncurrent hashmap and arraylist of IDS
        // TODO: implement
    }

    public boolean inList(Vertex vertex){
        // Lets do correctness verification here.
        return registeredVertexs.containsKey(vertex.getID());
    }

    synchronized public boolean correctIP(Vertex vertex){
        // Checks to see if node ip match socket ip
        Socket socket = vertex.getSocket(); 
        InetAddress inAd = socket.getInetAddress();
        String remoteAdd = inAd.getHostName();

        int endIndex = remoteAdd.indexOf(".");
        System.out.println("Vertex IP: " + vertex.getIP());
        System.out.println("Socket IP: " + remoteAdd.substring(0, endIndex));
        String requestString = vertex.getIP();
        String socketString = remoteAdd.substring(0, endIndex);

        return requestString.equals(socketString);
    }

    public String registrationInfo(byte statusCode){
        switch(statusCode){
            case StatusCodes.SUCCESS:
                return "Registration request successful. The number of messaging nodes currently constituting the overlay is " + registeredVertexs.size();
            case StatusCodes.FAILURE:
                return "Registration request unsuccessful. Node already in overlay. The number of messaging nodes currently constituting the overlay is " + registeredVertexs.size();
            case StatusCodes.FAILURE_IP:
                return "Registration request unsuccessful. IP in request mismatches the InputStream IP. The number of messaging nodes currently constituting the overlay is " + registeredVertexs.size();
            default:
                return "Issue with registration";
        }
    }

    public int size(){
        // returns an approximation 
        return registeredVertexs.size();
    }

    public Vertex get(String keyName){
        return registeredVertexs.get(keyName);
    }

    synchronized public void printVertexList(){
        System.out.print("--- VertexList: " );

        for(String name : this.vertexIDs){
            System.out.print(name + ", ");
        }
        System.out.println("---" );

    }

    synchronized public void sendAllNodes(Event event){
        try {
            for(Vertex vertex : this.getValues()){
                vertex.printVertex();

                vertex.sendMessage(event.getBytes());
                // TCPSender send = new TCPSender(vertex.getSocket());
                // send.sendData(event.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
