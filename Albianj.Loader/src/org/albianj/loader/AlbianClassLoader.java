package org.albianj.loader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class AlbianClassLoader extends ClassLoader {
	private JarInputStream jis;
	private static Map<String, ByteBuffer> entryMap;

	private static AlbianClassLoader _cl = null;

	public static synchronized AlbianClassLoader getInstance() {
		if (null == _cl) {
			_cl = new AlbianClassLoader();
			entryMap = new HashMap<String, ByteBuffer>();
		}
		return _cl;
	}

	public boolean existClass(String name){
		String path = name.replace('.', '/').concat(".class");
		return entryMap.containsKey(path);
	}
	public AlbianClassLoader() {
		super();
	}

	public void regeditPlugin(String src) throws FileNotFoundException, IOException {
		regeditPlugin(new FileInputStream(src));
	}

	public void regeditPlugin(File file) throws FileNotFoundException, IOException {
		regeditPlugin(new FileInputStream(file));
	}

	public void regeditPlugin(InputStream is) throws IOException {
		jis = new JarInputStream(is);
		JarEntry entry = null;
		while ((entry = jis.getNextJarEntry()) != null) {
			String name = entry.getName();
			if (name.endsWith(".class")) { // class�ļ����ܺ��ٻ���
				byte[] bytes = getBytes(jis); // ��ȡclass�ļ�����
				ByteBuffer buffer = ByteBuffer.wrap(bytes); // ����ݸ��Ƶ�ByteBuffer������
				entryMap.put(name, buffer); // �������
			} else { // �����ļ�ֱ�ӻ���
				byte[] bytes = getBytes(jis);
				ByteBuffer buffer = ByteBuffer.wrap(bytes);
				entryMap.put(name, buffer);
			}
		}
		jis.close();
	}

	public void regeditPlugin(byte[] b) throws IOException {
		jis = new JarInputStream(new ByteArrayInputStream(b));
		JarEntry entry = null;
		while ((entry = jis.getNextJarEntry()) != null) {
			String name = entry.getName();
			
			if (name.endsWith(".class")) { // class�ļ����ܺ��ٻ���
				byte[] bytes = getBytes(jis); // ��ȡclass�ļ�����
				ByteBuffer buffer = ByteBuffer.wrap(bytes); // ����ݸ��Ƶ�ByteBuffer������
				entryMap.put(name, buffer); // �������
			} else { // �����ļ�ֱ�ӻ���
				byte[] bytes = getBytes(jis);
				ByteBuffer buffer = ByteBuffer.wrap(bytes);
				entryMap.put(name, buffer);
			}
		}
		jis.close();
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		String path = name.replace('.', '/').concat(".class");
		ByteBuffer buffer = entryMap.get(path);
		if (buffer == null) {
			return super.findClass(name);
		} else {
			byte[] bytes = buffer.array();
			return defineClass(name, bytes, 0, bytes.length);
		}
	}
	
	private byte[] getBytes(JarInputStream jis) throws IOException {
		int len = 0;
		byte[] bytes = new byte[8192];
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
		while ((len = jis.read(bytes, 0, bytes.length)) != -1) {
			baos.write(bytes, 0, len);
		}
		return baos.toByteArray();
	}

	/**
	 * �ر�Decoder
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		Iterator<ByteBuffer> iterator = entryMap.values().iterator();
		while (iterator.hasNext()) {
			ByteBuffer buffer = iterator.next();
			buffer.clear(); // ���ByteBuffer���󻺴�
		}
		entryMap.clear(); // ���HashMap
	}
}