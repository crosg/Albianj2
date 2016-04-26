package org.albianj.security;

public class StyleMapping {
	public static String toMACStyleString(MACStyle style) {
		switch (style) {
		case MD5: {
			return "HmacMD5";
		}
		case SHA1: {
			return "HmacSHA1";
		}
		case SHA256: {
			return "HmacSHA256";
		}
		case SHA384: {
			return "HmacSHA384";
		}
		case SHA512: {
			return "HmacSHA512";
		}
		default: {
			// Hmac
			return "HmacMD5";
		}
		}
	}
}
