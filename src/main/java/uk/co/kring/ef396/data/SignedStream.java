package uk.co.kring.ef396.data;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SignedStream {


    //================================= PUBLIC INTERFACE
    public static KeyPair getKeys() throws NoSuchAlgorithmException {
        // Generate a 1024-bit
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("DSA");
        keyGenerator.initialize(1024);
        return keyGenerator.genKeyPair();
    }

    public static byte[] getDigest(byte[] msg) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256").digest(msg);
    }

    public static PublicKey pubKey(byte[] input) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(input);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey priKey(byte[] input) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(input);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static byte[] pubKey(PublicKey input) {
        return input.getEncoded();
    }

    public static byte[] priKey(PrivateKey input) {
        return input.getEncoded();
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
