package org.albianj.net;

public interface IMsgHeader {
	public static final int MsgHeaderLength = 29;

	public int getVersion();

	public void setVersion(int version);

	public int getProtocol();

	public void setProtocol(int protocol);

	public long getBodyLength();

	public void setBodyLength(long bodylen);

	public long getOffset();

	public void setOffset(long offset);

	public boolean getKeepAlive();

	public void setKeepAlive(boolean keepalive);

	public int getError();

	public void setError(int error);

	public byte[] pack();

	public IMsgHeader unpack(byte[] buffer);
}
