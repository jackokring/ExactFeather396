package uk.co.kring.ef396.data;

import uk.co.kring.ef396.data.backend.Restart;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZWStream {

    private Map<String,Integer> dictionary = new HashMap<String,Integer>();
    private Map<Integer,String> dictionary2 = new HashMap<Integer,String>();
    private int dictSize = 0;

    public void reset() {
        dictSize = 256;
        for (int i = 0; i < 256; i++)
            dictionary.put("" + (char)i, i);
    }

    /** Compress a string to a list of output symbols. */
    public List<Integer> compress(String uncompressed) {
        reset();
        String w = "";
        List<Integer> result = new ArrayList<Integer>();
        for (char c : uncompressed.toCharArray()) {
            String wc = w + c;
            if (dictionary.containsKey(wc))
                w = wc;
            else {
                result.add(dictionary.get(w));
                // Add wc to the dictionary.
                dictionary.put(wc, dictSize++);
                w = "" + c;
            }
        }

        // Output the code for w.
        if (!w.equals(""))
            result.add(dictionary.get(w));
        return result;
    }

    /** Decompress a list of output ks to a string. */
    public String decompress(List<Integer> compressed) {
        reset();
        String w = "" + (char)(int)compressed.remove(0);
        StringBuffer result = new StringBuffer(w);
        for (int k : compressed) {
            String entry;
            if (dictionary2.containsKey(k))
                entry = dictionary2.get(k);
            else if (k == dictSize)
                entry = w + w.charAt(0);
            else
                throw new IllegalArgumentException("Bad compressed k: " + k);

            result.append(entry);

            // Add w+entry[0] to the dictionary.
            dictionary2.put(dictSize++, w + entry.charAt(0));

            w = entry;
        }
        return result.toString();
    }

    public static class Input extends FilterInputStream implements Restart {

        @Override
        public int read() throws IOException {
            return 0;//TODO
        }

        public Input(InputStream in) {
            super(in);
        }

        @Override
        public void restart() {

        }
    }

    public static class Output extends FilterOutputStream implements Restart {

        @Override
        public void write(int b) throws IOException {
            //TODO
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

        @Override
        public void restart() {

        }
    }
}
