package org.albianj.persistence.impl.object;

import org.albianj.persistence.object.IDataRouterAttribute;

public class DataRouterAttribute implements IDataRouterAttribute {
	private String name = null;
	private String storageName = null;
	private String tableName = null;
	private String owner = "dbo";
	public boolean enable = true;

	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	public String getStorageName() {
		// TODO Auto-generated method stub
		return this.storageName;
	}

	public void setStorageName(String storageName) {
		// TODO Auto-generated method stub
		this.storageName = storageName;
	}

	public String getTableName() {
		// TODO Auto-generated method stub
		return this.tableName;
	}

	public void setTableName(String tableName) {
		// TODO Auto-generated method stub
		this.tableName = tableName;
	}

	public String getOwner() {
		// TODO Auto-generated method stub
		return this.owner;
	}

	public void setOwner(String owner) {
		// TODO Auto-generated method stub
		this.owner = owner;
	}

	public boolean getEnable() {
		return this.enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
}
