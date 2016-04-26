package org.albianj.net;

public class MsgHeader implements IMsgHeader {

	public MsgHeader() {

	}

	public MsgHeader(int version, int protocol, long bodylen, long offset,
			boolean keepalive, int error) {
		this._version = version;
		this._protocol = protocol;
		this._bodylen = bodylen;
		this._offset = offset;
		this._keepalive = keepalive;
		this._error = error;
	}

	private int _version = 0;
	private int _protocol = 0;
	private long _bodylen = 0;
	private long _offset = 0;
	private boolean _keepalive = false;
	private int _error = 0;

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return this._version;
	}

	@Override
	public void setVersion(int version) {
		// TODO Auto-generated method stub
		this._version = version;
	}

	@Override
	public int getProtocol() {
		// TODO Auto-generated method stub
		return this._protocol;
	}

	@Override
	public void setProtocol(int protocol) {
		// TODO Auto-generated method stub
		this._protocol = protocol;
	}

	@Override
	public long getBodyLength() {
		// TODO Auto-generated method stub
		return this._bodylen;
	}

	@Override
	public void setBodyLength(long bodylen) {
		// TODO Auto-generated method stub
		this._bodylen = bodylen;
	}

	@Override
	public long getOffset() {
		// TODO Auto-generated method stub
		return this._offset;
	}

	@Override
	public void setOffset(long offset) {
		// TODO Auto-generated method stub
		this._offset = offset;
	}

	@Override
	public boolean getKeepAlive() {
		// TODO Auto-generated method stub
		return this._keepalive;
	}

	@Override
	public void setKeepAlive(boolean keepalive) {
		// TODO Auto-generated method stub
		this._keepalive = keepalive;
	}

	@Override
	public int getError() {
		// TODO Auto-generated method stub
		return this._error;
	}

	@Override
	public void setError(int error) {
		// TODO Auto-generated method stub
		this._error = error;
	}

	public byte[] pack() {
		byte[] buff = new byte[MsgHeaderLength];
		MemoryToIOStream.intToNetStream(_version, buff, 0);
		MemoryToIOStream.intToNetStream(_protocol, buff, 4);
		MemoryToIOStream.longToNetStream(_bodylen, buff, 8);
		MemoryToIOStream.longToNetStream(_offset, buff, 16);
		buff[24] = _keepalive ? Byte.parseByte("1") : Byte.parseByte("0");
		MemoryToIOStream.intToNetStream(_error, buff, 25);
		return buff;
	}

	public IMsgHeader unpack(byte[] buff) {
		this._version = MemoryToIOStream.netStreamToInt(buff, 0);
		this._protocol = MemoryToIOStream.netStreamToInt(buff, 4);
		this._bodylen = MemoryToIOStream.netStreamToLong(buff, 8);
		this._offset = MemoryToIOStream.netStreamToLong(buff, 16);
		this._keepalive = (0 != buff[24]);
		this._error = MemoryToIOStream.netStreamToInt(buff, 25);
		return this;
	}

}
