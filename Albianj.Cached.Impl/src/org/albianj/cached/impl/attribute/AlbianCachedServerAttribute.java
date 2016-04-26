package org.albianj.cached.impl.attribute;

import org.albianj.cached.attribute.IAlbianCachedServerAttribute;

public class AlbianCachedServerAttribute implements IAlbianCachedServerAttribute {
	private String host = null;
	private int port = 6379;
	@Override
	public String getHost() {
		// TODO Auto-generated method stub
		return this.host;
	}

	@Override
	public void setHost(String host) {
		// TODO Auto-generated method stub
		this.host = host;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return this.port;
	}

	@Override
	public void setPort(int port) {
		// TODO Auto-generated method stub
		this.port = port;
	}

}
