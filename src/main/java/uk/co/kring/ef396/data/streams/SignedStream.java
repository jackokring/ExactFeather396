package uk.co.kring.ef396.data.streams;

import uk.co.kring.ef396.data.Data;

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

    public static class Input extends FilterInputStream {

        private final PublicKey puk, me2;
        private final PrivateKey me;
        private final MessageDigest md;

        @Override
        public int read() throws IOException {
            int b = in.read();
            md.update((byte)b);
            return b;
        }

        public Input(InputStream in) throws IOException {
            super(in);
            try {
                md = MessageDigest.getInstance("SHA-256");
                puk = pubKey(readLenBytes(this.in));
                me = priKey();
                me2 = pubKey(pub);
            } catch(Exception e) {
                throw new IOException("Bad public key in stream");
            }
        }

        @Override
        public final void close() throws IOException {
            byte[] hash = readLenBytes(in);
            byte[] sign = readLenBytes(in);
            byte[] dig = md.digest();
            try {
                if (verify(sign, puk)) {
                    boolean match = true;
                    if(!Arrays.equals(hash, dig)) throw new IOException("Invalid hash");
                    if(puk.equals(me2)) {
                       //extra checks for self
                       byte[] b = sign(dig, me);
                       if(!Arrays.equals(b, sign)) throw new IOException("Signature cloned integrity problem");
                       //ok it's me almost for sure
                    }
                }
            } catch(Exception e) {
                throw new IOException("Signature verification problem");
            }
        }
    }

    public static class Output extends FilterOutputStream {

        private PrivateKey pk;
        private MessageDigest md;

        @Override
        public void write(int b) throws IOException {
            md.update((byte)b);
        }

        @Override
        public void close() throws IOException {//likely is the super definition
            byte[] b = md.digest();
            try {
                writeLenBytes(out, b);
                writeLenBytes(out, sign(b, pk));
            } catch (Exception e) {
                throw new IOException("Signing exception");
            }
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
            //now ready for streaming
        }
    }

    public static void writeLenBytes(OutputStream out, byte[] b) throws IOException {
        LocalDataStream.Output dos = new LocalDataStream.Output(out);
        dos.writeInt(b.length);
        dos.write(b);
    }

    public static byte[] readLenBytes(InputStream in) throws IOException {
        LocalDataStream.Input dis = new LocalDataStream.Input(in);
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
