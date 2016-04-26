package org.albianj.cached.attribute;

import java.util.List;

public interface IAlbianCachedAttribute {
	
	String getName();
	void setName(String name);
	
	int getCacheStyle();
	void setCacheStyle(int cs);
	
	String getType();
	void setType(String type);
	
	boolean getEnable();
	void setEnable(boolean enable);
	
	boolean getCluster();
	void setCluster(boolean isCluster);
	
	long getConnectTimeout();
	void setConnectTimeout(long tot);
	
	int getConnectPoolSize();
	void setConnectPoolSize(int cps);
	
	List<IAlbianCachedServerAttribute> getServers();
	void setServers(List<IAlbianCachedServerAttribute> servers);
}
