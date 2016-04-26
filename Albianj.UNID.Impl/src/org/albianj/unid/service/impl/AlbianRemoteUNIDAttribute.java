package org.albianj.unid.service.impl;

import org.albianj.unid.service.IAlbianRemoteUNIDAttribute;

public class AlbianRemoteUNIDAttribute implements IAlbianRemoteUNIDAttribute {

	private String _host = null;
	private int _port = 0;
	private int _tto = 30;
	private int poolsize = 20;

	@Override
	public String getHost() {
		// TODO Auto-generated method stub
		return this._host;
	}

	@Override
	public void setHost(String host) {
		// TODO Auto-generated method stub
		_host = host;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return this._port;
	}

	@Override
	public void setPort(int port) {
		// TODO Auto-generated method stub
		this._port = port;
	}

	public int getTimeout() {
		return _tto;
	}

	public void setTimeout(int tto) {
		this._tto = tto;
	}

	@Override
	public int getPoolSize() {
		// TODO Auto-generated method stub
		return this.poolsize;
	}

	@Override
	public void setPoolSize(int size) {
		// TODO Auto-generated method stub
		this.poolsize = size;
	}

}
