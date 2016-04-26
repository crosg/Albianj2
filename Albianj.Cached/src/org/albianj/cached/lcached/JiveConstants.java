package org.albianj.cached.lcached;

public class JiveConstants {

	public static final int SYSTEM = 17;
	public static final int ROSTER = 18;
	public static final int OFFLINE = 19;
	public static final int MUC_ROOM = 23;

	public static final long SECOND = 1000;
	public static final long MINUTE = 60 * SECOND;
	public static final long HOUR = 60 * MINUTE;
	public static final long DAY = 24 * HOUR;
	public static final long WEEK = 7 * DAY;

	/**
	 * Date/time format for use by SimpleDateFormat. The format conforms to <a
	 * HREF="http://www.jabber.org/jeps/jep-0082.html">JEP-0082</a>, which
	 * defines a unified date/time format for XMPP.
	 */
	public static final String XMPP_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	// Read more:
	// http://kickjava.com/src/org/jivesoftware/util/JiveConstants.java.htm#ixzz1WZHRPjRz
}
