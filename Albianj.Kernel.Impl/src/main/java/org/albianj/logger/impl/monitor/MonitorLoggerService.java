package org.albianj.logger.impl.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.ILoggerService2;
import org.albianj.logger.monitor.AlbianMonitorData;
import org.albianj.logger.monitor.IMonitorLoggerService;
import org.albianj.service.ServiceTag;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.FreeService;
import org.albianj.service.parser.AlbianParserException;
import org.slf4j.Logger;

@ServiceTag(Id = IMonitorLoggerService.Name, Interface = IMonitorLoggerService.class)
public class MonitorLoggerService extends FreeService implements IMonitorLoggerService {

    private Logger mlog = null;
    private String logName = "AlbianMonitorLogger";


    @Override
    public void loading() throws AlbianParserException {
        mlog = getLogger(logName);
        if(null == mlog) {
            AlbianServiceRouter.addLog("startup thread", ILoggerService2.AlbianRunningLoggerName,
                    AlbianLoggerLevel.Mark,"loadConf monitor addLog service is fail.this service is unuseful.");
        }

        super.loading();
    }

    public Logger getLogger(String name) {
        return AlbianServiceRouter.getLogger().getLogger(name);
    }

    public void addMonitorLog(String sessionId,AlbianMonitorData data) {
       String json =  JSON.toJSONString(data,
                SerializerFeature.SkipTransientField,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect);
        try {
            mlog.info(json);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
