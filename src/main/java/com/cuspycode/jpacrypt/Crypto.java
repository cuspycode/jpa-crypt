package com.cuspycode.jpacrypt;

import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class Crypto {
    private SecureRandom rng;
    private byte[] iv;

    public Crypto() {
        this.rng = new SecureRandom();
        this.iv = new byte[12];
    }

    public byte[] getIV() { return iv; }

    private Cipher setup(int mode, byte[] iv, String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes("UTF-8"));
        byte[] sha256 = md.digest();
        SecretKey key = new SecretKeySpec(Arrays.copyOf(sha256, 128/8), "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
	if (mode == Cipher.ENCRYPT_MODE) {
	    rng.nextBytes(iv);			// Do this again for each new encryption
	}
        GCMParameterSpec spec = new GCMParameterSpec(16*8, iv);
        cipher.init(mode, key, spec);
        return cipher;
    }

    public byte[] encrypt(byte[] plaintext, String password) throws Exception {
        return setup(Cipher.ENCRYPT_MODE, this.iv, password).doFinal(plaintext);
    }

    public byte[] decrypt(byte[] cryptext, String password, byte[] iv) throws Exception {
        return setup(Cipher.DECRYPT_MODE, iv, password).doFinal(cryptext);
    }

    public static String encrypt(String plaintext, String password) throws Exception {
	Crypto c = new Crypto();
	byte[] cryptext = c.encrypt(plaintext.getBytes("UTF-8"), password);
	return (Base64.getEncoder().encodeToString(c.getIV()) +
		":" +
		Base64.getEncoder().encodeToString(cryptext));
    }

    public static String decrypt(String cryptext, String password) throws Exception {
	String[] parts = cryptext.split(":");
	byte[] iv = Base64.getDecoder().decode(parts[0]);
	byte[] data = Base64.getDecoder().decode(parts[1]);
	return new String(new Crypto().decrypt(data, password, iv), "UTF-8");
    }
}

