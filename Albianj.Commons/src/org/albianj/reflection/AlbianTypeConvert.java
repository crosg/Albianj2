package org.albianj.reflection;

import org.albianj.datetime.AlbianDateTime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xuhaifeng on 16/5/12.
 */
public class AlbianTypeConvert {
    public static  Object toRealObject(String type,String o) throws ParseException {

        if ("java.lang.string".equalsIgnoreCase(type)
                || "string".equalsIgnoreCase(type)) {
            return o;
        } else if (
            "java.math.bigdecimal".equalsIgnoreCase(type)
                || "bigdecimal".equalsIgnoreCase(type)) {
            BigDecimal bd = new BigDecimal(o.toString());
            return bd;
        } else if ("java.lang.boolean".equalsIgnoreCase(type)
        || "boolean".equalsIgnoreCase(type)) {
            return Boolean.parseBoolean(o.toString());
        } else if ("java.lang.integer".equalsIgnoreCase(type)
                || "int".equalsIgnoreCase(type)) {
            return Integer.parseInt(o.toString());
        } else if ("java.lang.long".equalsIgnoreCase(type)
        || "long".equalsIgnoreCase(type)) {
            return Long.parseLong(o.toString());
        } else if (
                "java.math.biginteger".equalsIgnoreCase(type)
                || "biginteger".equalsIgnoreCase(type)) {
            BigInteger bi = new BigInteger(o.toString());
            return bi;
        } else if ("java.lang.float".equalsIgnoreCase(type)
        || "float".equalsIgnoreCase(type)) {
            return Float.parseFloat(o.toString());
        } else if ("java.lang.double".equalsIgnoreCase(type)
                || "double".equalsIgnoreCase(type)) {
            return Double.parseDouble(o.toString());
        } else if("java.sql.time".equalsIgnoreCase(type)){
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
            };
            return new java.sql.Date(d.getTime());
        } else if("java.util.date".equalsIgnoreCase(type)){
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
        } else if("java.text.simpledateformat".equalsIgnoreCase(type)){
            return o;
        } else {
            return o;
        }
    }
}
