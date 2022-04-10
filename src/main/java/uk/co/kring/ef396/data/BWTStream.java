package uk.co.kring.ef396.data;

import java.io.*;

public class BWTStream {

    public static final int K1 = 1024;

    public static class Input extends FilterInputStream {

        private byte[] buffer = new byte[K1 * K1];
        private int count = -1;

        @Override
        public int read() throws IOException {
            if(count < 0) {
                DataInputStream dis = new DataInputStream(this);
                count = dis.readInt();
                int p = dis.readInt();//index
                byte[] U = new byte[buffer.length];
                int[] A = new int[buffer.length];
                super.read(U);
                Sais.unbwt(U, buffer, A, count, p);//un-transform
            }
            return buffer[buffer.length - (count--) - 1];//zero 1st
        }

        protected Input(InputStream in) {
            super(in);
        }
    }

    public static class Output extends FilterOutputStream {

        private byte[] buffer = new byte[K1 * K1];
        private int count = 0;

        @Override
        public void write(int b) throws IOException {
            if(count == buffer.length) {
                flush();
            }
            buffer[count] = (byte)b;
            count++;
        }

        public void flush() throws IOException {
            if(count > 0) {
                DataOutputStream dos = new DataOutputStream(this);
                dos.writeInt(count);
                byte[] U = new byte[buffer.length];
                int[] A = new int[buffer.length];
                int p = Sais.bwtransform(buffer, U, A, count);
                dos.writeInt(p);//index
                super.write(U);//transform
                count = 0;
                super.flush();
            }
        }

        public Output(OutputStream out) {
            super(out);
        }
    }
}
