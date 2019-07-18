package com.ctr.crm.commons.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Base64 {
	public static String encode(String str) {

		if (str == null)
			return null;
		return (new BASE64Encoder()).encode(str.getBytes());
	}

	public static String decode(String s) {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}

	}

}
