package uk.co.kring.ef396.data;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public enum Pipe {

    GZIP(false, true),
    RLE(true, false);
    // TODO

    private final boolean gzip;
    private final boolean rle;

    Pipe(boolean rle, boolean gzip) {
        this.rle = rle;
        this.gzip = gzip;
    }

    public DataInputStream getStream(InputStream is) throws IOException {
        if(gzip) is = new GZIPInputStream(is);
        if(rle) is = new RLEStream.Input(is);
        return new DataInputStream(is);
    }

    public DataOutputStream getStream(OutputStream os) throws IOException {
        if(rle) os = new RLEStream.Output(os);
        if(gzip) os = new GZIPOutputStream(os);
        return new DataOutputStream(os);
    }
}
