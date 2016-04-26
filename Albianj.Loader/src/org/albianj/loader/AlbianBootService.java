package org.albianj.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.albianj.kernel.AlbianState;
import org.albianj.kernel.IAlbianTransmitterService;
import org.albianj.net.MemoryToIOStream;
import org.albianj.verify.Validate;
import org.apache.commons.codec.binary.Base64;

public class AlbianBootService {
	@SuppressWarnings("resource")
	private static ArrayList<byte[]> unpack(File target) {
		FileInputStream fis = null;
		ArrayList<byte[]> list = null;
		try {
			list = new ArrayList<byte[]>();
			fis = new FileInputStream(target);
			byte[] bsize = new byte[4];
			fis.read(bsize);
			long size = MemoryToIOStream.netStreamToInt(bsize, 0);
			for (int i = 0; i < size; i++) {
				byte[] blength = new byte[8];
				fis.read(blength);
				long length = MemoryToIOStream.netStreamToLong(blength, 0);
				byte[] ebytes = new byte[(int) length];
				fis.read(ebytes);
				Base64 b64 = new Base64();
				byte[] stream = b64.decode(ebytes);
				list.add(stream);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (null != fis) {
				fis.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("unzip kernel jars is fail.then exit the system.");
			System.exit(1);
		}
		return null;
	}

	public static boolean start() {
		return start(null, null);
	}

	public static boolean start(String configPath) {
		return start(null,null, configPath);
	}
	
	public static boolean start(String classpath,String kernelPath, String configPath){
		String jar = "Albianj.spx";
		File jarf = null;
		if(!Validate.isNullOrEmptyOrAllSpace(classpath)) {
			jarf = new File(classpath + File.separator + jar);
			if (!jarf.exists()) {
				System.err.println("not found albianj.spx.please input this file to current file or ext path. ");
				return false;
			}
		} else {
			jarf = new File(jar);
			if (!jarf.exists()) {
				String epath = System.getProperty("java.ext.dirs");
				System.out.println(epath);
				jarf = new File(epath + File.separator + jar);
				if (!jarf.exists()) {
					System.err.println("not found albianj.spx.please input this file to current file or ext path. ");
					return false;
				}
			} 
		}

		ArrayList<byte[]> list = unpack(jarf);
		if (Validate.isNullOrEmpty(list)) {
			System.err.println("unzip the jars is null. ");
			return false;
		}

		try {
			for (byte[] bs : list) {
				AlbianClassLoader.getInstance().regeditPlugin(bs);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		try {
			Class<?> clss = AlbianClassLoader.getInstance()
					.loadClass("org.albianj.kernel.impl.AlbianTransmitterService");
			IAlbianTransmitterService abs = (IAlbianTransmitterService) clss.newInstance();
			if (!Validate.isNullOrEmptyOrAllSpace(kernelPath) && !Validate.isNullOrEmptyOrAllSpace(configPath)) {
				abs.start(kernelPath, configPath);
			} else if (Validate.isNullOrEmptyOrAllSpace(kernelPath) && !Validate.isNullOrEmptyOrAllSpace(configPath)) {
				abs.start(configPath);
			} else {
				abs.start();
			}
			if (AlbianState.Running != abs.getLifeState()) {
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean start(String kernelPath, String configPath) {
		return start(null,kernelPath,configPath);
	}
}