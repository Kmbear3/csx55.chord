package csx55.overlay.util;

import java.io.IOException;
import java.net.InetAddress;
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
            System.out.println("VertexList.registerVertex");
            RegistrationRequest regReq = new RegistrationRequest(event.getBytes());
            Vertex vertex = new Vertex(regReq.getIP(), regReq.getPort(), socket);

            RegisterationResponse registerationResponse;
            
            if(inList(vertex) == false){
                correctIP(vertex);
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

            System.out.println("Trying to send repsponse back");
            
            TCPSender tcpSender = new TCPSender(vertex.getSocket());
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

    public boolean correctIP(Vertex vertex){
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
}
