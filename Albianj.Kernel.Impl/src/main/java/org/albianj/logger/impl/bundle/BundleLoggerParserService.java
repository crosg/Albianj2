package org.albianj.logger.impl.bundle;

import org.albianj.boot.except.entry.BootAttribute;
import org.albianj.loader.AlbianBootContext;
import org.albianj.loader.entry.AlbianBundleModuleKeyValueConf;
import org.albianj.service.BuiltinNames;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.xml.XmlParser;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * 因为log4j无法在一个jvm中多次同name的加载，故当使用多bundle
 * 的时候，推荐使用自定义的logger配置文件logger.xml来做配置
 * 当程序中只有一个bundle或者只有一个使用log4j的bundle时，不受影响
 */
public class BundleLoggerParserService extends FreeLoggerParserService {
    private String file = "logger.xml";
    public void loadConf() {
        try {
            String loggerConfpath = findConfigFile(file);
            Document loggerConfXML =  XmlParser.load(loggerConfpath);
            BootAttribute bootAttr =  AlbianBootContext.Instance.findBootAttr();
            AlbianBundleModuleKeyValueConf logConf = parser(loggerConfpath,loggerConfXML,bootAttr);
            this.getBundleContext().addModuleConf(BuiltinNames.Conf.Logger,logConf);
        } catch (Exception e) {
            AlbianServiceRouter.throwEnterExceptionV2("StartupThread",AlbianServiceRouter.LoggerRunning,
                    AlbianServiceRouter.Error,e,"Startup boot ie error.",
                    "startup kernel boot by parser boot.xml in config path -> ",
                    this.getBundleContext().getConfPath(),"is fail.");
        }
    }

    private AlbianBundleModuleKeyValueConf parser(String loggerConfpath, Document bootXml, BootAttribute bootAttr ) {
        Node e =  XmlParser.selectNode(bootXml,"Loggers");
        AlbianBundleModuleKeyValueConf sysLogsAttr = parserLoggers(loggerConfpath,bootAttr,(Element) e);
        return sysLogsAttr;
    }

}
