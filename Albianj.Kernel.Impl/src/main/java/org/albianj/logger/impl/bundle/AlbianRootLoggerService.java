package org.albianj.logger.impl.bundle;

import org.albianj.loader.AlbianBootContext;
import org.albianj.boot.entry.AlbianBootAttribute;
import org.albianj.loader.entry.IAlbianBundleLoggerAttribute;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.FinalAlbianBundleRootLoggerService;
import org.albianj.logger.IAlbianBundleLoggerService;
import org.albianj.logger.AlbianLoggerOpt;
import org.albianj.service.FreeAlbianService;
import org.albianj.service.parser.AlbianParserException;
import org.apache.log4j.*;
import org.apache.log4j.helpers.OptionConverter;

import java.util.HashMap;
import java.util.Map;

import static org.apache.log4j.Level.*;

public class AlbianRootLoggerService extends FreeAlbianService implements IAlbianBundleLoggerService {
    private Map<String,Logger> loggerRepos;
    @Override
    public void init() throws AlbianParserException {
        loggerRepos = new HashMap<>();
        makeRootLoggers();
        super.init();
    }

    private void makeRootLoggers(){
        AlbianBootAttribute bootAttr =  AlbianBootContext.Instance.findBootAttr();
        Map<String, IAlbianBundleLoggerAttribute> rlogsAttr =  bootAttr.getLoggerAttrs();
        for(Map.Entry<String, IAlbianBundleLoggerAttribute> rlog : rlogsAttr.entrySet()){
            IAlbianBundleLoggerAttribute attr =  rlog.getValue();
            makeRootLogger(bootAttr.getAppName(),attr,5);
        }
    }

    private void makeRootLogger(String appName, IAlbianBundleLoggerAttribute logAttr, int backupIndex){
        Logger logRoot =  LogManager.getRootLogger();
        Logger logNode = logRoot.getLoggerRepository().getLogger(logAttr.getLoggerName());

        AlbianBundleLoggerAppender appender =  new AlbianBundleLoggerAppender(logAttr.getPath(),appName,logAttr.getLoggerName(),logAttr.getMaxFilesize(),backupIndex);
        Level level = OptionConverter.toLevel(logAttr.getLevel(), INFO);
        appender.setThreshold(level);
        appender.setAppend(true);
        appender.setLayout(new PatternLayout("%m%n"));
        appender.activateOptions();
        logNode.addAppender(appender);

        if(logAttr.isOpenConsole()) {
            ConsoleAppender console = new ConsoleAppender();
            console.setThreshold(level);
            console.setLayout(new PatternLayout("%m%n"));
            console.activateOptions();
            logNode.addAppender(console);
        }
        loggerRepos.put(logAttr.getLoggerName(),logNode);
    }

    public  void addLog(String logName, AlbianLoggerLevel level, Throwable excp, String msg){
        Logger logger =  loggerRepos.get(logName);
        if(null != logger) {
            logger.log(AlbianLoggerOpt.Instance.toLevel(level), msg, excp);
        } else {
            FinalAlbianBundleRootLoggerService.Instance.addLog(logName,level, excp, msg);
        }
    }
}
