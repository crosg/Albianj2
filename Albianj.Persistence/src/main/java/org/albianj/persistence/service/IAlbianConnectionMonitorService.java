package org.albianj.persistence.service;

import org.albianj.service.IAlbianService;

/**
 * project : com.yuewen.nrzx.albianj
 * 监控获取Connection的时间
 * liyuqi 2018-07-31 10:45</br>
 */
public interface IAlbianConnectionMonitorService extends IAlbianService {
    String Name = "AlbianConnectionMonitorService";

    void monitorConnectionCost(final String storageName, final String database, final MonitorMethod method, long cost, boolean success);

    enum MonitorMethod {
        GetConnection, ReturnConnection
    }
}
