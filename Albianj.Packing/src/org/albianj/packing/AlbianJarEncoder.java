package org.albianj.packing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.commons.codec.binary.Base64;

//import com.wind.load.Base64;

public class AlbianJarEncoder {
	private JarInputStream jis;

	public AlbianJarEncoder(String src) throws FileNotFoundException, IOException {
		this(new FileInputStream(src));
	}

	public AlbianJarEncoder(File file) throws FileNotFoundException, IOException {
		this(new FileInputStream(file));
	}

	public AlbianJarEncoder(InputStream is) throws IOException {
		jis = new JarInputStream(is);
	}

	/**
	 * ͨ��ָ����·��������ܺ��jar
	 * 
	 * @param target
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void write(String target) throws FileNotFoundException, IOException {
		write(new FileOutputStream(target));
	}

	/**
	 * ͨ��ָ�����ļ�������ܺ��jar
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void write(File file) throws FileNotFoundException, IOException {
		write(new FileOutputStream(file));
	}

	/**
	 * ͨ��ָ���������������ܺ��jar
	 * 
	 * @param os
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void write(OutputStream os) throws FileNotFoundException, IOException {
		Manifest menifest = jis.getManifest(); // ��ȡjar��Manifest��Ϣ
		JarOutputStream jos = null;
		if (menifest == null) {
			jos = new JarOutputStream(os);
		} else {
			// JarInputStream��getNextJarEntry()�����޷���ȡManifest��Ϣ������ֻ��ͨ�����ַ�ʽд��Manifest��Ϣ
			jos = new JarOutputStream(os, menifest);
		}

		JarEntry entry = null;
		while ((entry = jis.getNextJarEntry()) != null) {
			jos.putNextEntry(entry);
			if (entry.getName().endsWith(".class")) { // ֻ����class�ļ�
				byte[] bytes = getBytes(jis); // ��ȡclass�ļ�����
//				Base64 b64 = new Base64();
//				byte[] enbytes = b64.encode(bytes);
				jos.write(bytes, 0, bytes.length); // �Ѽ��ܺ����Ϣд����
			} else { // �������͵��ļ�ֱ��д����
				byte[] bytes = getBytes(jis);
				jos.write(bytes, 0, bytes.length);
			}
			jos.flush();
		}
		jos.close();
		jis.close();
	}

	/**
	 * ��jar�������ж�ȡ��Ϣ
	 * 
	 * @param jis
	 * @return
	 * @throws IOException
	 */
	private byte[] getBytes(JarInputStream jis) throws IOException {
		int len = 0;
		byte[] bytes = new byte[8192];
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
		while ((len = jis.read(bytes, 0, bytes.length)) != -1) {
			baos.write(bytes, 0, len);
		}
		return baos.toByteArray();
	}
}