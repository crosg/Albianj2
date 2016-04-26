package org.albianj.persistence.object;

public interface IRunningStorageAttribute {
	
	IStorageAttribute getStorageAttribute();
	void setStorageAttribute(IStorageAttribute sa);
	
	String getDatabase();
	void setDatabase(String database);

}
