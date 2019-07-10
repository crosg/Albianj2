package com.yuewen.pplogstat.impl;

import com.qq.monitor.logstat.PPLogStatApi;
import org.albianj.kernel.KernelSetting;
import org.albianj.service.AlbianServiceRant;
import org.albianj.service.FreeAlbianService;
import org.albianj.service.parser.AlbianParserException;

@AlbianServiceRant(Id = IYuewenPPLogStatService.Name, Interface = IYuewenPPLogStatService.class)
public class YuewenPPLogStatService extends FreeAlbianService implements IYuewenPPLogStatService {

    public String getServiceName() {
        return Name;
    }

    @Override
    public void init() throws AlbianParserException {
        // TODO Auto-generated method stub
        PPLogStatApi.setAppName(KernelSetting.getAppName());
        super.init();
    }

    @Override
    public void log(String oppName, String timestamp, String callIp, String callName, String serviceIp,
                    String serviceName, String ifName, int returnCode, boolean isSuccess, long useTime, boolean isTimeout) {

        PPLogStatApi.log(oppName,
                new String[]{timestamp, callIp, callName, serviceIp, serviceName, ifName, String.valueOf(returnCode)},
                new float[]{isSuccess ? 1 : 0, useTime, isTimeout ? 1 : 0, 1});

    }

}
