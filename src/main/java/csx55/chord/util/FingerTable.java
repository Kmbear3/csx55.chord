package csx55.chord.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import csx55.chord.Peer;
import csx55.chord.transport.TCPSender;
import csx55.chord.wireformats.InsertRequest;
import csx55.chord.wireformats.InsertResponse;
import csx55.chord.wireformats.NewSuccessor;
import csx55.chord.wireformats.SuccessorRequest;
import csx55.chord.wireformats.SuccessorResponse;

public class FingerTable {

    PeerEntry[] fingerTable;
    PeerEntry me;

    PeerEntry succ;
    PeerEntry pred;

    boolean tableCreated = false;

    public FingerTable(PeerEntry me){
        // First node to join, so its the only node in the overlay
        fingerTable = new PeerEntry[32];
        this.me = me;
        constructInitialTable();
    }

    public FingerTable(Vertex randomPeer, PeerEntry me){
        // Start constructing the finger table with a random peer

        try {
            this.me = me;

            Socket randomPeerConn = new Socket(randomPeer.getIP(), randomPeer.getPort());
            TCPSender sender = new TCPSender(randomPeerConn);
            SuccessorRequest successorRequest = new SuccessorRequest(me, me);
            sender.sendData(successorRequest.getBytes());

            // InsertRequest inReq = new InsertRequest(me.IP, me.port, me.peerID);


        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    // No other entries are in the overlay, all entries in the finger table are me
    private void constructInitialTable(){
        for(int i = 0; i < fingerTable.length; i++){
            fingerTable[i] = me;
        }
        this.pred = me;
        this.succ = me;
        this.tableCreated = true;
    }


    public PeerEntry successor(int peerID){
        //TODO 

        return null;
    }

    public void createFingerTableWithSuccessorInfo(InsertResponse insertResponse) {
        // try{
            this.fingerTable = insertResponse.getFingerTable();
            this.succ = insertResponse.getSucc();
            this.pred = insertResponse.getPred();

            // Notify predecessor of my addition
            // NewSuccessor newSuccessor = new NewSuccessor(me);
            // pred.sendMessage(newSuccessor.getBytes());
      
        // } catch (IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }

    }

    public void handleNodeAdditionRequest(InsertRequest insertRequest) {
        try {

            String newPredIP = insertRequest.getIP();
            int newPredport = insertRequest.getPort();
            int newPredID = insertRequest.getPeerId();

            PeerEntry newPred = new PeerEntry(newPredIP, newPredport, newPredID);
            InsertResponse newAddition = new InsertResponse(me.getIP(), me.getPort(), me.getID(), this.fingerTable, newPred.getIP(), newPred.getPort(), newPred.getID());
            newPred.sendMessage(newAddition.getBytes());

            this.pred = newPred;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void newSucessor(NewSuccessor newSuccessor) {
        System.out.print("New successor: ");
        newSuccessor.getNewSucc().print();
        System.out.print("My successor prior to assignment: ");
        succ.print();
        this.succ = newSuccessor.getNewSucc();

        System.out.print("My pred prior to assignment: ");
        this.pred.print();

        this.succ = newSuccessor.getNewSucc();
        System.out.print("My successor after assignment: ");
        this.succ.print();

    }

    public void print(){
        for(int i = 0; i < fingerTable.length; i++){
            System.out.println((i + 1) + " " + fingerTable[i].peerID);
        }
    }

    public void neighbors() {
        System.out.println("predecessor: " + pred.peerID + " " + pred.IP + ":" + pred.port);
        System.out.println("successor: " + succ.peerID + " " + succ.IP + ":" + succ.port);

    }

    public boolean isBetween(int first, int second, int target){
        if(first < second){
            return first < target && target <= second;
        }
        else {
            return first < target || target <= second;
        }
    }

    public boolean IamSuccessor(int ID){
        return isBetween(pred.getID(), me.getID(), ID);
    }

    public PeerEntry lookup(PeerEntry lookupNode){
        if(IamSuccessor(lookupNode.getID())) {
            return me;
        }

        PeerEntry peer = this.succ;
    
        for(int i = 0; i < fingerTable.length; i++){
            if(isBetween(me.getID(), lookupNode.getID(), fingerTable[i].getID())){
                peer = fingerTable[i];
            }
        }

        return peer;

    }

    public void successorRequest(SuccessorRequest successorRequest) {
        PeerEntry peer = lookup(successorRequest.getTargetNode());

        try {
            if(peer.equals(me)){
                SuccessorResponse successorResponse = new SuccessorResponse(me);
                PeerEntry requestingNode = successorRequest.getRequestingNode();
                requestingNode.sendMessage(successorResponse.getBytes());
                System.out.println("Senidng to requesting node");
            }
            else{
                peer.sendMessage(successorRequest.getBytes());
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void succesorResponse(SuccessorResponse successorResponse) {
        if(!tableCreated){
            PeerEntry successor = successorResponse.getSuccessor();
            this.succ = successor;
            InsertRequest insert = new InsertRequest(me.IP, me.port, me.peerID);
            try {
                successor.sendMessage(insert.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            // DO other stuff
        }
       
    }
    

}
