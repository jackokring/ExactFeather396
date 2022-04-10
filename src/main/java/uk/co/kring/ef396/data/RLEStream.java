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
                    if(count == -1) throw new IOException("Malformed terminal EOF");
                }
            }
            int tmp = last;
            if(count == 0) {
                last = -1;
            }
            count--;
            return tmp;
        }

        protected Input(InputStream in) {
            super(in);
        }
    }

    public static class Output extends FilterOutputStream {

        private int count = 0;
        private int last = -1;

        @Override
        public void write(int b) throws IOException {
            if(last == b) {
                if(count == 255) {
                    last = -1;
                    count = -1;
                    super.write(count);
                }
                count++;
            } else {
                if(last != -1) {
                    super.write(count);
                }
                last = b;
                count = 0;
                super.write(b);
            }
        }

        public Output(OutputStream out) {
            super(out);
        }
    }
}
