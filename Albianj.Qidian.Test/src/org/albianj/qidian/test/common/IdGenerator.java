package org.albianj.qidian.test.common;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicLong;

import org.albianj.argument.RefArg;
import org.albianj.datetime.AlbianDateTime;

public class IdGenerator {

	private static AtomicLong seed = new AtomicLong(0);
	/**
	 * @return
	 */
	public static BigInteger make(){
		Timestamp ts =  AlbianDateTime.getDateTimeNow();
		long ymd = (1900+  ts.getYear()) * 10000 + (ts.getMonth() + 1) * 100 + ts.getDate();
		Long n = seed.getAndIncrement();
		BigInteger bi = new BigInteger(String.valueOf(ymd) );
		BigInteger bi2 = bi.multiply(new BigInteger("10000000")).add(new BigInteger(n.toString()));
		return bi2;
	}
	
	public static BigInteger make(long ymd){
		Timestamp ts =  AlbianDateTime.getDateTimeNow();
		Long n = seed.getAndIncrement();
		BigInteger bi = new BigInteger(String.valueOf(ymd) );
		BigInteger bi2 = bi.multiply(new BigInteger("10000000")).add(new BigInteger(n.toString()));
		return bi2;
	}
	
	public static void parser(BigInteger id,RefArg<Long> ymd,RefArg<Long> idx){
		String sid = id.toString();
		long y = Long.parseLong(sid.substring(0,8));
		if(null != ymd) ymd.setValue(y);
		long i = Long.parseLong(sid.substring(8));
		if(null != idx) idx.setValue(i);
		
	}
}
