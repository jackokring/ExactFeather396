package uk.co.kring.ef396.data.streams;

import uk.co.kring.ef396.data.Data;
import uk.co.kring.ef396.data.backend.Secure;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class SignedStream {

    private static final File pri = new File("~/.config/" + Data.name + "/dsa");
    private static final File pub = new File("~/.config/" + Data.name + "/dsa.pub");
    public static final File git = new File("~/.config/" + Data.name + "/repo");

    public static boolean checkGit() {
        File gdot = new File(git, ".git");
        return git.exists() && git.isDirectory()
                && gdot.exists() && gdot.isDirectory();
    }

    public static void doGit(PublicKey pk) {
        if(checkGit()) {
            byte[] bytes = pubKey(pk);
            //git actions
            //TODO
        }
    }

    public static byte[] readConfig(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        byte[] bytes = is.readAllBytes();
        is.close();
        return bytes;
    }

    private static void writeConfig(byte[] key, File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        os.write(key);
        os.close();
    }

    public static PublicKey pubKey(File file) throws NoSuchAlgorithmException,
            InvalidKeySpecException, IOException {
        //load git check
        PublicKey pk = pubKey(readConfig(file));
        doGit(pk);
        return pk;
    }

    public static PublicKey pubKey(byte[] key) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        //file git check
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        PublicKey pk = keyFactory.generatePublic(keySpec);
        doGit(pk);
        return pk;
    }

    private static PrivateKey priKey() throws NoSuchAlgorithmException,
            InvalidKeySpecException, IOException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(readConfig(pri));
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static void pubKey(File file, PublicKey input) throws IOException {
        //save git check
        doGit(input);
        writeConfig(input.getEncoded(), file);
    }

    public static byte[] pubKey(PublicKey input) {
        return input.getEncoded();
    }

    public static void priKey(PrivateKey input) throws IOException {
        writeConfig(input.getEncoded(), pri);
    }

    public static class Input extends FilterInputStream implements Secure {

        private final PublicKey puk;
        private final MessageDigest md;
        private int count;
        private byte[] d;
        private boolean evade = true;
        private final byte[] buffer = new byte[1024];

        @Override
        public int read() throws IOException {
            if(count == 0) {
                readBuffer();
            }
            count--;
            return buffer[buffer.length - count - 1];
        }

        private void readCheck(byte[] buffer, int length) throws IOException {
            int checked = 0;
            if(length < 0) throw new IOException("Negative size specification");
            int at = 0;
            while(length - at != (at = super.read(buffer, at, length - at))) {
                if (checked == 5) throw new IOException("Read stream unavailable amount");
                checked++;
            }
        }

        public void readBuffer() throws IOException {
            count = new DataInputStream(this.in).readInt();
            if(count < 0 || count > buffer.length) throw new IOException("Bad length EOF");
            readCheck(buffer, count);
            if(d != null) md.update(d, 0, d.length);//chain
            md.update(buffer, 0, count);//check
            d = md.digest();
            byte[] e = readLenBytes(this.in);
            if(!Arrays.equals(d, e)) {
                throw new IOException("Checksum error");
            }
            int i = in.read();
            if(i != 0) {//zero is baulk no sign block
                evade = false;
                try {
                    byte[] s = readLenBytes(this.in);
                    if (!verify(s, puk)) throw new IOException();
                } catch (Exception f) {
                    throw new IOException("Signature verification error");
                }
                // higher packet versions here?
            } else {
                // packet format middles?
                evade = true;//prevent end not signed
            }
        }

        public Input(InputStream in) throws IOException {
            super(in);
            try {
                md = MessageDigest.getInstance("SHA-256");
                puk = pubKey(readLenBytes(this.in));
            } catch(Exception e) {
                throw new IOException("Bad public key in stream");
            }
            count = 0;
        }

        @Override
        public final boolean checkOK() {
            return !evade;
        }

        @Override
        public final void close() throws IOException {
            boolean exit = checkOK();
            try {
                in.close();
            } catch(IOException e) {
                exit = false;
            }
            if(!exit) throw new IOException(
                    new SecurityException("Possible evasion of signature on closure"));
        }
    }

    public static class Output extends FilterOutputStream {

        private PrivateKey pk;
        private MessageDigest md;
        private int count;
        private byte[] d;
        private final byte[] buffer = new byte[1024];

        @Override
        public void write(int b) throws IOException {
            buffer[count] = (byte)b;
            md.update((byte)b);
            count++;
            flusher(false);
        }

        public void flusher(boolean end) throws IOException {
            if(count == buffer.length || end) {
                new DataOutputStream(this.out).writeInt(count);
                out.write(buffer, 0, count);
                if(d != null) md.update(d, 0, d.length);//chain
                md.update(buffer, 0, count);
                count = 0;
                d = md.digest();
                writeLenBytes(this.out, d);
                if(!end) {
                    // last sign only
                    out.write(0);
                } else {
                    out.write(1);
                    try {
                        d = sign(d, pk);
                    } catch (Exception e) {
                        throw new IOException("Signing error");
                    }
                    writeLenBytes(this.out, d);
                }
            }
        }

        @Override
        public void flush() throws IOException {
            flusher(true);
            super.flush();
        }

        @Override
        public void close() throws IOException {//likely is the super definition
            flush();
            out.close();
        }

        public Output(OutputStream out) throws IOException {
            super(out);
            PublicKey puk;
            try {
                md = MessageDigest.getInstance("SHA-256");
                pk = priKey();
                puk = pubKey(pub);
            } catch(Exception e) {
                try {
                    KeyPair kp = getKeys();
                    priKey(pk = kp.getPrivate());//save
                    pubKey(pub, puk = kp.getPublic());//save
                } catch(Exception f) {
                    throw new IOException("Key generation error");
                }
            }
            byte[] pubBytes = pubKey(puk);
            writeLenBytes(this.out, pubBytes);
            count = 0;
            //now ready for streaming
        }
    }

    public static void writeLenBytes(OutputStream out, byte[] b) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeInt(b.length);
        dos.write(b);
    }

    public static byte[] readLenBytes(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        int len = dis.readInt();
        byte[] b = new byte[len];
        if(dis.read(b) != len) throw new IOException("Bad length EOF");
        return b;
    }

    //================================= PUBLIC INTERFACE
    public static KeyPair getKeys() throws NoSuchAlgorithmException {
        // Generate a 1024-bit
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("DSA");
        keyGenerator.initialize(1024);
        return keyGenerator.genKeyPair();
    }

    public static byte[] sign(byte[] input, PrivateKey key) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        Signature s = Signature.getInstance("DSA");
        s.initSign(key);
        s.update(input);
        return s.sign();
    }

    public static boolean verify(byte[] input, PublicKey key) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException {
        Signature s = Signature.getInstance("DSA");
        s.initVerify(key);
        return s.verify(input);
    }
}
