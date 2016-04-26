package org.albianj.persistence.impl.db;

import java.sql.Types;

import org.albianj.persistence.db.ISqlParameter;

public class SqlParameter implements ISqlParameter {
	private int sqlType = Types.NVARCHAR;
	private String name = null;
	private Object value = null;
	private String sqlFieldName = null;

	// private Class valueClass;
	// private int length = 200;

	public int getSqlType() {
		return sqlType;
	}

	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setSqlFieldName(String sqlFieldName) {
		this.sqlFieldName = sqlFieldName;
	}

	public String getSqlFieldName() {
		return this.sqlFieldName;
	}

	// public Class getValueClass()
	// {
	// return valueClass;
	// }
	// public void setValueClass(Class<?> valueClass)
	// {
	// this.valueClass = valueClass;
	// }
	// public int getLength()
	// {
	// return length;
	// }
	// public void setLength(int length)
	// {
	// this.length = length;
	// }
}
