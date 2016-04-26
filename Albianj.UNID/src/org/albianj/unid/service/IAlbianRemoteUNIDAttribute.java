package org.albianj.unid.service;

public interface IAlbianRemoteUNIDAttribute {

	public String getHost();

	public void setHost(String host);

	public int getPort();

	public void setPort(int port);

	public int getTimeout();

	public void setTimeout(int tto);
	
	int getPoolSize();
	void setPoolSize(int size);

}
