package csx55.overlay.util;

import csx55.overlay.wireformats.Message;
import java.util.concurrent.ConcurrentLinkedQueue;


public class MessageSender {
    ConcurrentLinkedQueue<Message> messages;

    public MessageSender(ConcurrentLinkedQueue<Message> messages){
        this.messages = messages;
    }
}
