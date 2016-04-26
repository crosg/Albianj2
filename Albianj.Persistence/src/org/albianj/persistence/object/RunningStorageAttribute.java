package org.albianj.persistence.object;

public class RunningStorageAttribute implements IRunningStorageAttribute {

	public RunningStorageAttribute(IStorageAttribute sa,String database){
		this.sa = sa;
		this.database = database;
	}

	IStorageAttribute sa = null;
	String database = null;
	@Override
	public IStorageAttribute getStorageAttribute() {
		// TODO Auto-generated method stub
		return sa;
	}

	@Override
	public void setStorageAttribute(IStorageAttribute sa) {
		// TODO Auto-generated method stub
		this.sa = sa;
	}

	@Override
	public String getDatabase() {
		// TODO Auto-generated method stub
		return this.database;
	}

	@Override
	public void setDatabase(String database) {
		// TODO Auto-generated method stub
		this.database = database;
	}

}
