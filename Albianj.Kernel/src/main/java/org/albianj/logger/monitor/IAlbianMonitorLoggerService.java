package org.albianj.logger.monitor;

import org.albianj.service.IAlbianService;

public interface IAlbianMonitorLoggerService extends IAlbianService {
    
    void addMonitorLog(IAlbianMonitorData data);

}
