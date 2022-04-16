package uk.co.kring.ef396.data;

import java.io.*;

public class ZLEStream {


    public static class Input extends FilterInputStream {

        private int count = 0;
        private int last = -1;

        @Override
        public int read() throws IOException {
            if(last == -1) {
                last = in.read();
                if(last == 0) {
                    count = in.read();
                    if(count == -1) throw new IOException("Malformed count EOF");
                } else if(last == -1) {
                    throw new IOException("Malformed byte EOF");
                }
            }
            int tmp = last;
            if(last != 0) {
                last = -1;//reset
            } else {
                count--;
                if (count < 0) {//all decremented off
                    last = -1;
                }
            }
            return tmp;
        }

        public Input(InputStream in) {
            super(in);
        }
    }

    public static class Output extends FilterOutputStream {

        private int count = 0;
        private boolean last = false;

        @Override
        public void write(int b) throws IOException {
            if(b == 0) {
                if(!last) {
                    out.write(0);//zero RLE
                    last = true;
                    count = 0;//first
                } else {//in zero stream
                    count++;
                    if (count == 255) {
                        out.write(count);
                        count = 0;
                        out.write(0);//new run
                    }
                }
            } else {
                if(last) out.write(count);//finish count
                out.write(b);
                last = false;//reset
            }
        }

        public Output(OutputStream out) {
            super(out);
        }
    }
}
