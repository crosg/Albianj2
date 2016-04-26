package org.albianj.qidian.test.object;

import java.math.BigInteger;

import org.albianj.persistence.object.IAlbianObject;

public interface IUser extends IAlbianObject {

	public BigInteger getId();
	public void setId(BigInteger id);
	
	public String getName();
	public void setName(String name);
	
	public boolean getIsDelete();
	public void setIsDelete(boolean isDalete);
	
	public int getAge();
	public void setAge(int age);
	
	public void setSex(boolean sex);
	public boolean getSex();
	
	public String getUnit();
	public void setUnit(String unit);
	
	public String getTelNo();
	public void setTelNo(String tel);
}
