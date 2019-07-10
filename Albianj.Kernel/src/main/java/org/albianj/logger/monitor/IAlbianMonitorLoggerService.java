package org.albianj.logger.monitor;

import org.albianj.service.AlbianBuiltinServiceNamePair;
import org.albianj.service.IAlbianService;

public interface IAlbianMonitorLoggerService extends IAlbianService {
    String Name =  AlbianBuiltinServiceNamePair.AlbianMonitorLoggerServiceName;

    void addMonitorLog(String sessionId,AlbianMonitorData data);

}
