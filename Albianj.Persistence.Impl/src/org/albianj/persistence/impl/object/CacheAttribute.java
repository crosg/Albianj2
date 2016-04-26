package org.albianj.persistence.impl.object;

import org.albianj.persistence.object.ICacheAttribute;

public class CacheAttribute implements ICacheAttribute {
	private boolean enable = true;
	private int lifeTime = 300;
	private String name = null;

	public boolean getEnable() {
		// TODO Auto-generated method stub
		return this.enable;
	}

	public void setEnable(boolean enable) {
		// TODO Auto-generated method stub
		this.enable = enable;
	}

	public int getLifeTime() {
		// TODO Auto-generated method stub
		return this.lifeTime;
	}

	public void setLifeTime(int lifeTime) {
		// TODO Auto-generated method stub
		this.lifeTime = lifeTime;
	}

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

}
