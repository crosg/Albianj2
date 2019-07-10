package org.albianj.logger.impl.monitor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.logger.monitor.AlbianMonitorData;
import org.albianj.logger.monitor.IAlbianMonitorLoggerService;
import org.albianj.service.AlbianServiceRant;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.FreeAlbianService;
import org.albianj.service.parser.AlbianParserException;
import org.slf4j.Logger;

@AlbianServiceRant(Id = IAlbianMonitorLoggerService.Name, Interface = IAlbianMonitorLoggerService.class)
public class AlbianMonitorLoggerService extends FreeAlbianService implements IAlbianMonitorLoggerService {

    private Logger mlog = null;
    private String logName = "AlbianMonitorLogger";


    @Override
    public void loading() throws AlbianParserException {
        mlog = getLogger(logName);
        if(null == mlog) {
            AlbianServiceRouter.addLog("startup thread", IAlbianLoggerService2.AlbianRunningLoggerName,
                    AlbianLoggerLevel.Mark,"init monitor log service is fail.this service is unuseful.");
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
