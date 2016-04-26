package org.albianj.kernel.impl;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.albianj.datetime.AlbianDateTime;
import org.albianj.kernel.IAlbianLogicIdService;
import org.albianj.kernel.KernelSetting;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.impl.AlbianLoggerService;
import org.albianj.net.AlbianHost;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.FreeAlbianService;
import org.albianj.text.StringHelper;
import org.albianj.verify.Validate;

/**
 * ??????ID?????????
 * ID???asiic?????????????????????32.?????????kernel-appname-timestamep
 * -serial????????????
 * kernel??????????????????kernel.properties??????key???????????????4 appname
 * ????????????????????????kernel???????????????7,appname???????????????0???
 * timestamep
 * id???????????????????????????????????????YYYYMMDDHHmmSS????????????14
 * serial-number ?????????????????????4
 * 
 * @Important?????????????????????????????????????????????????????????????????????0
 * @author Seapeak
 * 
 */
public class AlbianLogicIdService extends FreeAlbianService implements IAlbianLogicIdService {
	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.AlbianLogicIdServide#makeStringUNID()
	 */
	@Override
	public synchronized String makeStringUNID() {
		return makeStringUNID("Kenerl");
	}

	static AtomicInteger serial = new AtomicInteger(0);

	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.AlbianLogicIdServide#makeStringUNID(java.lang.String)
	 */
	@Override
	public synchronized String makeStringUNID(String appName) {
		// ?????????????????????????????????????????????????????????
		Random rnd = new Random();
		rnd.setSeed(10000);
		int numb = rnd.nextInt(10000);
		numb = (numb ^ serial.getAndIncrement()) % 10000;// ????????????????????????
		serial.compareAndSet(10000, 0);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String app = appName;
		if (app.length() < 7) {
			app = StringHelper.padLeft(app, 7);
		}
		if (app.length() > 7) {
			app = app.substring(0, 7);
		}

		return String.format("%1$s-%2$s-%3$s-%4$04d",
				StringHelper.padLeft(KernelSetting.getKernelId(), 4), app,
				dateFormat.format(new Date()), numb);
	}

	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.AlbianLogicIdServide#generate32UUID()
	 */
	@Override
	@SuppressWarnings("static-access")
	public synchronized String generate32UUID() {
		return UUID.randomUUID().randomUUID().toString().replaceAll("-", "");
	}

	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.AlbianLogicIdServide#getAppName(java.lang.String)
	 */
	@Override
	public String getAppName(String id) {
		if (Validate.isNullOrEmptyOrAllSpace(id)) {
			return null;
		}
		String[] strs = id.split("-");
		if (4 != strs.length) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,"id:%1$s is fail.", id);
			return null;
		}
		return StringHelper.censoredZero(strs[1]);

	}

	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.AlbianLogicIdServide#getGenerateDateTime(java.lang.String)
	 */
	@Override
	public Date getGenerateDateTime(String id) {
		if (Validate.isNullOrEmptyOrAllSpace(id)) {
			return null;
		}
		String[] strs = id.split("-");
		if (4 != strs.length) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,"id:%1$s is fail.", id);
			return null;
		}

		DateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
		Date d;
		try {
			d = f.parse(strs[2]);
		} catch (ParseException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,"id:%1$s is fail.", id);
			return null;
		}
		return d;
	}

	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.AlbianLogicIdServide#getGenerateTime(java.lang.String)
	 */
	@Override
	public Calendar getGenerateTime(String id) {
		if (Validate.isNullOrEmptyOrAllSpace(id)) {
			return null;
		}
		String[] strs = id.split("-");
		if (4 != strs.length) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,"id:%1$s is fail.", id);
			return null;
		}

		DateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
		Date d = null;
		try {
			d = f.parse(strs[2]);
		} catch (ParseException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,"id:%1$s is fail.", id);
			return null;
		}
		Calendar cal = Calendar.getInstance(); // ?????????????????????????????????date????????????nm?????????
		cal.setTime(d);
		return cal;
	}
	
	static AtomicLong id = new AtomicLong(0);
	static AtomicLong jobid = new AtomicLong(0);
	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.AlbianLogicIdServide#makeLoggerId()
	 */
	@Override
	public String makeLoggerId() {
		
		id.compareAndSet(1000000, 0);
		try {
			return String.format("%d-%d-%d", AlbianHost.ipToLong(AlbianHost.getLocalIp()),
					AlbianDateTime.getDateTimeNow().getTime(),
					id.getAndIncrement());
		} catch (UnknownHostException e) {
			return String.format("%d-%d", AlbianDateTime.getDateTimeNow()
					.getTime(), id.getAndIncrement());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.AlbianLogicIdServide#makeJobId()
	 */
	@Override
	public String makeJobId(){
		id.compareAndSet(1000000, 0);
		try {
			return String.format("%d-%d-%d", AlbianHost.ipToLong(AlbianHost.getLocalIp()),
					AlbianDateTime.getDateTimeNow().getTime(),
					jobid.getAndIncrement());
		} catch (UnknownHostException e) {
			return String.format("%d-%d", AlbianDateTime.getDateTimeNow()
					.getTime(), jobid.getAndIncrement());
		}
	}
}
