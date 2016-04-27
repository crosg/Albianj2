package org.albianj.security.impl;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.albianj.kernel.KernelSetting;
import org.albianj.security.IAlbianSecurityService;
import org.albianj.security.MACStyle;
import org.albianj.security.StyleMapping;
import org.albianj.service.FreeAlbianService;
import org.albianj.text.StringHelper;
import org.albianj.verify.Validate;
import org.apache.commons.codec.binary.Base64;

public class AlbianSecurityService extends FreeAlbianService implements IAlbianSecurityService {

	private String DEFAULT_SHA_KEY = "sdfgrgeyt*&()43543dfgsdfgs6454";
	private  String DEFAULT_MD5_KEY = "!t#==-;'sdfd3432dfgdgs43242#!";
	private String DEFAULT_DES_KEY = "$fdge5rt7903=dfgdgr;.,'ergfegn$";
	
	@Override
	public void init() throws org.albianj.service.parser.AlbianParserException {
		super.init();
		String mkey = KernelSetting.getMachineKey();
		if(Validate.isNullOrEmptyOrAllSpace(mkey)){
			return;
		}
		if(40 <= mkey.length()) {
			DEFAULT_SHA_KEY = mkey.substring(3, 40);
		}
		if(80 <= mkey.length()){
			DEFAULT_MD5_KEY = mkey.substring(50,79);
		}
		if(60 <= mkey.length()){
			DEFAULT_DES_KEY = mkey.substring(30,58);
		}
	}
	
	
	public String decryptDES(String message) throws Exception {
		return decryptDES(DEFAULT_DES_KEY, message);
	}

	public String decryptDES(String key, String message) throws Exception {
		String k = StringHelper.padLeft(key, 8);
		byte[] bytesrc = decryptBASE64(message);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(k.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(k.getBytes("UTF-8"));

		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

		byte[] retByte = cipher.doFinal(bytesrc);
		return new String(retByte);
	}

	public String encryptDES(String message) throws Exception {
		return encryptDES(DEFAULT_DES_KEY, message);
	}

	public String encryptDES(String key, String message) throws Exception {
		String k = StringHelper.padLeft(key, 8);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		DESKeySpec desKeySpec = new DESKeySpec(k.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
		IvParameterSpec iv = new IvParameterSpec(k.getBytes("UTF-8"));
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

		return encryptBASE64(cipher.doFinal(message.getBytes("UTF-8")));
	}

	
	
	public byte[] decryptBASE64(String key) throws Exception {
		return Base64.decodeBase64(key);
	}

	public String encryptBASE64(byte[] key) throws Exception {
		return Base64.encodeBase64String(key);
	}

	public String encryptMD5(String data) throws Exception {
		return encryptHMAC(DEFAULT_MD5_KEY, MACStyle.MD5, data);
	}

	public String encryptSHA(String data) throws Exception {
		return encryptHMAC(DEFAULT_SHA_KEY, MACStyle.SHA1, data);
	}

	public String initMacKey() throws Exception {
		return initMacKey(MACStyle.MD5);
	}

	public String initMacKey(MACStyle style) throws Exception {
		return initMacKey(StyleMapping.toMACStyleString(style));
	}

	protected String initMacKey(String key) throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(key);
		SecretKey secretKey = keyGenerator.generateKey();
		return encryptBASE64(secretKey.getEncoded());
	}

	public String encryptHMAC(String key, MACStyle style, byte[] data)
			throws Exception {
		SecretKey secretKey = new SecretKeySpec(decryptBASE64(key),
				StyleMapping.toMACStyleString(style));
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return encryptBASE64(mac.doFinal(data));
	}

	public String encryptHMAC(String key, MACStyle style, String data)
			throws Exception {
		SecretKey secretKey = new SecretKeySpec(decryptBASE64(key),
				StyleMapping.toMACStyleString(style));
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return encryptBASE64(mac.doFinal(decryptBASE64(data)));
	}

	public String encryptHMAC(String key, byte[] data) throws Exception {
		return encryptHMAC(key, MACStyle.MD5, data);
	}

	public String encryptHMAC(String key, String data) throws Exception {
		return encryptHMAC(key, MACStyle.MD5, data);
	}

	
}
