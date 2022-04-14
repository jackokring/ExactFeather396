package uk.co.kring.ef396.data;

import uk.co.kring.ef396.data.backend.Restart;
import uk.co.kring.ef396.data.backend.Sais;

import java.io.*;

public class BWTStream {

    public static final int K1 = 1024;

    public static class Input extends FilterInputStream {

        private byte[] buffer = new byte[K1 * K1];
        private int count = -1;

        @Override
        public int read() throws IOException {
            if(count < 0) {
                DataInputStream dis = new DataInputStream(this.in);
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
                        super.read(U, start, L[i]);
                        start += L[i];//next section
                    }
                } else {
                    count = dis.readInt();
                    if(count > buffer.length) throw new IOException("Invalid length for block size");
                    p = dis.readInt();//index
                    if(p > count) throw new IOException("Invalid index for block size");
                    super.read(U);
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

        private byte[] buffer = new byte[K1 * K1];
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
                DataOutputStream dos = new DataOutputStream(this.out);
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
