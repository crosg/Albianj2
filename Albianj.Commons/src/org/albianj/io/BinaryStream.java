package org.albianj.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class BinaryStream {

	public static Object deepClone(Object obj) throws IOException, ClassNotFoundException  {
		Object newObj = null;
		ByteArrayOutputStream byteOut = null;
		ObjectOutputStream out = null;
		ByteArrayInputStream byteIn = null;
		ObjectInputStream in = null;
		try {
			byteOut = new ByteArrayOutputStream();
			out = new ObjectOutputStream(byteOut);
			if (null == obj)
				return null;
			out.writeObject(obj);
			byteIn = new ByteArrayInputStream(byteOut.toByteArray());
			in = new ObjectInputStream(byteIn);
			newObj = in.readObject();
		} finally {
			try {
				if (null != out)
					out.close();
			} catch (IOException e) {
							}
			try {
				if (null != byteOut)
					byteOut.close();
			} catch (IOException e) {
			}
			try {
				if (null != in)
					in.close();
			} catch (IOException e) {
			}
			try {
				if (null != byteIn)
					byteIn.close();
			} catch (IOException e) {
			}
	
		}
		return newObj;
	}

}
