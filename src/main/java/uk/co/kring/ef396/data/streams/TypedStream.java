package uk.co.kring.ef396.data.streams;

import uk.co.kring.ef396.data.FilePipe;

import java.io.*;

public class TypedStream {


    public static class Input extends DataInputStream {

        private final FilePipe fp;
        private final FilePipe.Task task;
        private boolean streamed = false;

        public void setStreamed() {
            streamed = true;
        }

        public boolean isStreamed() {
            return streamed;
        }

        public Input(InputStream in, FilePipe fp, FilePipe.Task task) {
            super(in);
            this.fp = fp;
            this.task = task;
        }

        public final FilePipe getFilePipe() {
            return fp;
        }

        public final FilePipe.Task getTask() {
            return task;
        }

        @Override
        public int read() throws IOException {
            if(getTask() != null && getTask().getError() != null) {
                throw getTask().getError();
            }
            return in.read();
        }
    }

    public static class Output extends DataOutputStream {

        private final FilePipe fp;
        private final FilePipe.Task task;
        private boolean streamed = false;

        public void setStreamed() {
            streamed = true;
        }

        public boolean isStreamed() {
            return streamed;
        }

        public Output(OutputStream out, FilePipe fp, FilePipe.Task task) {
            super(out);
            this.fp = fp;
            this.task = task;
        }

        public final FilePipe getFilePipe() {
            return fp;
        }

        public final FilePipe.Task getTask() {
            return task;
        }

        @Override
        public void write(int b) throws IOException {
            if(getTask() != null && getTask().getError() != null) {
                throw getTask().getError();
            }
            out.write(b);
        }
    }
}
