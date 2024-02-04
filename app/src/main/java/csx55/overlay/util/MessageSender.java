package csx55.overlay.util;

import csx55.overlay.wireformats.Message;
import csx55.overlay.wireformats.Poke;

import java.util.concurrent.ConcurrentLinkedQueue;
import csx55.overlay.node.MessagingNode;

public class MessageSender {
    private ConcurrentLinkedQueue<Message> messages;
    private int numberOfRounds;
    private MessagingNode node;

    public MessageSender(MessagingNode node, ConcurrentLinkedQueue<Message> messages, int numberOfRounds){
        this.messages = messages;
        this.node = node;
        this.numberOfRounds = numberOfRounds;
    }

    public MessageSender(MessagingNode node){
        this.node = node;
    }

    synchronized public void sendPoke(){
        Poke poke = new Poke(node.getMessagingNodeIP(), node.getMessagingNodePort());
        VertexList peerList = node.getPeerList();
        peerList.printVertexList();
        peerList.sendAllNodes(poke);
    }
}
