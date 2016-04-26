package org.albianj.datetime;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlbianDateTime {
	public static final String DEFAULT_FORMAT = "yyyyMMddHHmmss";
	public static final String DEFAULT_TIME_FORMAT = "HHmmss";
	public static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
	public static final String CHINESE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String CHINESE_SIMPLE_FORMAT = "yyyy-MM-dd";

	public static String getDateTimeString() {
		return getDateTimeString(new Date(), DEFAULT_FORMAT);
	}

	public static String getDateTimeString(String format) {
		return getDateTimeString(new Date(), format);
	}

	public static String getDateTimeString(Date date) {
		return getDateTimeString(date, DEFAULT_FORMAT);
	}

	public static String getDateTimeString(Date date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	public static String getDateString() {
		return getDateTimeString(new Date(), DEFAULT_DATE_FORMAT);
	}
	public static String getTimeString() {
		return getDateTimeString(new Date(), DEFAULT_TIME_FORMAT);
	}

	/**
	 * @param begin
	 * @param end
	 * @return timespan seconds
	 */
	public static long getTimeSpan(Date begin, Date end) {
		return (end.getTime() - begin.getTime()) / 1000;
	}

	public static Timestamp getDateTimeNow() {
		return new Timestamp(new Date().getTime());
	}
	
	public static Timestamp getDateTimeFromUnixTime(long ut) {
		return new Timestamp(new Date(ut * 1000).getTime());
	}


	@SuppressWarnings("deprecation")
	public static Timestamp getBaseDate(int year, int month, int day) {
		return new Timestamp(new Date(year, month, day).getTime());
	}

	@SuppressWarnings("deprecation")
	public static Date dateAddSeconds(int year, int month, int day, long second) {
		Date dt = new Date();
		dt.setYear(year - 1900);
		dt.setMonth(month - 1);
		dt.setDate(day);
		dt.setHours(0);
		dt.setMinutes(0);
		dt.setSeconds(0);
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(dt);
		rightNow.add(Calendar.SECOND, (int) second);
		Date dt1 = rightNow.getTime();
		return dt1;
	}
}
