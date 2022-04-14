package uk.co.kring.ef396.data;

import uk.co.kring.ef396.data.backend.Restart;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LZWStream {//not quite

    public static byte[] getBytes(String from) {
        byte[] b = new byte[from.length()];
        for (int i = 0; i < from.length(); i++) {
            b[i] = (byte) from.charAt(i);
        }
        return b;
    }

    public static class Input extends FilterInputStream implements Restart {

        private Map<Integer,String> dictionary = new HashMap<Integer,String>();
        private int dictSize = 0;
        private String entry, w;
        private Input24 dis;
        private byte[] ok;
        private int count;

        @Override
        public int read() throws IOException {
            if(ok == null) {
                int k = dis.read24();//inverse e.g. 256 - (256 - x) = x
                if (dictionary.containsKey(k))
                    entry = dictionary.get(k);
                else if (k == dictSize)
                    entry = w + w.charAt(0);
                else
                    throw new IllegalArgumentException("Bad compression dictionary index");
                ok = getBytes(entry);
                count = 0;
                // Add w+entry[0] to the dictionary.
                if(dictSize < (2 << 24))
                    dictionary.put(dictSize++, w + entry.charAt(0));
                w = entry;
            }
            int tmp = ok[count++];
            if(count == ok.length) {
                ok = null;
            }
            return tmp;
        }

        private void create() throws IOException {
            dis = new Input24(this.in);
            w = "";
            dictSize = 256;
            for (int i = 0; i < 256; i++)
                dictionary.put(i, "" + (char)i);
            int c = dis.read24();
            if(c >= dictSize) throw new IOException("Bad first index");
            String w = "" + (char)c;
            ok = getBytes(w);
        }

        public Input(InputStream in) throws IOException {
            super(in);
            create();
        }

        public static class Input24 extends DataInputStream {

            public Input24(InputStream in) {
                super(in);
            }

            public int read24() throws IOException {
                return (readByte() << 16) | readChar();
            }
        }

        @Override
        public void restart() throws IOException {
            create();
        }
    }

    public static class Output extends FilterOutputStream implements Restart {

        private Map<String,Integer> dictionary;
        private int dictSize;
        private String w;
        private Output24 dos;

        @Override
        public void write(int b) throws IOException {
            String wc = w + (char)b;
            if (dictionary.containsKey(wc))
                w = wc;
            else {
                dos.write24(dictionary.get(w));//inverse e.g. 256 - (256 - x) = x
                // Add wc to the dictionary.
                if(dictSize < (2 << 24))
                    dictionary.put(wc, dictSize++);
                w = "" + (char)b;
            }
        }

        public void flush() throws IOException {
            restart();
            super.flush();
        }

        public Output(OutputStream out) {
            super(out);
            create();
        }

        public static class Output24 extends DataOutputStream {

            public Output24(OutputStream out) {
                super(out);
            }

            public void write24(int x) throws IOException {
                writeByte((x >> 16) & 0xff);
                writeChar(x & 0xffff);
            }
        }

        private void create() {
            dos = new Output24(out);
            dictionary = new HashMap<>();
            dictSize = 256;
            w = "";
            for (int i = 0; i < 256; i++)
                dictionary.put("" + (char)i, i);
        }

        @Override
        public void restart() throws IOException {
            // Output the code for w.
            if (!w.equals(""))
                dos.writeInt(dictionary.get(w));
            create();
        }
    }
}
