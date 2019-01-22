package org.albianj.logger.impl.monitor;

import org.albianj.logger.monitor.IAlbianMonitorData;
import org.albianj.logger.monitor.IAlbianMonitorLoggerService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.FreeAlbianService;
import org.albianj.service.parser.AlbianParserException;
import org.slf4j.Logger;

public class AlbianMonitorLoggerService extends FreeAlbianService implements IAlbianMonitorLoggerService {

    private Logger mLog = null;
    private String logName = "AlbianMonitorLogger";

    @Override
    public void init() throws AlbianParserException {
        mLog = AlbianServiceRouter.getLogger().getLogger(logName);
        if(null == mLog) {
//            throw new AlbianParserException(ExceptionUtil.ExceptForError,"Not Found Logger.",
//                    StringHelper.join("not found logger -> ",logName));
        }

        super.init();
    }

    @Override
    public void addMonitorLog(IAlbianMonitorData data) {

    }
}
