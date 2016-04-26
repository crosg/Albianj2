package org.albianj.net;

public class MemoryToIOStream {
	public static byte[] intToNetStream(int n) {
		byte[] buff = new byte[4];
		buff[0] = (byte) ((n >> 24) & 0xFF);
		buff[1] = (byte) ((n >> 16) & 0xFF);
		buff[2] = (byte) ((n >> 8) & 0xFF);
		buff[3] = (byte) (n & 0xFF);
		return buff;
	}

	public static byte[] intToNetStream(int n, byte[] buff, int offset) {
		buff[offset] = (byte) ((n >> 24) & 0xFF);
		buff[offset + 1] = (byte) ((n >> 16) & 0xFF);
		buff[offset + 2] = (byte) ((n >> 8) & 0xFF);
		buff[offset + 3] = (byte) (n & 0xFF);
		return buff;
	}

	public static int netStreamToInt(byte[] buff, int offset) {
		int n = (int) (
				  ((((int) buff[offset]) & 0xFF) << 24)
				| ((((int) buff[offset + 1]) & 0xFF) << 16)
				| ((((int) buff[offset + 2]) & 0xFF) << 8) 
				| ((((int) buff[offset + 3]) & 0xFF)));
		return n;
	}

	public static byte[] longToNetStream(long n) {
		byte[] buff = new byte[8];
		buff[0] = (byte) ((n >> 56) & 0xFF);
		buff[1] = (byte) ((n >> 48) & 0xFF);
		buff[2] = (byte) ((n >> 40) & 0xFF);
		buff[3] = (byte) ((n >> 32) & 0xFF);
		buff[4] = (byte) ((n >> 24) & 0xFF);
		buff[5] = (byte) ((n >> 16) & 0xFF);
		buff[6] = (byte) ((n >> 8) & 0xFF);
		buff[7] = (byte) (n & 0xFF);
		return buff;
	}

	public static byte[] longToNetStream(long n, byte[] buff, int offset) {
		buff[offset] = (byte) ((n >> 56) & 0xFF);
		buff[offset + 1] = (byte) ((n >> 48) & 0xFF);
		buff[offset + 2] = (byte) ((n >> 40) & 0xFF);
		buff[offset + 3] = (byte) ((n >> 32) & 0xFF);
		buff[offset + 4] = (byte) ((n >> 24) & 0xFF);
		buff[offset + 5] = (byte) ((n >> 16) & 0xFF);
		buff[offset + 6] = (byte) ((n >> 8) & 0xFF);
		buff[offset + 7] = (byte) (n & 0xFF);
		return buff;
	}

	public static long netStreamToLong(byte[] buff, int offset) {
		long n = (long) (
				  ((((long) buff[offset]) & 0xFF) << 56)
				| ((((long) buff[offset + 1]) & 0xFF) << 48)
				| ((((long) buff[offset + 2]) & 0xFF) << 40)
				| ((((long) buff[offset + 3]) & 0xFF) << 32)
				| ((((long) buff[offset + 4]) & 0xFF) << 24)
				| ((((long) buff[offset + 5]) & 0xFF) << 16)
				| ((((long) buff[offset + 6]) & 0xFF) << 8) 
				| ((((long) buff[offset + 7]) & 0xFF)));
		return n;
	}

	public static byte[] intToNetStreamLE(int n) {
		byte[] buff = new byte[4];
		buff[3] = (byte) ((n >> 24) & 0xFF);
		buff[2] = (byte) ((n >> 16) & 0xFF);
		buff[1] = (byte) ((n >> 8) & 0xFF);
		buff[0] = (byte) (n & 0xFF);
		return buff;
	}

	public static byte[] intToNetStreamLE(int n, byte[] buff, int offset) {
		buff[offset + 3] = (byte) ((n >> 24) & 0xFF);
		buff[offset + 2] = (byte) ((n >> 16) & 0xFF);
		buff[offset + 1] = (byte) ((n >> 8) & 0xFF);
		buff[offset] = (byte) (n & 0xFF);
		return buff;
	}

	public static int netStreamToIntLE(byte[] buff, int offset) {
		int n = (int) (
				  ((((int) buff[offset + 3]) & 0xFF) << 24)
				| ((((int) buff[offset + 2]) & 0xFF) << 16)
				| ((((int) buff[offset + 1]) & 0xFF) << 8) 
				| ((((int) buff[offset]) & 0xFF)));
		return n;
	}

	public static byte[] longToNetStreamLE(long n) {
		byte[] buff = new byte[8];
		buff[7] = (byte) ((n >> 56) & 0xFF);
		buff[6] = (byte) ((n >> 48) & 0xFF);
		buff[5] = (byte) ((n >> 40) & 0xFF);
		buff[4] = (byte) ((n >> 32) & 0xFF);
		buff[3] = (byte) ((n >> 24) & 0xFF);
		buff[2] = (byte) ((n >> 16) & 0xFF);
		buff[1] = (byte) ((n >> 8) & 0xFF);
		buff[0] = (byte) (n & 0xFF);
		return buff;
	}

	public static byte[] longToNetStreamLE(long n, byte[] buff, int offset) {
		buff[offset + 7] = (byte) ((n >> 56) & 0xFF);
		buff[offset + 6] = (byte) ((n >> 48) & 0xFF);
		buff[offset + 5] = (byte) ((n >> 40) & 0xFF);
		buff[offset + 4] = (byte) ((n >> 32) & 0xFF);
		buff[offset + 3] = (byte) ((n >> 24) & 0xFF);
		buff[offset + 2] = (byte) ((n >> 16) & 0xFF);
		buff[offset + 1] = (byte) ((n >> 8) & 0xFF);
		buff[offset] = (byte) (n & 0xFF);
		return buff;
	}

	public static long netStreamToLongLE(byte[] buff, int offset) {
		long n = (long) (
				  ((((long) buff[offset + 7]) & 0xFF) << 56)
				| ((((long) buff[offset + 6]) & 0xFF) << 48)
				| ((((long) buff[offset + 5]) & 0xFF) << 40)
				| ((((long) buff[offset + 4]) & 0xFF) << 32)
				| ((((long) buff[offset + 3]) & 0xFF) << 24)
				| ((((long) buff[offset + 2]) & 0xFF) << 16)
				| ((((long) buff[offset + 1]) & 0xFF) << 8) 
				| ((((long) buff[offset ]) & 0xFF)));
		return n;
	}
}
