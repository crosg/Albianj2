package org.albianj.persistence.impl.toolkit;

import java.sql.Types;

public class Convert {
	public static int toSqlType(Class<?> cls) {
		String type = cls.getSimpleName().toLowerCase();
		if ("string".equalsIgnoreCase(type)) {
			return Types.VARCHAR;
		} else if ("bigdecimal".equalsIgnoreCase(type)) {
			return Types.NUMERIC;
		} else if ("boolean".equalsIgnoreCase(type)) {
			return Types.BIT;
		} else if ("integer".equalsIgnoreCase(type)
				|| "int".equalsIgnoreCase(type)) {
			return Types.INTEGER;
		} else if ("long".equalsIgnoreCase(type)) {
			return Types.BIGINT;
		} else if ("biginteger".equalsIgnoreCase(type)) {
			return Types.BIGINT;
		} else if ("float".equalsIgnoreCase(type)) {
			return Types.FLOAT;
		} else if ("double".equalsIgnoreCase(type)) {
			return Types.DOUBLE;
		} else if ("date".equalsIgnoreCase(type)) {
			return Types.DATE;
		} else if ("time".equalsIgnoreCase(type)) {
			return Types.TIME;
		} else if ("timestamp".equalsIgnoreCase(type)) {
			return Types.TIMESTAMP;
		} else if ("clob".equalsIgnoreCase(type)) {
			return Types.CLOB;
		} else if ("blob".equalsIgnoreCase(type)) {
			return Types.BLOB;
		} else if ("array".equalsIgnoreCase(type)) {
			return Types.ARRAY;
		} else {
			return Types.VARCHAR;
		}
	}

	public static int toSqlType(String typeSimpleName) {
		String typeName = typeSimpleName.toLowerCase();
		if ("char".equalsIgnoreCase(typeName)) {
			return Types.CHAR;
		} else if ("varchar".equalsIgnoreCase(typeName)) {
			return Types.VARCHAR;
		} else if ("longvarchar".equalsIgnoreCase(typeName)) {
			return Types.LONGVARCHAR;
		} else if ("numeric".equalsIgnoreCase(typeName)) {
			return Types.NUMERIC;
		} else if ("decimal".equalsIgnoreCase(typeName)) {
			return Types.DECIMAL;
		} else if ("bit".equalsIgnoreCase(typeName)) {
			return Types.BIT;
		} else if ("tinyint".equalsIgnoreCase(typeName)) {
			return Types.TINYINT;
		} else if ("smallint".equalsIgnoreCase(typeName)) {
			return Types.SMALLINT;
		} else if ("integer".equalsIgnoreCase(typeName)) {
			return Types.INTEGER;
		} else if ("bigint".equalsIgnoreCase(typeName)) {
			return Types.BIGINT;
		} else if ("real".equalsIgnoreCase(typeName)) {
			return Types.REAL;
		} else if ("float".equalsIgnoreCase(typeName)) {
			return Types.FLOAT;
		} else if ("double".equalsIgnoreCase(typeName)) {
			return Types.DOUBLE;
		} else if ("binary".equalsIgnoreCase(typeName)) {
			return Types.BINARY;
		} else if ("varbinary".equalsIgnoreCase(typeName)) {
			return Types.VARBINARY;
		} else if ("longvarbinary".equalsIgnoreCase(typeName)) {
			return Types.LONGVARBINARY;
		} else if ("date".equalsIgnoreCase(typeName)) {
			return Types.DATE;
		} else if ("time".equalsIgnoreCase(typeName)) {
			return Types.TIME;
		} else if ("timestamp".equalsIgnoreCase(typeName)) {
			return Types.TIMESTAMP;
		} else if ("clob".equalsIgnoreCase(typeName)) {
			return Types.CLOB;
		} else if ("blob".equalsIgnoreCase(typeName)) {
			return Types.BLOB;
		} else if ("array".equalsIgnoreCase(typeName)) {
			return Types.ARRAY;
		} else {
			return Types.VARCHAR;
		}
	}
}
