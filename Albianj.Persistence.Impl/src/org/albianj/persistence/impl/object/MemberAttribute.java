package org.albianj.persistence.impl.object;

import java.sql.Types;

import org.albianj.persistence.object.IMemberAttribute;

public class MemberAttribute implements IMemberAttribute {
	private String name = null;
	private String sqlFieldName = null;
	private boolean allowNull = true;
	private int length = -1;
	private boolean primaryKey = false;
	private int databaseType = Types.NVARCHAR;
	private boolean isSave = true;

	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	public String getSqlFieldName() {
		// TODO Auto-generated method stub
		return this.sqlFieldName;
	}

	public void setSqlFieldName(String sqlFieldName) {
		// TODO Auto-generated method stub
		this.sqlFieldName = sqlFieldName;
	}

	public boolean getAllowNull() {
		// TODO Auto-generated method stub
		return this.allowNull;
	}

	public void setAllowNull(boolean allowNull) {
		// TODO Auto-generated method stub
		this.allowNull = allowNull;
	}

	public int getLength() {
		// TODO Auto-generated method stub
		return this.length;
	}

	public void setLength(int length) {
		// TODO Auto-generated method stub
		this.length = length;
	}

	public boolean getPrimaryKey() {
		// TODO Auto-generated method stub
		return this.primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		// TODO Auto-generated method stub
		this.primaryKey = primaryKey;
	}

	public int getDatabaseType() {
		// TODO Auto-generated method stub
		return this.databaseType;
	}

	public void setDatabaseType(int databaseType) {
		// TODO Auto-generated method stub
		this.databaseType = databaseType;
	}

	public boolean getIsSave() {
		// TODO Auto-generated method stub
		return this.isSave;
	}

	public void setIsSave(boolean isSave) {
		// TODO Auto-generated method stub
		this.isSave = isSave;
	}

}
