package csx55.overlay.wireformats;

import java.io.IOException;

public interface Event {
    //  [This is an interface with the getType() and getBytes() defined]
    public int getType();
    public byte[] getBytes() throws IOException;
}
