package csx55.overlay.util;

public interface StatusCodes {
    byte SUCCESS = 0;
    byte FAILURE = 1; 
    byte FAILURE_IP = 2;

    // 1: Node already registered in overlay
    // 2: IP mismatch
}
