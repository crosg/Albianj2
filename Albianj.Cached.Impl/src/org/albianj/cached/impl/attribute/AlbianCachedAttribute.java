package org.albianj.cached.impl.attribute;

import java.util.List;

import org.albianj.cached.attribute.IAlbianCachedAttribute;
import org.albianj.cached.attribute.IAlbianCachedServerAttribute;

public class AlbianCachedAttribute implements IAlbianCachedAttribute {
	private String name = null;
	private int cacheStyle = 0;
	private String type = null;
	private boolean enable = false;
	private List<IAlbianCachedServerAttribute> servers = null;
	private long connectTimeout = 30000;
	private int connectPoolsize = 100;
	private boolean isCluster = false;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	@Override
	public int getCacheStyle() {
		// TODO Auto-generated method stub
		return this.cacheStyle;
	}

	@Override
	public void setCacheStyle(int cs) {
		// TODO Auto-generated method stub
		this.cacheStyle = cs;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return this.type;
	}

	@Override
	public void setType(String type) {
		// TODO Auto-generated method stub
		 this.type = type;
	}

	@Override
	public boolean getEnable() {
		// TODO Auto-generated method stub
		return this.enable;
	}

	@Override
	public void setEnable(boolean enable) {
		// TODO Auto-generated method stub
		this.enable = enable;
	}

	@Override
	public List<IAlbianCachedServerAttribute> getServers() {
		// TODO Auto-generated method stub
		return this.servers;
	}

	@Override
	public void setServers(List<IAlbianCachedServerAttribute> servers) {
		// TODO Auto-generated method stub
		this.servers = servers;
	}

	@Override
	public long getConnectTimeout() {
		// TODO Auto-generated method stub
		return this.connectTimeout;
	}

	@Override
	public void setConnectTimeout(long tot) {
		// TODO Auto-generated method stub
		this.connectTimeout = tot;
	}

	@Override
	public int getConnectPoolSize() {
		// TODO Auto-generated method stub
		return this.connectPoolsize;
	}

	@Override
	public void setConnectPoolSize(int cps) {
		// TODO Auto-generated method stub
		this.connectPoolsize = cps;
	}

	@Override
	public boolean getCluster() {
		// TODO Auto-generated method stub
		return this.isCluster;
	}

	@Override
	public void setCluster(boolean isCluster) {
		// TODO Auto-generated method stub
		this.isCluster = isCluster;
	}

}
