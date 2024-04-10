package csx55.chord.wireformats;

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
    public static final int DEREGISTER_REQUEST = 12;
    public static final int DEREGISTER_RESPONSE = 13;
    public static final int NODE_TASKS = 14;
    public static final int TASKS = 15;
    public static final int ROUND_INCREMENT = 16;
    public static final int INSERT_REQUEST = 17;
    public static final int INSERT_RESPONSE = 18;
    public static final int NEW_SUCCESSOR = 19;
    public static final int SUCCESSOR_REQUEST = 20;
    public static final int SUCCESSOR_RESPONSE = 21;
    public static final int NEW_ADDITION = 22;
    public static final int FORWARD_FILE = 23;
    public static final int DOWNLOAD_REQUEST = 24;
    public static final int DOWNLOAD_RESPONSE = 25;
    public static final int MIGRATE_FILE = 26;
    public static final int EXIT_NOTIFICATION = 27;
    public static final int NOTIFY_SUCCESSOR = 28;


}
