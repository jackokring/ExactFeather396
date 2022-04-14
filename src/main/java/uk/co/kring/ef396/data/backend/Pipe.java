package uk.co.kring.ef396.data.backend;

import uk.co.kring.ef396.data.*;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public enum Pipe {

    GZIP(false,false, Runs.NULL, true, false),//generic deflate algorithm
    RLE(false,false, Runs.RLE, false, false),//run length encoding
    ZLE_GZIP(false,false, Runs.ZLE, true, false),//long run compression
    // before deflate (highly redundant data?)
    BWT_GZIP(false,true, Runs.NULL, true, false),//good
    LZW(true,false, Runs.NULL, false, false),//24-bit dictionary indexing
    // technically this is slow but does have full size in the 1MB block for dictionary per prefix
    // and the dictionary indexes keeps them as low as possible so GZIP cleans up.
    BWT_LZW_GZIP(true,true, Runs.NULL, true, true),
    SIGN(false, false, Runs.NULL, false, true),
    NULL(false,false, Runs.NULL, false, false);//straight data pipe

    private final boolean check;
    private final boolean gzip;
    private final Runs runs;
    private final boolean bwt;
    private final boolean lzw;

    public enum Runs {
        RLE(), ZLE(), NULL();
    }

    Pipe(boolean lzw, boolean bwt, Runs runs, boolean gzip, boolean check) {
        this.lzw = lzw;
        this.bwt = bwt;
        this.runs = runs;
        this.gzip = gzip;
        this.check = check;
    }

    public DataInputStream getStream(InputStream is) throws IOException {
        if(check) is = new SignedStream.Input(is);
        if(gzip) is = new GZIPInputStream(is, 65536);
        switch(runs) {
            case RLE -> is = new RLEStream.Input(is);
            case ZLE -> is = new ZLEStream.Input(is);
        }
        if(lzw) is = new LZWStream.Input(is);
        if(bwt) is = new BWTStream.Input(is);
        return new DataInputStream(is);
    }

    public DataOutputStream getStream(OutputStream os) throws IOException {
        if(check) os = new SignedStream.Output(os);
        if(gzip) os = new GZIPOutputStream(os, 65536, true);// SYNC_FLUSH
        switch(runs) {
            case RLE ->  os = new RLEStream.Output(os);
            case ZLE ->  os = new ZLEStream.Output(os);
        }
        if(lzw) os = new LZWStream.Output(os);
        if(bwt) os = new BWTStream.Output(os);
        return new DataOutputStream(os);
    }
}
