package uk.co.kring.ef396.data;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public enum Pipe {

    GZIP(false, false, true),//generic deflate algorithm
    RLE(false, true, false),//run length encoding
    RLE_GZIP(false, true, true),//long run compression before deflate (highly redundant data?)
    //although almost pointless unless the RLE is still not the most compressed the repeats can get
    //GZIP includes some LZ77 so the repeats would be compacted quite a bit anyhow.
    BWT_GZIP(true, false, true),
    // TODO - a LZW with dictionary resets and LRU entry MTF
    NULL(false, false, false);//straight data pipe

    private final boolean gzip;
    private final boolean rle;
    private final boolean bwt;

    Pipe(boolean bwt, boolean rle, boolean gzip) {
        this.bwt = bwt;
        this.rle = rle;
        this.gzip = gzip;
    }

    public DataInputStream getStream(InputStream is) throws IOException {
        if(gzip) is = new GZIPInputStream(is);
        if(rle) is = new RLEStream.Input(is);
        if(bwt) is = new BWTStream.Input(is);
        return new DataInputStream(is);
    }

    public DataOutputStream getStream(OutputStream os) throws IOException {
        if(gzip) os = new GZIPOutputStream(os);
        if(rle) os = new RLEStream.Output(os);
        if(bwt) os = new BWTStream.Output(os);
        return new DataOutputStream(os);
    }
}
