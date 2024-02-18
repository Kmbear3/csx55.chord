package csx55.threads.node;

import java.net.Socket;

import csx55.threads.wireformats.Event;

public interface Node {
    public void onEvent(Event event, Socket socket);
}
