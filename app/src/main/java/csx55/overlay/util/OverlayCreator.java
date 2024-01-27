package csx55.overlay.util;

import csx55.overlay.node.Node;
import csx55.overlay.node.Registry;

public class OverlayCreator {
    // Needs registered nodes
    int numberOfConnections;
    Registry registry;

    public OverlayCreator(Registry registry, int numberOfConnections){
        this.numberOfConnections = numberOfConnections;
        this.registry = registry;
        constructOverlay();
    }   

    public void constructOverlay(){
        VertexList registeredList = registry.getRegistry();

        for(Vertex vertex : registeredList.getValues()){
            vertex.printVertex();
        }

    }



}
