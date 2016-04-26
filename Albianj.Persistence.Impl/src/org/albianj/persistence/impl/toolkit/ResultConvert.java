package org.albianj.persistence.impl.toolkit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.albianj.datetime.AlbianDateTime;

public class ResultConvert {

	@SuppressWarnings("deprecation")
	public static Object toBoxValue(Class<?> cls, Object o) throws Exception {
		String type = cls.getSimpleName().toLowerCase();
		if ("string".equalsIgnoreCase(type)) {
			return o.toString();
		} else if ("bigdecimal".equalsIgnoreCase(type)) {
			BigDecimal bd = new BigDecimal(o.toString());
			return bd;
		} else if ("boolean".equalsIgnoreCase(type)) {
			return Boolean.parseBoolean(o.toString());
		} else if ("integer".equalsIgnoreCase(type)
				|| "int".equalsIgnoreCase(type)) {
			return Integer.parseInt(o.toString());
		} else if ("long".equalsIgnoreCase(type)) {
			return Long.parseLong(o.toString());
		} else if ("biginteger".equalsIgnoreCase(type)) {
			BigInteger bi = new BigInteger(o.toString());
			return bi;
		} else if ("float".equalsIgnoreCase(type)) {
			return Float.parseFloat(o.toString());
		} else if ("double".equalsIgnoreCase(type)) {
			return Double.parseDouble(o.toString());
		} else if ("date".equalsIgnoreCase(type)) {
			Date d = null;
			try{
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						AlbianDateTime.CHINESE_SIMPLE_FORMAT);
				d = dateFormat.parse(o.toString());
			}catch(Exception e){
				d = null;
			}
			if(null == d) {
				try{
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							AlbianDateTime.CHINESE_FORMAT);
					d = dateFormat.parse(o.toString());
				}catch(Exception e){
					throw e; 
				}
			}
			return d;
			
		} else if ("time".equalsIgnoreCase(type)) {
			return Time.parse(o.toString());
		} else if ("timestamp".equalsIgnoreCase(type)) {
			return o;// donot ask me why,if i parser ,it will be crash
			// return Timestamp.parse(o.toString());
		} else {
			return o;
		}
	}

	public static String sqlValueToString(int sqlType, Object v) {
		if(null == v) return "";
		switch (sqlType) {
		case Types.DATE: {
			Date d = (Date) v;
			return AlbianDateTime.getDateTimeString(d);
		}
		case Types.TIME: {
			Time t = (Time) v;
			return t.toString();
		}
		case Types.TIMESTAMP: {
			Timestamp ts = (Timestamp) v;
			return ts.toString();
		}
		default: {
			return v.toString();
		}
		}
	}
}
