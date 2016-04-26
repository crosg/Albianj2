package org.albianj.persistence.object;

public interface ICacheAttribute {
	// <Cache Enable="false" LifeTime="300"></Cache>

	public boolean getEnable();

	public void setEnable(boolean enable);

	public int getLifeTime();

	public void setLifeTime(int lifeTime);
	
	public String getName();
	public void setName(String name);
}
