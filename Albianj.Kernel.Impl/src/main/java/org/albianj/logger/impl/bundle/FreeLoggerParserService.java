package org.albianj.logger.impl.bundle;

import org.albianj.boot.except.entry.BootAttribute;
import org.albianj.loader.entry.AlbianBundleModuleKeyValueConf;
import org.albianj.loader.entry.IAlbianBundleLoggerAttribute;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.FreeParserService;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Element;

import java.util.List;

public class FreeLoggerParserService extends FreeParserService {
    protected String logPathInWin = "c:\\logs\\";
    protected String logPathInLinux = "/data/logs/JAVA/";

    protected AlbianBundleModuleKeyValueConf parserLoggers(String filename, BootAttribute bootAttr, Element node) {
        List nodes = XmlParser.getChildNodes(node, "Logger");
        AlbianBundleModuleKeyValueConf map = new AlbianBundleModuleKeyValueConf();
        for (Object e : nodes) {
            IAlbianBundleLoggerAttribute loggerAttr = parserLoggerOrGetDefault(filename,bootAttr, (Element) e);
            map.put(loggerAttr.getLoggerName(), loggerAttr);
        }
        return map;
    }

    protected IAlbianBundleLoggerAttribute parserLoggerOrGetDefault(String filename, BootAttribute bootAttr, Element node){
        IAlbianBundleLoggerAttribute loggerAttr = new AlbianBundleLoggerAttribute();
        String name = XmlParser.getValueByAttrOrChileNode(node,"Name");
        if(Validate.isNullOrEmptyOrAllSpace(name)){
            AlbianServiceRouter.throwEnterExceptionV2("StartupThread",AlbianServiceRouter.LoggerRunning,
                    AlbianServiceRouter.Error,null,"SysLogger Name is NullOrEmpty.",
                    "SysLogger Name is nullOrEmpty,but it must exist.please check -> ",
                    filename," in config path -> ",this.getBundleContext().getConfPath());
        }
        loggerAttr.setLoggerName(name);

        String path = XmlParser.getValueByAttrOrChileNode(node,"Path");
        if(Validate.isNullOrEmptyOrAllSpace(name)) {
            if(bootAttr.isWindows()) {
                loggerAttr.setPath(logPathInWin);
                AlbianServiceRouter.addLogV2("Satrtup",AlbianServiceRouter.LoggerRunning,
                        AlbianServiceRouter.Info,null,"Set default path in Win.",
                        "logger -> ",name," use default logpath -> ",logPathInWin," in Windows");
            } else {
                loggerAttr.setPath(logPathInLinux);
                AlbianServiceRouter.addLogV2("Satrtup",AlbianServiceRouter.LoggerRunning,
                        AlbianServiceRouter.Info,null,"Set default path in Linux.",
                        "logger -> ",name," use default logpath -> ",logPathInLinux," in Not Windows");
            }
        } else {
            loggerAttr.setPath(path);
        }

        String level = XmlParser.getValueByAttrOrChileNode(node,"Level");
        if(Validate.isNullOrEmptyOrAllSpace(level)) {
            AlbianServiceRouter.addLogV2("Satrtup",AlbianServiceRouter.LoggerRunning,
                    AlbianServiceRouter.Info,null,"Set default level.",
                    "logger -> ",name," use default level -> ",loggerAttr.getLevel());
        } else {
            loggerAttr.setLevel(level);
        }

        String console = XmlParser.getValueByAttrOrChileNode(node,"Console");
        if(Validate.isNullOrEmptyOrAllSpace(console)) {
            AlbianServiceRouter.addLogV2("Satrtup",AlbianServiceRouter.LoggerRunning,
                    AlbianServiceRouter.Info,null,"Set default path.",
                    "logger -> ",name," use default close console");
        } else {
            loggerAttr.setOpenConsole(Boolean.valueOf(console.trim()));
        }

        String maxFilesize = XmlParser.getValueByAttrOrChileNode(node,"MaxFileSize");
        if(Validate.isNullOrEmptyOrAllSpace(level)) {
            AlbianServiceRouter.addLogV2("Satrtup",AlbianServiceRouter.LoggerRunning,
                    AlbianServiceRouter.Info,null,"Set default path in Win.",
                    "logger -> ",name," use default MaxFileSize -> ",loggerAttr.getMaxFilesize());
        } else {
            loggerAttr.setMaxFilesize(maxFilesize);
        }
        return loggerAttr;
    }
}
