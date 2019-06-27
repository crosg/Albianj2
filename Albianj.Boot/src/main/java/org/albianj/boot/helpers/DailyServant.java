package org.albianj.boot.helpers;

import org.albianj.boot.tags.BundleSharingTag;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

@BundleSharingTag
public class DailyServant {
    /**
     * 一天时间的时间戳，单位MS
     */
    public final static long DailyTimestampMS = 86400000;

    public static DailyServant Instance = null;

    static {
        if(null == Instance) {
            Instance = new DailyServant();
        }
    }

    protected DailyServant() {

    }

    /**
     * 当前日期零点的时间戳
     *
     * @return
     */
    public long todayTimestampOfZero() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 当前日期的字符串，日期"-"分隔符，时间":"分隔
     */
    public String datetimeLongString() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(calendar.getTime());
    }

    /**
     * 当前日期的字符串，然后"-"分隔符
     */
    public String dateString() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(calendar.getTime());
    }

    /**
     * 当前时间的字符串，然后":"分隔符
     */
    public String timeString() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(calendar.getTime());
    }

    /**
     * 当前日期时间的字符串，无":"分隔符
     */
    public String datetimeLongStringWithoutSep() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(calendar.getTime());
    }

    /**
     * 当前日期的字符串，无":"分隔符
     */
    public String dateStringWithoutSep() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(calendar.getTime());
    }

    /**
     * 当前时间的字符串，无":"分隔符
     */
    public String timeStringWithoutSep() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        DateFormat df = new SimpleDateFormat("HHmmss");
        return df.format(calendar.getTime());
    }

}
