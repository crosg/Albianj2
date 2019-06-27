package com.yuewen.pplogstat.impl;

import org.albianj.service.BuiltinNames;
import org.albianj.service.IService;

public interface IYuewenPPLogStatService extends IService {

    final String Name = BuiltinNames.YuewenPPLogServiceName;

    void log(String oppName, String timestamp, String callIp, String callName, String serviceIp, String serviceName,
             String ifName, int returnCode, boolean isSuccess, long useTime, boolean isTimeout);
}
