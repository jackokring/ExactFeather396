package uk.co.kring.ef396.data.streams;

import java.io.*;

public class LocalDataStream {

    public static class Input extends DataInputStream {

        @Override
        public void close() throws IOException {
            //nothing
        }

        public void closeActual() throws IOException {
            in.close();
        }

        public Input(InputStream in) {
            super(in);
        }
    }

    public static class Output extends DataOutputStream {

        @Override
        public void close() throws IOException {
            //nothing
        }

        public void closeActual() throws IOException {
            out.close();
        }

        public Output(OutputStream out) {
            super(out);
        }
    }
}
