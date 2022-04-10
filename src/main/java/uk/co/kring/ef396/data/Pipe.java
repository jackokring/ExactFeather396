package uk.co.kring.ef396.data;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public enum Pipe {

    GZIP(false,false, false, true),//generic deflate algorithm
    RLE(false,false, true, false),//run length encoding
    RLE_GZIP(false,false, true, true),//long run compression before deflate (highly redundant data?)
    //although almost pointless unless the RLE is still not the most compressed the repeats can get
    //GZIP includes some LZ77 so the repeats would be compacted quite a bit anyhow.
    BWT_GZIP(false,true, false, true),
    // TODO - a LZW with dictionary resets and LRU entry MTF (use reset() for dict reset)
    // technically this is slow but does have full size in the 1MB block for dictionary per prefix
    // and the MTF on the dictionary indexes keeps them as low as possible so GZIP cleans up.
    LZW(true,false, false, false),
    BWT_LZW_GZIP(true,true, false, true),
    NULL(false,false, false, false);//straight data pipe

    private final boolean gzip;
    private final boolean rle;
    private final boolean bwt;
    private final boolean lzw;

    Pipe(boolean lzw, boolean bwt, boolean rle, boolean gzip) {
        this.lzw = lzw;
        this.bwt = bwt;
        this.rle = rle;
        this.gzip = gzip;
    }

    public DataInputStream getStream(InputStream is) throws IOException {
        if(gzip) is = new GZIPInputStream(is);
        if(rle) is = new RLEStream.Input(is);
        if(lzw) is = new LZWStream.Input(is);
        if(bwt) is = new BWTStream.Input(is);
        return new DataInputStream(is);
    }

    public DataOutputStream getStream(OutputStream os) throws IOException {
        if(gzip) os = new GZIPOutputStream(os);
        if(rle) os = new RLEStream.Output(os);
        if(lzw) os = new LZWStream.Output(os);
        if(bwt) os = new BWTStream.Output(os);
        return new DataOutputStream(os);
    }
}
