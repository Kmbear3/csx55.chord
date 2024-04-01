package csx55.chord.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import csx55.chord.util.StatusCodes;

public class DeregisterResponse implements Event, Protocol{
    String additionalInfo;
    int statusCode;

    public DeregisterResponse(int statusCode, String additionalInfo) {
        this.statusCode = statusCode;
        this.additionalInfo = additionalInfo;
    }

    public DeregisterResponse(byte[] marshalledBytes) {
        try {
            ByteArrayInputStream baInputStream =  new ByteArrayInputStream(marshalledBytes);
            DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

            int type = din.readInt();

            if(type != Protocol.DEREGISTER_RESPONSE){
                System.err.println("Type mismatch in DeregisterResponse!");
            }

            this.statusCode = din.readInt();
        
            int infoLength = din.readInt();
            byte[] infoBytes = new byte[infoLength];
            din.readFully(infoBytes);
            this.additionalInfo = new String(infoBytes);

            baInputStream.close();
            din.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean exitOverlay() {
        return statusCode == StatusCodes.EXIT_OVERLAY;
    }

    public boolean deregisterStatus() {
        return statusCode == StatusCodes.DEREGISTER;
    }

    @Override
    public int getType() {
        return Protocol.DEREGISTER_RESPONSE;
    }

    @Override
    public byte[] getBytes() throws IOException {
        byte[] marshalledBytes = null;
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(Protocol.DEREGISTER_RESPONSE);
        dout.writeInt(this.statusCode);

        byte[] additionalInfoBytes = this.additionalInfo.getBytes();
        int elementLength = additionalInfoBytes.length;
        dout.writeInt(elementLength);
        dout.write(additionalInfoBytes);
        dout.flush();

        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();
        
        return marshalledBytes;
    }

    public String getAdditionalInfo() {
        return this.additionalInfo;
    }
}
