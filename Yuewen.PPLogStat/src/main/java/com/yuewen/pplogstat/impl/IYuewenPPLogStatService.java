package com.yuewen.pplogstat.impl;

import org.albianj.service.AlbianBuiltinServiceNamePair;
import org.albianj.service.IAlbianService;

public interface IYuewenPPLogStatService extends IAlbianService {

    final String Name = AlbianBuiltinServiceNamePair.YuewenPPLogServiceName;

    void log(String oppName, String timestamp, String callIp, String callName, String serviceIp, String serviceName,
             String ifName, int returnCode, boolean isSuccess, long useTime, boolean isTimeout);
}
