package org.albianj.configurtion;

import java.math.BigInteger;
import java.sql.Timestamp;

import org.albianj.persistence.object.IAlbianObject;

public interface IConfigItem extends IAlbianObject{

	static final String RootName = "root";
	static final BigInteger RootId = new BigInteger("0");
	
	public BigInteger getId();
	public void setId(BigInteger id);
	
	public String getName();
	public void setName(String name);
	
	public Object getValue();
	public void setValue(Object o);
	
	public BigInteger getParentId();
	public void setParentId(BigInteger pid);
	
	public boolean getEnable();
	public void setEnable(boolean enable);
	
	public String getDescribe();
	public void setDescribe(String describe);
	
	public Timestamp getCreateTime();
	public void setCreateTime(Timestamp ts);
	
	public Timestamp getLastModify();
	public void setLastModify(Timestamp ts);
	
	public String getAuthor();
	public void setAuthor(String author);
	
	public String getLastMender();
	public void setLastMender(String mender);
	
	public boolean getIsDelete();
	public void setIsDelete(boolean isDelete);
	
	public  String getParentNamePath();
	public void setParentNamePath(String pnp);
	
}
