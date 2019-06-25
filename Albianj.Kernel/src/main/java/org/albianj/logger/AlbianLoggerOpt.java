package org.albianj.logger;

import org.albianj.datetime.AlbianDateTime;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.text.StringHelper;
import org.albianj.verify.Validate;
import org.apache.log4j.Level;

import java.util.HashMap;
import java.util.Map;

public class AlbianLoggerOpt {

    public static  AlbianLoggerOpt Instance;
    static {
        Instance = new AlbianLoggerOpt();
    }

    private static String logFmtV2 = "{time} {level} Bundle:{bundleName} SessionId:{sessionId} Thread:{tid} Brief:{brief} CallChain:[{chain}] Except:[Type:{type} Msg:{showMsg}] Ctx:[{ctx}]";
    private static String logInterFmtV2 = "{time} {level} Bundle:{bundleName} SessionId:{sessionId} Thread:{tid} Brief:{brief} CallChain:[{chain}] Except:[Type:{type} InterMsg:[{intermsg}] Msg:{showMsg}] Ctx:[{ctx}]";

    public void logMsg(String logName, AlbianLoggerLevel level, Throwable excp, String msg) {
        IAlbianLoggerService2 log = AlbianServiceRouter.getSingletonService(IAlbianLoggerService2.class, IAlbianLoggerService2.Name, false);
        IAlbianBundleLoggerService rlogServ = AlbianServiceRouter.getSingletonService(IAlbianBundleLoggerService.class, IAlbianBundleLoggerService.Name, false);
        if((null != log) && log.isExistLogger(logName)) {
            log.log3(logName, level, msg);
        } else if(null != rlogServ) { // 业务log不在的话，直接记录到root的 runtime日志
            rlogServ.addLog(IAlbianBundleLoggerService.LogName4Runtime,level,excp,msg);
        } else { //业务和root的log都没有实例化，那么只有console日志了
            FinalAlbianBundleRootLoggerService.Instance.addLog(logName,level,excp,msg);
        }
    }

    public String buildMsg(String sessionId,
                                  String bundleName,
                                  AlbianLoggerLevel level,
                                  StackTraceElement[] stes,
                                  String brief,
                                  Throwable excp,
                                  String interMsg,
                                  Object[] info) {
        int count = stes.length >= 6 ? 6 : stes.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            StackTraceElement ste = stes[i];
            sb.append(ste.getFileName())
                    .append("$").append(ste.getMethodName())
                    .append("$").append(ste.getLineNumber())
                    .append(" -> ");
        }
        if (0 != sb.length()) {
            sb.delete(sb.length() - 4, sb.length() - 1);
        }

        String mInfo = null;
        if (null != info && 0 != info.length) {
            mInfo = StringHelper.join(info);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("bundleName",bundleName);
        map.put("time", AlbianDateTime.fmtCurrentLongDatetime());
        map.put("level", level.getTag());
        map.put("sessionId", sessionId);
        map.put("tid", Thread.currentThread().getId());
        map.put("chain", sb);
        map.put("brief", brief);
        map.put("type", null == excp ? "" : excp.getClass().getName());
        map.put("msg", null == excp ? brief : excp.getMessage());
        map.put("ctx", null == mInfo ? "" : mInfo);
        if(!Validate.isNullOrEmptyOrAllSpace(interMsg)){
            map.put("intermsg",null == interMsg ? "" : interMsg );
            return StringHelper.formatTemplate(logInterFmtV2, map);
        }
        return StringHelper.formatTemplate(logFmtV2, map);
    }

    public Level toLevel(AlbianLoggerLevel loggerLevel){
        return loggerLevel == AlbianLoggerLevel.Debug ? Level.DEBUG
                : loggerLevel == AlbianLoggerLevel.Error ? Level.ERROR
                : loggerLevel == AlbianLoggerLevel.Info ? Level.INFO
                : loggerLevel == AlbianLoggerLevel.Warn ? Level.WARN
                : Level.ALL;
    }
}
