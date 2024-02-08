package csx55.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LinkWeights implements Event, Protocol{
    // Message Type: Link_Weights
    // Number of links: L
    // Linkinfo1
    // Linkinfo2
    // ...
    // LinkinfoL

    // A Linkinfo connecting messaging nodes A and B contains the following fields: hostnameA:portnumA hostnameB:portnumB weight
    
    private int numberOfLinks;
    ArrayList<String> linkInfos = new ArrayList<>();
    private final int[][] connections;

    // hostnameA:portnumA hostnameB:portnumB weight
    public LinkWeights(int[][] connections, ArrayList<String> linkInfos){
        this.connections = connections;
        this.linkInfos = linkInfos;
        this.numberOfLinks = linkInfos.size();
    }

    public LinkWeights(byte[] marshalledBytes) throws IOException {
        System.out.println("COnstructor called");
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
       
        int messageType = din.readInt();

        if(Protocol.Link_Weights != messageType){
            System.err.println("Type mis-match in LinkWeights");
        }

        this.numberOfLinks = din.readInt();
        System.out.println(this.numberOfLinks);

        for(int i = 0; i < this.numberOfLinks; i ++){
            int linkLength = din.readInt();
            byte[] linkBytes = new byte[linkLength];
            din.readFully(linkBytes);
            String linkInfo = new String(linkBytes);

            System.out.println(linkInfo);
            this.linkInfos.add(linkInfo);
        }

        this.connections = createConnections(linkInfos);

        baInputStream.close();
        din.close();
    }


    synchronized public int[][] createConnections(ArrayList<String> linkInfos){
        // This convert linkInfos to int[][]
        int numberOfNodes = (int) Math.floor(Math.sqrt(linkInfos.size()));
        int[][] nodeConnections = new int[numberOfNodes][numberOfNodes];
        
        int index = 0;

        for(int i = 0;  i < numberOfNodes; i ++ ){
            for(int j = 0; j < numberOfNodes; j ++){
                // linkInfos.add(names.get(i) + " " + names.get(j) + " " + linkWeights[i][j]);

                nodeConnections[i][j] = Integer.parseInt(linkInfos.get(index).split(" ")[2]);
                index++;
            }
        }

        return nodeConnections;
    }

    public int[][] getConnections(){
        return connections;
    }

    @Override
    public int getType() {
        return Protocol.Link_Weights;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(Protocol.Link_Weights);
        dout.writeInt(this.numberOfLinks);
       
       
        for(int i = 0; i < this.numberOfLinks; i ++){
            byte[] link = linkInfos.get(i).getBytes();
            int elementLength = link.length;
            dout.writeInt(elementLength);
            dout.write(link);
        }
       
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;

    } 
}
