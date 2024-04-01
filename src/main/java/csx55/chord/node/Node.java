package csx55.chord.node;

import java.net.Socket;

import csx55.chord.wireformats.Event;

public interface Node {
    public void onEvent(Event event, Socket socket);
}
