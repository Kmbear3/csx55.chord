package csx55.chord.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import csx55.chord.wireformats.Event;
import csx55.chord.wireformats.RegisterationResponse;
import csx55.chord.wireformats.RegistrationRequest;

public class VertexList{
    ConcurrentHashMap<Integer, Vertex> registeredVertexs;  

    public VertexList(){
        this.registeredVertexs = new ConcurrentHashMap<>();
    }

    public void registerVertex(Event event, Socket socket){
        try {
            RegistrationRequest regReq = new RegistrationRequest(event.getBytes());
            Vertex vertex = new Vertex(regReq.getPeerId(), regReq.getIP(), regReq.getPort(), socket);

            RegisterationResponse registerationResponse;

            // System.out.println("- Received RegReq -\n" + regReq.getIP() + "\n" + regReq.getPort() + "\n" + regReq.getPeerId());
            
            if(correctIP(vertex) == true){
                int peerID = addToList(vertex);
                byte statusCode = StatusCodes.SUCCESS;
                String additionalInfo = registrationInfo(StatusCodes.SUCCESS);

                Vertex randomPeer = randomPeer(vertex);
                registerationResponse = new RegisterationResponse(statusCode, additionalInfo, peerID, randomPeer);
            }   
            else{
                byte statusCode = StatusCodes.FAILURE_IP;
                String additionalInfo = registrationInfo(StatusCodes.FAILURE_IP);
                registerationResponse = new RegisterationResponse(statusCode, additionalInfo, -1, null);
            }
            
            vertex.sendMessage(registerationResponse.getBytes());
            // System.out.println("Number of Nodes is Overlay: " + registeredVertexs.size());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    synchronized private Vertex randomPeer(Vertex me) {
        Random rand = new Random();
        ArrayList<Vertex> vertexes = new ArrayList<>();
        vertexes.addAll(registeredVertexs.values());

        int randomPeer = rand.nextInt(vertexes.size());

        if(vertexes.size() == 1 && vertexes.get(0).equals(me)){
            return me;
        }

        Vertex peerNode;
        while (true) {
            peerNode = vertexes.get(randomPeer);
            if(!peerNode.equals(me)){
                return peerNode;
            }
            randomPeer = rand.nextInt(vertexes.size());
        }
    }

    synchronized public int addToList(Vertex vertex){
        if(registeredVertexs.get(vertex.getID()) == null){
            registeredVertexs.put(vertex.getID(), vertex);
            return vertex.getID();
        }else{
            // The case of a collision

            String Ip = vertex.getIP();
            int port = vertex.getPort(); 
            int collisionID = vertex.getID();

            while(registeredVertexs.containsKey(collisionID)){
                Ip = Ip + "0";
                collisionID = (Ip + ":" + port).hashCode();
            }

            vertex.setID(collisionID);
            registeredVertexs.put(vertex.getID(), vertex);
            return vertex.getID();
        }
    }

    public Collection<Vertex> getValues(){
        return registeredVertexs.values();
    }

    public void deregisterVertex(int peerID){
        registeredVertexs.remove(peerID);
        // System.out.println("Peer Removed from Overlay: " + peerID);
    }

    public boolean inList(Vertex vertex){
        return registeredVertexs.containsKey(vertex.getID());
    }

    synchronized public boolean correctIP(Vertex vertex){
        // Checks to see if node ip match socket ip
        Socket socket = vertex.getSocket(); 
        InetAddress inAd = socket.getInetAddress();
        String remoteAdd = inAd.getHostAddress();

        return remoteAdd.equals(vertex.getIP());
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

    public Vertex get(int peerID){
        return registeredVertexs.get(peerID);
    }

    synchronized public void printVertexList(){
        ArrayList<Vertex> vertexes = new ArrayList<>();
        vertexes.addAll(registeredVertexs.values());

        Collections.sort(vertexes);
        for(Vertex vertex : vertexes){
            System.out.println(vertex.getID() + " " + vertex.getIP() + ":" + vertex.getPort());
        }
    }

    synchronized public void sendAllNodes(Event event){
        // Add logic to remove node from the registry if it is uncontactable. 
        try {
            for(Vertex vertex : this.getValues()){

                // vertex.printVertex();
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
                System.out.println("Unexpected Connection loss, removing from overlay. " + registeredVertexs.size());
            }
        }
    }
}
