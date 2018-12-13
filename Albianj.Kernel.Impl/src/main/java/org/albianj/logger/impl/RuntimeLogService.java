package org.albianj.logger.impl;

import org.albianj.logger.IRuntimeLogService;
import org.albianj.logger.RuntimeLogType;
import org.albianj.service.FreeAlbianService;

public class RuntimeLogService extends FreeAlbianService implements IRuntimeLogService {
    @Override
    public void addLog(String sessionId, RuntimeLogType logType, Class<?> type, String fmt, Object[]... args) {
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        int count = stes.length >= 6 ? 6 : stes.length;
        StringBuilder sb = new StringBuilder("Call ");
        for (int i = 1; i < count; i++) {
            StackTraceElement ste = stes[i];
            sb.append("ST:").append(ste.getFileName())
                    .append(" M:").append(ste.getMethodName())
                    .append(" L:").append(ste.getLineNumber())
                    .append(",");
        }
        if (0 != sb.length()) {
            sb.deleteCharAt(sb.length() - 1);
        }

    }

    @Override
    public void addLog(String sessionId, RuntimeLogType logType, Class<?> type, Throwable t, String fmt, Object[]... args) {
        StackTraceElement[] stes = t.getStackTrace();
        int count = stes.length >= 5 ? 5 : stes.length;
        StringBuilder sb = new StringBuilder("Call ");
        for (int i = 0; i < count; i++) {
            StackTraceElement ste = stes[i];
            sb.append("ST:").append(ste.getFileName())
                    .append(" M:").append(ste.getMethodName())
                    .append(" L:").append(ste.getLineNumber())
                    .append(",");
        }
        if (0 != sb.length()) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}
