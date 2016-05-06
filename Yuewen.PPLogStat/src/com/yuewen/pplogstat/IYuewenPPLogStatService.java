package com.yuewen.pplogstat;

import org.albianj.service.IAlbianService;

public interface IYuewenPPLogStatService extends IAlbianService {

	final String Name = "YuewenPPLog";
	
	void log(String oppName, String timestamp, String callIp, String callName, String serviceIp, String serviceName,
			String ifName, int returnCode, boolean isSuccess, long useTime, boolean isTimeout);
}
