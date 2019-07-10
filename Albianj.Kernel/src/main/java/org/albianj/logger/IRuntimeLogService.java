package org.albianj.logger;

import org.albianj.service.IAlbianService;

public interface IRuntimeLogService extends IAlbianService {
    void addLog(String sessionId, RuntimeLogType logType, Class<?> type, String fmt, Object[]... args);

    void addLog(String sessionId, RuntimeLogType logType, Class<?> type, Throwable t, String fmt, Object[]... args);
}
