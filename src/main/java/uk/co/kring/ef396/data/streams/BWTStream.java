package uk.co.kring.ef396.data.streams;

import uk.co.kring.ef396.data.backend.Restart;
import uk.co.kring.ef396.data.backend.Sais;

import java.io.*;

public class BWTStream {

    public static final int K1 = 1024 * 4;//=> 16M block size

    public static class Input extends FilterInputStream {

        private final byte[] buffer = new byte[K1 * K1];
        private int count = -1;

        private void readCheck(byte[] buffer, int start, int length) throws IOException {
            int at = 0;
            int checked = 0;
            if(length < 0 || start < 0) throw new IOException("Negative size specification");
            while(length - at != (at = in.read(buffer, start + at, length - at))) {
                if (checked == 5) throw new IOException("Read stream unavailable amount");
                checked++;
            }
        }

        @Override
        public int read() throws IOException {
            if(count < 0) {
                LocalDataStream.Input dis = new LocalDataStream.Input(this.in);
                int p;
                int[] L = new int[256];
                byte[] U = new byte[buffer.length];
                int[] A = new int[buffer.length];
                if(in instanceof Restart restart) {//special
                    restart.restart();
                    count = dis.readInt();
                    if(count > buffer.length) throw new IOException("Invalid length for block size");
                    p = dis.readInt();//index
                    if(p > count) throw new IOException("Invalid index for block size");
                    for(int i = 0; i < 256; i++) {
                        L[i] = dis.readInt();//24 bit?
                    }
                    int start = 0;
                    for(int i = 0; i < 256; i++) {
                        restart.restart();
                        readCheck(U, start, L[i]);
                        start += L[i];//next section
                    }
                } else {
                    count = dis.readInt();
                    if(count > buffer.length) throw new IOException("Invalid length for block size");
                    p = dis.readInt();//index
                    if(p > count) throw new IOException("Invalid index for block size");
                    readCheck(U, 0, count);
                }
                Sais.unbwt(U, buffer, A, count, p);//un-transform
            }
            count--;
            return buffer[buffer.length - count - 1];//zeroth 1st
        }

        public Input(InputStream in) {
            super(in);
        }
    }

    public static class Output extends FilterOutputStream {

        private final byte[] buffer = new byte[K1 * K1];
        private int count = 0;

        @Override
        public void write(int b) throws IOException {
            if(count == buffer.length) {
                flusher();
            }
            buffer[count] = (byte)b;
            count++;
        }

        public void flush() throws IOException {
            flusher();//avoid pass through issues of flush
            super.flush();
        }

        public void flusher() throws IOException {
            if(count > 0) {
                LocalDataStream.Output dos = new LocalDataStream.Output(this.out);
                int[] L = new int[256];
                byte[] U = new byte[buffer.length];
                int[] A = new int[buffer.length];
                int p = Sais.bwtransform(buffer, U, A, count);
                if(out instanceof Restart restart) {//special
                    restart.restart();
                    dos.writeInt(count);
                    dos.writeInt(p);//index
                    for(int i = 0; i < buffer.length; i++) {
                        L[U[i]]++;//count characters of type
                    }
                    for(int i = 0; i < 256; i++) {
                        dos.writeInt(L[i]);//24 bit?
                    }
                    int start = 0;
                    for(int i = 0; i < 256; i++) {
                        restart.restart();
                        super.write(U, start, L[i]);
                        start += L[i];//next section
                    }
                } else {
                    dos.writeInt(count);
                    dos.writeInt(p);//index
                    super.write(U);//transform
                }
                count = 0;
            }
        }

        public Output(OutputStream out) {
            super(out);
        }
    }
}
