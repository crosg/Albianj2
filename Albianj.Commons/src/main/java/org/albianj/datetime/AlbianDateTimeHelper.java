package org.albianj.datetime;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlbianDateTimeHelper {
    public static Date valueOf(String date,String pattern){
        Date d = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            d = dateFormat.parse(date);
        } catch (Exception e) {
            d = null;
        }
        return d;
    }

    public  static Date valueOf(String date) {
        Date d = null;
        d = valueOf(date,"yyyy-MM-dd HH:mm:ss");
        return null != d ? d : valueOf(date,"yyyy-MM-dd");
    }

    public static long toTimeMillis(String date,String pattern){
        Date d = valueOf(date,pattern);
        if(null == d) return 0;
        return d.getTime();
    }

    public static long toTimeMillis(String date){
        Date d = valueOf(date);
        if(null == d) return 0;
        return d.getTime();
    }

    public static java.sql.Date valueOfSqlDate(String date,String pattern){
        Date d = valueOf(date,pattern);
        if(null == d) return null;
        return new java.sql.Date(d.getTime());
    }

    public static java.sql.Date valueOfSqlDate(String date){
        Date d = valueOf(date);
        if(null == d) return null;
        return new java.sql.Date(d.getTime());
    }


}
