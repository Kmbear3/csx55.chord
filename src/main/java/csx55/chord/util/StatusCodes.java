package csx55.chord.util;

public interface StatusCodes {
    byte SUCCESS = 0;
    byte FAILURE = 1; 
    byte FAILURE_IP = 2;

    // 1: Node already registered in overlay
    // 2: IP mismatch

    public static final int DEREGISTER = 3; // via cli custom input that just deregisters without process termination 
    public static final int EXIT_OVERLAY = 4; // via cli input that causes process termination 
}
