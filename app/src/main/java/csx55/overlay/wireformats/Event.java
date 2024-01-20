package csx55.overlay.wireformats;

import java.io.IOException;

public interface Event {
    //  [This is an interface with the getType() and getBytes() defined]
    public String getType();
    public byte[] getBytes() throws IOException;
}
