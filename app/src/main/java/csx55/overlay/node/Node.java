package csx55.overlay.node;

import java.net.Socket;

import csx55.overlay.wireformats.Event;

public interface Node {
    public void onEvent(Event event);
    public void onEvent(Event event, Socket socket);
}
