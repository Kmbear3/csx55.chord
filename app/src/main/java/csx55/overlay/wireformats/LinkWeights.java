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
    ArrayList<String> linkInfos;
    private final int[][] connections;

    // hostnameA:portnumA hostnameB:portnumB weight
    public LinkWeights(int[][] connections, ArrayList<String> linkInfos){
        this.connections = connections;
        this.linkInfos = linkInfos;
    }

    public LinkWeights(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
       
        int messageType = din.readInt();
        this.numberOfLinks = din.readInt();

        for(int i = 0; i < this.numberOfLinks; i ++){
            int linkLength = din.readInt();
            byte[] linkBytes = new byte[linkLength];
            din.readFully(linkBytes);
            String linkInfo = new String(linkBytes);

            linkInfos.add(linkInfo);
        }

        connections = createConnections(linkInfos);

        baInputStream.close();
        din.close();
    }


    synchronized public int[][] createConnections(ArrayList<String> linkInfos){
        // This convert linkInfos to int[][]
        int[][] nodeConnections = new int[linkInfos.size()][linkInfos.size()];

        for(int i = 0;  i < linkInfos.size(); i ++ ){
            for(int j = 0; j < linkInfos.size(); j ++){
                // linkInfos.add(names.get(i) + " " + names.get(j) + " " + linkWeights[i][j]);

                nodeConnections[i][j] = Integer.parseInt(linkInfos.get(i).split(" ")[2]);
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

        dout.writeInt(Protocol.MESSAGE);
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
