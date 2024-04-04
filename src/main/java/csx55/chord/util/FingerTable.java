package csx55.chord.util;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import csx55.chord.Peer;
import csx55.chord.transport.TCPSender;
import csx55.chord.wireformats.InsertRequest;
import csx55.chord.wireformats.InsertResponse;

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

    public void updateFingerTableWithSuccessorInfo(InsertResponse insertResponse) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateFingerTableWithSuccessorInfo'");
    }
}
