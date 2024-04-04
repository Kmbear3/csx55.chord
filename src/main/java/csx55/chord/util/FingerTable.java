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

public class FingerTable {

    PeerEntry[] fingerTable;
    PeerEntry me;

    PeerEntry succ;
    PeerEntry pred;

    public FingerTable(PeerEntry me){
        // First node to join, so its the only node in the overlay
        fingerTable = new PeerEntry[32];
        this.me = me;
        constructInitialTable();
    }

    public FingerTable(Vertex randomPeer){
        // Start constructing the finger table with a random peer

        try {
            Socket randomPeerConn = new Socket(randomPeer.getIP(), randomPeer.getPort());
            TCPSender sender = new TCPSender(randomPeerConn);
            InsertRequest inReq = new InsertRequest(me.IP, me.port, me.peerID);

            sender.sendData(inReq.getBytes());

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
    }


    public PeerEntry successor(int peerID){
        //TODO 

        return null;
    }

    public void createFingerTableWithSuccessorInfo(InsertResponse insertResponse) {
        try{
            this.fingerTable = insertResponse.getFingerTable();
            this.succ = insertResponse.getSucc();
            this.pred = insertResponse.getPred();

            // Notify predecessor of my addition
            NewSuccessor newSuccessor = new NewSuccessor(me);
            pred.sendMessage(newSuccessor.getBytes());
      
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void handleNodeAdditionRequest(InsertRequest insertRequest) {
        try {

            String newPredIP = insertRequest.getIP();
            int newPredport = insertRequest.getPort();
            int newPredID = insertRequest.getPeerId();

            PeerEntry newPred = new PeerEntry(newPredIP, newPredport, newPredID);
            InsertResponse newAddition = new InsertResponse(me.getIP(),me.getPort(),me.getID(), this.fingerTable, newPred.getIP(), newPred.getPort(), newPred.getID());
            newPred.sendMessage(newAddition.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void newSucessor(NewSuccessor newSuccessor) {
        this.succ = newSuccessor.getNewSucc();
    }
}
