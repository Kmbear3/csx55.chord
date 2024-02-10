package csx55.overlay.wireformats;

public interface Protocol {
    // Wireformats:

    public static final int MESSAGE = 1;
    public static final int REGISTER_REQUEST = 2;
    public static final int REGISTER_RESPONSE = 3;
    public static final int MESSAGING_NODES_LIST = 4;
    public static final int INITIATE_PEER_CONNECTION = 5;
    public static final int TASK_INITIATE = 6;
    public static final int POKE = 7;
    public static final int Link_Weights = 8;
    public static final int PULL_TRAFFIC_SUMMARY = 9;
    public static final int TASK_COMPLETE = 10;
    public static final int TRAFFIC_SUMMARY = 11;
}
