package likelion.mini.team1.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;

public class AESUtil {

	private final static String key = "0123456789abcdef";

	public static String encrypt(String data){
		try {
			Cipher cipher = Cipher.getInstance("AES");
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(data.getBytes());
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String decrypt(String encrypted) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
			return new String(original);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
