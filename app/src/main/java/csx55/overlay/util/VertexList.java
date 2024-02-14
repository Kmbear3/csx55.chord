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

    public String deregisterVertex(String id, String ip, Socket socket){
        if(!correctIP(socket, ip)){
            return deRegistrationInfo(StatusCodes.FAILURE_IP);
        }
        else if(!registeredVertexs.containsKey(id)){
            return deRegistrationInfo(StatusCodes.FAILURE);
        }
        else{
            registeredVertexs.remove(id);
            vertexIDs.remove(id);
            return deRegistrationInfo(StatusCodes.SUCCESS);
        }
    }

    public boolean inList(Vertex vertex){
        return registeredVertexs.containsKey(vertex.getID());
    }

    synchronized public boolean correctIP(Vertex vertex){
        // Checks to see if node ip match socket ip
        Socket socket = vertex.getSocket(); 
        InetAddress inAd = socket.getInetAddress();
        String remoteAdd = inAd.getHostName();

        int endIndex = remoteAdd.indexOf(".");
        String requestString = vertex.getIP();
        String socketString = remoteAdd.substring(0, endIndex);

        return requestString.equals(socketString);
    }

    //  Refactor
    synchronized public boolean correctIP(Socket socket, String ip){
        // Checks to see if node ip match socket ip
        InetAddress inAd = socket.getInetAddress();
        String remoteAdd = inAd.getHostName();

        int endIndex = remoteAdd.indexOf(".");
        String socketString = remoteAdd.substring(0, endIndex);

        return ip.equals(socketString);
    }

    public String registrationInfo(byte statusCode){
        switch(statusCode){
            case StatusCodes.SUCCESS:
                return "Registration request successful. The number of messaging nodes currently constituting the overlay is (" + registeredVertexs.size() + ")";
            case StatusCodes.FAILURE:
                return "Registration request unsuccessful. Node already in overlay. The number of messaging nodes currently constituting the overlay is (" + registeredVertexs.size() + ")";
            case StatusCodes.FAILURE_IP:
                return "Registration request unsuccessful. IP in request mismatches the InputStream IP. The number of messaging nodes currently constituting the overlay is (" + registeredVertexs.size() + ")";
            default:
                return "Issue with registration";
        }
    }


    public String deRegistrationInfo(byte statusCode){
        switch(statusCode){
            case StatusCodes.SUCCESS:
                return "Deregistration request successful. The number of messaging nodes currently constituting the overlay is (" + registeredVertexs.size() + ")";
            case StatusCodes.FAILURE:
                return "Deregistration request unsuccessful. Node not in overlay. The number of messaging nodes currently constituting the overlay is (" + registeredVertexs.size() + ")";
            case StatusCodes.FAILURE_IP:
                return "Deregistration request unsuccessful. IP in request mismatches the InputStream IP. The number of messaging nodes currently constituting the overlay is (" + registeredVertexs.size() + ")";
            default:
                return "Issue with Deregistration";
        }
    }


    public int size(){
        return registeredVertexs.size();
    }

    public Vertex get(String keyName){
        return registeredVertexs.get(keyName);
    }

    synchronized public void printVertexList(){
        for(String name : this.vertexIDs){
            System.out.println(name);
        }
    }

    synchronized public void sendAllNodes(Event event){
        // Add logic to remove node from the registry if it is uncontactable. 
        try {
            for(Vertex vertex : this.getValues()){

                vertex.printVertex();
                vertex.sendMessage(event.getBytes());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIPfromSocket(String socketID){
        String str = socketID.split("/")[1];
        String ip = str.split(",port=")[0];

        return ip;
    }
    
    public int getPortFromSocket(String socketId){
        String str = socketId.split(",port=")[1];
        String port = str.split(",localport=")[0];

        return Integer.parseInt(port);
    }

    public void removeFromList(String socketId) {
        for(Vertex vertex : this.getValues()){
    
            String vertexIP = getIPfromSocket(vertex.getSocket().toString());
            String socketIP = getIPfromSocket(socketId);

            int portVertex = getPortFromSocket(vertex.getSocket().toString());
            int portSocket = getPortFromSocket(socketId);

            if(portSocket == portVertex && vertexIP.equals(socketIP)){
                System.out.println(vertex.getID());
                registeredVertexs.remove(vertex.getID());
                vertexIDs.remove(vertex.getID());
                System.out.println("Unexpected Connection loss, removing from overlay. " + registeredVertexs.size());
            }
        }
    }
}
