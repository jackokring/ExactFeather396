package uk.co.kring.ef396.data;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public enum Pipe {

    GZIP(false, true),//generic deflate algorithm
    RLE(true, false),//run length encoding
    RLE_GZIP(true, true),//long run compression before deflate (highly redundant data?)
    // TODO
    NULL(false, false);//straight data pipe

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
