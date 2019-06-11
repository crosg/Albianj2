package org.albianj.logger;

import org.apache.log4j.*;

public class FinalAlbianBundleRootLoggerService implements IAlbianBundleLogger {

    public static IAlbianBundleLogger Instance = null;
    private Logger logger = null;
    static {
        Instance = new FinalAlbianBundleRootLoggerService();
    }

    private FinalAlbianBundleRootLoggerService(){
        logger = LogManager.getRootLogger().getLoggerRepository().getLogger("SysDefault");
        ConsoleAppender console = new ConsoleAppender();
        console.setThreshold(Level.DEBUG);
        console.setLayout(new PatternLayout("%m%n"));
        console.activateOptions();
        logger.addAppender(console);
    }


    @Override
    public  void addLog(String logName, AlbianLoggerLevel level, Throwable excp, String msg){
            logger.log(AlbianLoggerOpt.Instance.toLevel(level),msg,excp);

    }
}
