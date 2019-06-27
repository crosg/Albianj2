package org.albianj.logger.monitor;

import org.albianj.service.BuiltinNames;
import org.albianj.service.IService;

public interface IMonitorLoggerService extends IService {
    String Name =  BuiltinNames.AlbianMonitorLoggerServiceName;

    void addMonitorLog(String sessionId,AlbianMonitorData data);

}
