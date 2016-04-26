package org.albianj.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.albianj.service.IAlbianService;

public interface IAlbianSecurityService extends IAlbianService {
	
	final String Name = "AlbianSecurityService";

	public String decryptDES(String message) throws Exception;
	public String decryptDES(String key, String message) throws Exception ;
	
	public String encryptDES(String message) throws Exception ;
	public String encryptDES(String key, String message) throws Exception ;
	
	
	public byte[] decryptBASE64(String key) throws Exception ;
	public String encryptBASE64(byte[] key) throws Exception ;

	public String encryptMD5(String data) throws Exception ;
	public String encryptSHA(String data) throws Exception;

	
	public String initMacKey() throws Exception ;
	public String initMacKey(MACStyle style) throws Exception ;

	public String encryptHMAC(String key, MACStyle style, byte[] data)
			throws Exception ;
	public String encryptHMAC(String key, MACStyle style, String data)
			throws Exception ;

	public String encryptHMAC(String key, byte[] data) throws Exception ;
	public String encryptHMAC(String key, String data) throws Exception;
	
	
}
