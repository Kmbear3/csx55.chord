package csx55.chord.util;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import csx55.chord.Peer;
import csx55.chord.transport.TCPSender;
import csx55.chord.wireformats.InsertRequest;
import csx55.chord.wireformats.InsertResponse;
import csx55.chord.wireformats.MigrateFile;
import csx55.chord.wireformats.NewAddition;
import csx55.chord.wireformats.NewSuccessor;
import csx55.chord.wireformats.SuccessorRequest;
import csx55.chord.wireformats.SuccessorResponse;
import csx55.chord.util.FileManager;;

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

    public void createFingerTableWithSuccessorInfo(InsertResponse insertResponse) {
        try{
            this.fingerTable = insertResponse.getFingerTable();
            this.succ = insertResponse.getSucc();
            this.pred = insertResponse.getPred();

            NewSuccessor newSuccessor = new NewSuccessor(me);
            pred.sendMessage(newSuccessor.getBytes());

            succ.sendMessage(new NewAddition(me).getBytes());

            this.tableCreated = true;

            //Highly sus

            updateTableWithSuccessor();
      
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void updateTableWithSuccessor() {
        updateTable(this.succ);
    }

    public void handleNodeAdditionRequest(InsertRequest insertRequest) {
        // THis is when my new predecessor joins
        // I am its successor, and I give it my fingertable and my predecessor information
        // Transfer any files that need to be transferred to the new pred
        try {

            String newPredIP = insertRequest.getIP();
            int newPredport = insertRequest.getPort();
            int newPredID = insertRequest.getPeerId();

            PeerEntry newPred = new PeerEntry(newPredIP, newPredport, newPredID);
            InsertResponse newAddition = new InsertResponse(me.getIP(), me.getPort(), me.getID(), this.fingerTable, pred.getIP(), pred.getPort(), pred.getID());
            
            newPred.sendMessage(newAddition.getBytes());

            // this.pred = newPred;
            // System.out.println("Setting my NEW pred: " + newPred.toString());
            Thread.sleep(1000);
            setPred(newPred);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void newSucessor(NewSuccessor newSuccessor) {
        this.succ = newSuccessor.getNewSucc();
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

    public PeerEntry lookup(int target){
        if(IamSuccessor(target)) {
            return me;
        }

        PeerEntry peer = this.succ;
    
        for(int i = 0; i < fingerTable.length; i++){
            if(isBetween(me.getID(), target, fingerTable[i].getID())){
                peer = fingerTable[i];
            }
        }

        return peer;

    }

    public void successorRequest(SuccessorRequest successorRequest) {
        // Looking for a nodes sucessor
        PeerEntry peer = lookup(successorRequest.getTargetNode().getID());

        try {
            // If the peer is me, I am the new nodes successor responsed
            if(peer.equals(me)){
                SuccessorResponse successorResponse = new SuccessorResponse(me);
                PeerEntry requestingNode = successorRequest.getRequestingNode();
                requestingNode.sendMessage(successorResponse.getBytes());
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

    public void addNewAddition(NewAddition newAddition, FileManager fileManager) {
        try {
            if(!newAddition.getNode().equals(me)){
                // System.out.println("Recieved new Addition notification: " + newAddition.getNode().getID());
                updateTable(newAddition.getNode());
                
                if(!succ.equals(me)){
                    succ.sendMessage(newAddition.getBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
       
    }

    private void updateTable(PeerEntry node) {
        for(int i = 0; i < fingerTable.length; i++){
            int fingerTableRow = ((int)(me.getID() + Math.pow(2, i))) % (int) Math.pow(2, 32);

            if(isBetween(fingerTableRow, fingerTable[i].getID(), node.getID())){
                fingerTable[i] = node;
            }   

            // System.out.println(i + " k: " + fingerTableRow + " succ: " + fingerTable[i].getID());
        }
        
    }


    public void manageFileUpload(String pathName, FileManager fileManager) {
        String[] filePath = pathName.split("/");
        int fileID = filePath[filePath.length - 1].hashCode();

        PeerEntry peer = lookup(fileID);

        if(peer.equals(me)){
            fileManager.readStoreFile(pathName);
        }
        else{
            fileManager.readFowardFile(pathName, peer);
        }
    }
  
    public void sendSucc(byte[] bytes) {
        this.succ.sendMessage(bytes);
    }

    public void handleNodeExit(PeerEntry leavingPeer, PeerEntry leavingPeerSucc) {
        if(leavingPeer.equals(this.succ)){
            this.succ = leavingPeerSucc;
            // System.out.println("New successor: " + leavingPeerSucc.toString());
            // System.out.println("Old successor: " + leavingPeer.toString());
        }
        for(int i = 0 ; i < fingerTable.length; i++){
            if(fingerTable[i].equals(leavingPeer)){
                fingerTable[i] = leavingPeerSucc;

            }
        }

        //TOD0: Pred is also not being updated
    }   

    public PeerEntry getMe() {
        return this.me;
    }

    public PeerEntry getSucc() {
        return this.succ;
    }

    public PeerEntry getPred() {
        return this.pred;
    }

    public void setPred(PeerEntry newPred) {
        this.pred = newPred;
        
        String storeagePath = "/tmp/" + me.peerID + "/";
        File parentDirectory = new File(storeagePath);
        File[] files = parentDirectory.listFiles();

        try{
            for(File file : files){
                // System.out.println("file: " + file.getName() + " " + file.getName().hashCode());
                // System.out.println("this is my pred: " + this.pred.getID());
                if(isBetween(me.peerID, this.pred.peerID, file.getName().hashCode())){
                    // System.out.println("file need to be sent to pred!");
                    byte[] fileBytes = FileManager.readFromDisk(storeagePath+file.getName());
                    MigrateFile migratingFile = new MigrateFile(file.getName(), fileBytes);
                    this.pred.sendMessage(migratingFile.getBytes());

                    // Remove file after sending
                    // I DO NOT LIKE THIS REMOVE!!!!!
                    file.delete();
                }
            }
        }catch(IOException e){
            System.err.println("Error experienced migrating files to pred: " + e.getMessage());
        }
    }

    public void sendPred(byte[] message){
        this.pred.sendMessage(message);
    }
}
