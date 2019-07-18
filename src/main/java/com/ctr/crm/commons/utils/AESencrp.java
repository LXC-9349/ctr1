package com.ctr.crm.commons.utils;

import java.net.URLEncoder;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 实现AES加密解密 cg882 2016-11-16
 */
public class AESencrp {

	// 加密算法
	private static String ALGO = "AES";
	private static String ALGO_MODE = "AES/CBC/NoPadding";
	private static String akey = "yunhu!@#6crm.com";
	private static String aiv = "yunhu!@#6crm.com";

	/**
	 * 用来进行加密的操作
	 * 
	 * @param Data
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String Data) throws Exception {
		if(Data == null) return null;
		try {
			Cipher cipher = Cipher.getInstance(ALGO_MODE);
			int blockSize = cipher.getBlockSize();
			byte[] dataBytes = Data.getBytes();
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength
						+ (blockSize - (plaintextLength % blockSize));
			}
			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

			SecretKeySpec keyspec = new SecretKeySpec(akey.getBytes("utf-8"),
					ALGO);
			IvParameterSpec ivspec = new IvParameterSpec(aiv.getBytes("utf-8"));
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(plaintext);
			String EncStr = new BASE64Encoder().encode(encrypted);
			return EncStr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 用来进行解密的操作
	 * 
	 * @param encryptedData
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String encryptedData) throws Exception {
		if(encryptedData == null) return null;
		try {
			byte[] encrypted1 = new BASE64Decoder()
					.decodeBuffer(encryptedData);

			Cipher cipher = Cipher.getInstance(ALGO_MODE);
			SecretKeySpec keyspec = new SecretKeySpec(akey.getBytes("utf-8"),
					ALGO);
			IvParameterSpec ivspec = new IvParameterSpec(aiv.getBytes("utf-8"));

			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String AES_Encrypt(String plainText) {
		if (StringUtils.isBlank(plainText))
			return null;
		byte[] encrypt = null;
		try {
			Key key = generateKey(akey);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			encrypt = cipher.doFinal(plainText.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(Base64.encodeBase64(encrypt));
	}

	public static String AES_Decrypt(String encryptData) {
		if (StringUtils.isBlank(encryptData))
			return null;
		byte[] decrypt = null;
		try {
			Key key = generateKey(akey);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			decrypt = cipher.doFinal(Base64.decodeBase64(encryptData.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(decrypt).trim();
	}

	private static Key generateKey(String key) throws Exception {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
			return keySpec;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public static void main(String[] args) throws Exception {
		// {"callmethod": 3, "answerTime": "2017-12-26 12:00:07", "crmid": "8888", "hangupDirection": 10014, "chengshudu": "0", "hangupCause": 10001, "memberid": "104576", "disNumber": "075561988526", "destNumber": "18872699215", "downloadIp": "192.168.1.81", "recordFilename": "20171226112545_18872699215_823.mp3", "startTime": "2017-12-26 12:00:00", "companycode": 10000005, "duration": 7, "billsec": 19, "endTime": "2017-12-26 12:00:26", "type": "callout", "id": 582, "extNumber": "823"}
		System.out.println("加密: " + URLEncoder.encode(encrypt("{\"callmethod\": 3, \"answerTime\": \"2017-12-26 12:00:07\", \"crmid\": \"8888\", \"hangupDirection\": 10014, \"chengshudu\": \"0\", \"hangupCause\": 10001, \"memberid\": \"104576\", \"disNumber\": \"075561988526\", \"destNumber\": \"18872699215\", \"downloadIp\": \"192.168.1.81\", \"recordFilename\": \"20171226112545_18872699215_823.mp3\", \"startTime\": \"2017-12-26 12:00:00\", \"companycode\": 10000005, \"duration\": 7, \"billsec\": 19, \"endTime\": \"2017-12-26 12:00:26\", \"type\": \"callout\", \"id\": 582, \"extNumber\": \"823\"}"),"utf-8"));
		System.out.println("解密: " + decrypt("0Mg5UpyU/uPL83MpcPSZLey8bU/uEb8Cd+uHks7/UfOsgSZAfTMSmcsW/PfNGvzzNcRm3Wh9GVBz\r\nMnMlWQtpaM8GLx43ovgVPIGAPg+bQlrqRlPqyD+JHMqz0IJb6qi2hn4UjtY32lr4QrxrQ814J/xr\r\nNTv2BmJe0t0jPqea/Z3YvuBbSZSBrkDXRzc1DTEjcAQhLeX0o05mwm2smGeG9EDc81FY8o/Wa6wW\r\naEqFNq3pIyqg8yl2p8rRaHyykvilhownd4y7JLl+ce4/+errS3KRb4WkWI9yi9ZbyXUUqigwK3e5\r\nwJV1iie8Kv3fQh9w+CJtffUfE6k3lx3i/n5sQjbNmFaqfHGtRkTX5gToLKyPsyV8QCFwAnnuF49s\r\nRJ5syb9tgXYBXdSQTmvukLXBc03lWQCPGeeXcyHakKoUATiuEWq8ju1cJXpA2S6NKp+j2kPyXwY+\r\nMSTGOzBsiSsYJqWzJxGatNz6qCous31bkHVFbKueok/bOf0z5C1vJT+ZZH75UPmyrcBguGNpmWzK\r\nzjOCZa1xZcleODO9fLVT/cW5Vd8YeV4wMqDrN55xb4d2CSNXyy4NkvPyfytpGL3vRSvQSSxpHkUz\r\nuXyFAToMqOVy688XW7cNbIHKE9eNqMaK"));
		System.out.println(decrypt("95nCcLPYrSBGecpn5snwkNCfUI8i2o4c5yYmpJzQ8FjyVg0mEjjBx8eAg5QmGU3oU2j5Lg0oudpVbasOdp2wYYVgwsONcWRKxbdEeyrxUMYonc7xzgugeWVRXIOK2+ZaamzKHaHGgZAq5kuGMiRq3DWjtDgQ4uJ4JEsPhcT7sjawnbEh1MnTykEq+gmtFZUTgh+pt+ghJI98F/Q41l3CwuOzJT0Hf8EyHt7u0KiRvD7Hc6Xz30HqNEu6lX91BRWF"));
	}
}