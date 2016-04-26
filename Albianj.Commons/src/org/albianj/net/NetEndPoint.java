package org.albianj.net;

public class NetEndPoint implements INetEndPoint {
	String host = null;
	int port = 0;

	public NetEndPoint() {
		// TODO Auto-generated constructor stub
	}
	
	public NetEndPoint(String host,int port) {
		this.host = host;
		this.port = port;
	}
	
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
