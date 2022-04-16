package uk.co.kring.ef396.data;

import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class SignedStream {

    private static final File pri = new File("~/.config/data/dsa");
    private static final File pub = new File("~/.config/data/dsa.pub");

    public static byte[] readConfig(File file) throws IOException {
        return FilePipe.getInputStream(file).readAllBytes();
    }

    private static void writeConfig(byte[] key, File file) throws IOException {
        FilePipe.getOutputStream(file).write(key);
    }

    public static PublicKey pubKey(File file) throws NoSuchAlgorithmException,
            InvalidKeySpecException, IOException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(readConfig(file));
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PublicKey pubKey(byte[] key) throws NoSuchAlgorithmException,
            InvalidKeySpecException, IOException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePublic(keySpec);
    }

    private static PrivateKey priKey(File file) throws NoSuchAlgorithmException,
            InvalidKeySpecException, IOException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(readConfig(file));
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static void pubKey(File file, PublicKey input) throws IOException {
        writeConfig(input.getEncoded(), file);
    }

    public static void priKey(File file, PrivateKey input) throws IOException {
        writeConfig(input.getEncoded(), file);
    }

    public static class Input extends FilterInputStream {

        private PublicKey puk;
        private MessageDigest md;
        private int count = 0;
        private byte[] buffer = new byte[1024];

        @Override
        public int read() throws IOException {
            if(count == 0) {
                readBuffer();
            }
            count--;
            return buffer[buffer.length - count - 1];
        }

        private void readCheck(byte[] buffer, int start, int length) throws IOException {
            int at = 0;
            int checked = 0;
            if(length < 0 || start < 0) throw new IOException("Negative size specification");
            while(length - at != (at = super.read(buffer, start + at, length - at))) {
                if (checked == 5) throw new IOException("Read stream unavailable amount");
                checked++;
            }
        }

        public void readBuffer() throws IOException {
            count = new DataInputStream(this.in).readInt();
            if(count < 0 || count > buffer.length) throw new IOException("Bad length EOF");
            readCheck(buffer, 0, count);
            md.update(buffer, 0, count);//check
            byte[] d = md.digest();
            byte[] e = readLenBytes(this.in);
            if(!Arrays.equals(d, e)) {
                throw new IOException("Checksum error");
            }
            try {
                byte[] s = readLenBytes(this.in);
                if(!verify(s, puk)) throw new IOException();
            } catch(Exception f) {
                throw new IOException("Signature verification error");
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
    }

    public static class Output extends FilterOutputStream {

        private PrivateKey pk;
        private MessageDigest md;
        private int count = 0;
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
                md.update(buffer, 0, count);
                count = 0;
                byte[] d = md.digest();
                writeLenBytes(this.out, d);
                try {
                    d = sign(d, pk);
                } catch(Exception e) {
                    throw new IOException("Signing error");
                }
                writeLenBytes(this.out, d);
            }
        }

        public void flush() throws IOException {
            flusher(true);
            super.flush();
        }

        public Output(OutputStream out) throws IOException {
            super(out);
            PublicKey puk;
            try {
                md = MessageDigest.getInstance("SHA-256");
                pk = priKey(pri);
                puk = pubKey(pub);
            } catch(Exception e) {
                try {
                    KeyPair kp = getKeys();
                    priKey(pri, pk = kp.getPrivate());//save
                    pubKey(pub, puk = kp.getPublic());//save
                } catch(Exception f) {
                    throw new IOException("Key generation error");
                }
            }
            byte[] pubBytes = puk.getEncoded();
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
