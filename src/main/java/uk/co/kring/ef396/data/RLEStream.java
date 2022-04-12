package uk.co.kring.ef396.data;

import java.io.*;

public class RLEStream {

    public static class Input extends FilterInputStream {

        private int count = 0;
        private int last = -1;

        @Override
        public int read() throws IOException {
            if(last == -1) {
                last = super.read();
                if(last != -1) {
                    count = super.read();
                    if(count == -1) throw new IOException("Malformed count EOF");
                } else {
                    throw new IOException("Malformed byte EOF");
                }
            }
            int tmp = last;
            count--;
            if(count < 0) {//all decremented off
                last = -1;
            }
            return tmp;
        }

        public Input(InputStream in) {
            super(in);
        }
    }

    public static class Output extends FilterOutputStream {

        private int count = 0;
        private int last = -1;

        @Override
        public void write(int b) throws IOException {
            if(last == b) {//same
                count++;
                if(count == 255) {//overflow?
                    super.write(count);//complete count
                    super.write(b);//new set
                    count = 0;//for increment to zero
                }
            } else {
                if(last != -1) {
                    super.write(count);//zero basis last count
                }
                last = b;//new char
                count = 0;//one found so zero count
                super.write(b);//write it
            }
        }

        public Output(OutputStream out) {
            super(out);
        }
    }
}
