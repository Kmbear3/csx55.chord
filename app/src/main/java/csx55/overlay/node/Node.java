package csx55.overlay.node;

import csx55.overlay.wireformats.Event;

public interface Node {
    public void onEvent(Event event);
}
