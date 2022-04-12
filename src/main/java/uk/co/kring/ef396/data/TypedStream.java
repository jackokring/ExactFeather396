package uk.co.kring.ef396.data;

import java.io.*;

public class TypedStream {


    public static class Input extends FilterInputStream {

        private final FilePipe fp;

        protected Input(InputStream in, FilePipe fp) {
            super(in);
            this.fp = fp;
        }

        public final FilePipe getFilePipe() {
            return fp;
        }
    }

    public static class Output extends FilterOutputStream {

        private final FilePipe fp;

        public Output(OutputStream out, FilePipe fp) {
            super(out);
            this.fp = fp;
        }

        public final FilePipe getFilePipe() {
            return fp;
        }
    }
}
