package uk.co.kring.ef396.data.streams;

import uk.co.kring.ef396.data.FilePipe;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ClooneyStream {

    //so it seems the kernel swallows the input stream behind a protection buffer

    public static class Input extends DataInputStream {

        private boolean thru = false;
        private BufferedInputStream input;
        private PipedInputStream thruStream;
        private PipedOutputStream pos;

        @Override
        public int read() throws IOException {
            int r = -1;
            while(true) {
                in.mark(16);
                r = in.read();
                in.reset();
                switch (r) {
                    case 0xc0 -> {
                        thru = false;
                    }
                    case 0xc1 -> {
                        thru = true;
                    }
                    case -1 -> {
                        if(thru) {
                            pos.close();//write pipe
                        } else {
                            return -1;
                        }
                    }
                    default -> {
                        //make from UTF
                        int e = expect(r);
                        if(e == 2) {
                            r &= 0x1f;
                            r <<= 6;
                            r += (in.read() & 0x2f);
                        }
                        if(e == 3) {
                            r &= 0x0f;
                            r <<= 6;
                            r += (in.read() & 0x2f);
                            r <<= 6;
                            r += (in.read() & 0x2f);
                        }
                        if(thru) {
                            pos.write(r);//write pipe
                        } else {
                            return r;
                        }
                    }
                }
            }
        }

        private int expect(int b) throws IOException {
            if(b < 0x80) return 1;
            if(b < 0xe0) return 2;
            if(b < 0xf0) return 3;
            throw new IOException("Bad UTF coding");
        }

        public InputStream getThru() {
            return thruStream;//must use different thread to access
        }

        @Override
        public void close() {

        }

        public Input(InputStream in) throws IOException {
            super(in);
            input = new BufferedInputStream(in);
            pos = new PipedOutputStream();
            thruStream = new PipedInputStream(pos);
        }
    }

    public static class Output extends DataOutputStream {
        private InputStream input;

        @Override
        public void write(int b) throws IOException {
            char c = (char)b;
            byte[] bytes = (c+"").getBytes(StandardCharsets.UTF_8);
            out.write(bytes);
        }

        public void setThru(InputStream in) {
            input = in;
        }

        @Override
        public void close() throws IOException {
            out.write(0xc1);
            if(input != null) {
                FilePipe.cloneStream(input, this, true).rejoin().close();
            }
        }

        public Output(OutputStream out) throws IOException {
            super(out);
            out.write(0xc0);//set normal
        }
    }
}
