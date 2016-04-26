package org.albianj.packing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import org.albianj.net.MemoryToIOStream;
import org.apache.commons.codec.binary.Base64;

public class AlbianClassZip {
	public static void pack(String srcDir, String destPath) {
		File dir = new File(srcDir);
		if (!dir.isDirectory()) {
			System.err.println("not found the path:" + srcDir);
			return;
		}
		File[] files = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (name.endsWith(".Impl.jar"));
			}
		});

		if (null == files || 0 >= files.length) {
			System.err.println("not found jar in the path:" + srcDir);
			return;
		}

		File destDir = new File(destPath);

		if (!destDir.isDirectory()) {
			destDir.mkdir();
		}

		File target = new File(destPath + File.separator + "Albianj.spx");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(target);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int size = files.length;
		byte[] bsSize = MemoryToIOStream.intToNetStream(size);
		try {
			fos.write(bsSize);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		for (File f : files) {
			try {
				long length = f.length();
				byte[] bs = new byte[(int) length];
				FileInputStream fis = new FileInputStream(f);
				fis.read(bs);
				Base64 b64 = new Base64();
				byte[] enbytes = b64.encode(bs);
				int elen = enbytes.length;
				byte[] bsLength = MemoryToIOStream.longToNetStream(elen);
				fos.write(bsLength);
				fos.write(enbytes);
				fis.close();
				System.out.println("packing the jar:" + f.getName() + ".length:" + length + ",encode length: " + elen);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}

		}
		try {
			fos.flush();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	
		System.out.println("packing jar is success.");
	}
	
	public static void main(String[] args) {
		pack(args[0],args[0]);
	}
}
