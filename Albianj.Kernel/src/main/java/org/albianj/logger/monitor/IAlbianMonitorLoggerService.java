package org.albianj.logger.monitor;

import org.albianj.service.AlbianBuiltinNames;
import org.albianj.service.IAlbianService;

public interface IAlbianMonitorLoggerService extends IAlbianService {
    String Name =  AlbianBuiltinNames.AlbianMonitorLoggerServiceName;

    void addMonitorLog(String sessionId,AlbianMonitorData data);

}
