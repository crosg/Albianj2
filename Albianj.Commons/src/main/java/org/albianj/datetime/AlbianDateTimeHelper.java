package org.albianj.datetime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

    public static java.sql.Date today(){
        Date date = new Date();//取时间
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        return new java.sql.Date(cal.getTimeInMillis());
    }

    public static java.sql.Date yesterday(){
        Date date = new Date();//取时间
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        cal.add(Calendar.DATE,-1);
        return new java.sql.Date(cal.getTimeInMillis());
    }

    public static java.sql.Date tomorrow(){
        Date date = new Date();//取时间
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        cal.add(Calendar.DATE,1);
        return new java.sql.Date(cal.getTimeInMillis());
    }

}
