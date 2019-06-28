package org.albianj.kernel.impl;

import org.albianj.boot.BundleContext;
import org.albianj.boot.entry.BootAttribute;
import org.albianj.loader.AlbianBootContext;
import org.albianj.loader.entry.AlbianBundleModuleKeyValueConf;
import org.albianj.loader.entry.IAlbianBundleLoggerAttribute;
import org.albianj.logger.IBundleLoggerService;
import org.albianj.logger.impl.bundle.AlbianBundleLoggerAttribute;
import org.albianj.logger.impl.bundle.FreeLoggerParserService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.io.*;
import java.math.BigInteger;

/**
 * 第一个启动的workpath中的boot。xml即为整个进程的root boot。
 * 后面再加载的bundle中的boot.xml都将会被忽略
 * 此举是为了可以统一启动bundle，而不用因为前后而使用不一样的方法
 */
public class BootParserService extends FreeLoggerParserService {
    private String file = "boot.xml";

    private String[] defLogs = {
            IBundleLoggerService.LogName4Runtime,
            IBundleLoggerService.LogName4State,
            IBundleLoggerService.LogName4Monitor
    };

    public void loadConf() {
        try {
           BootAttribute bootAttr =  AlbianBootContext.Instance.findBootAttr();
           String bootXmlFilePath = findConfigFile(file);
           Document bootXml =  XmlParser.load(bootXmlFilePath);
           String bundleName = AlbianBootContext.Instance.getCurrentBundleContext().getBundleName();
           parser(bootXmlFilePath,bootXml,bootAttr,bundleName);

        } catch (Exception e) {
            AlbianServiceRouter.throwEnterExceptionV2("StartupThread",AlbianServiceRouter.LoggerRunning,
                    AlbianServiceRouter.Error,e,"Startup boot ie error.",
                    "startup kernel boot by parser boot.xml in config path -> ",
                    this.getBundleContext().getConfPath(),"is fail.");
        }
    }

    private void parser(String bootXmlFilePath, Document bootXml, BootAttribute bootAttr, String bundleName ) {

        Node e = XmlParser.selectNode(bootXml,"Boot/MachineId");
        String sMid = XmlParser.getNodeValue((Element) e);
        if(Validate.isNullOrEmptyOrAllSpace(sMid)) {
            BundleContext bundleContext = AlbianBootContext.Instance.findBundleContext(bundleName,true);
            sMid = loadMachineOrNewIfNotExist(bundleContext.getWorkPath());
        }
        bootAttr.setMachineId(sMid);

        e = XmlParser.selectNode(bootXml,"Boot/MachineKey");
        String sKey = XmlParser.getNodeValue((Element) e);
        if(!Validate.isNullOrEmptyOrAllSpace(sMid)) {
            bootAttr.setMachineKey(sKey);
        }
        e = XmlParser.selectNode(bootXml,"Boot/AppName");
        String sAppName = XmlParser.getNodeValue((Element) e);
        if(!Validate.isNullOrEmptyOrAllSpace(sAppName)) {
            bootAttr.setAppName(sAppName);
        }
        e = XmlParser.selectNode(bootXml,"Boot/RuntimeLevel");
        String sRuntimeLevel = XmlParser.getNodeValue((Element) e);
        if(!Validate.isNullOrEmptyOrAllSpace(sRuntimeLevel)) {
            bootAttr.setRuntimeLevel(sRuntimeLevel);
        }
        e =  XmlParser.selectNode(bootXml,"Boot/Loggers");
        AlbianBundleModuleKeyValueConf sysLogsAttr = parserLoggers(bootXmlFilePath,bootAttr,(Element) e);
        sysLogsAttr = checkAndMakeupDefault(bootAttr,sysLogsAttr);
        bootAttr.setLoggerAttrs(sysLogsAttr);
    }

    private AlbianBundleModuleKeyValueConf checkAndMakeupDefault(BootAttribute bootAttr, AlbianBundleModuleKeyValueConf map) {
        for(String defLogName : defLogs) {
            if(!map.containsKey(defLogName)) {
                IAlbianBundleLoggerAttribute loggerAttr = new AlbianBundleLoggerAttribute();
                loggerAttr.setLoggerName(defLogName);
                if(bootAttr.isWindows()) {
                    loggerAttr.setPath(logPathInWin);
                } else {
                    loggerAttr.setPath(logPathInLinux);
                }
                map.put(defLogName,loggerAttr);
                AlbianServiceRouter.addLogV2("Satrtup",AlbianServiceRouter.LoggerRunning,
                        AlbianServiceRouter.Info,null,"Set default Logger.",
                        "logger -> ",defLogName," use default logpath -> ",loggerAttr.getPath());

            }
        }
        return map;
    }



    private String loadMachineOrNewIfNotExist(String workpath) {
        String idFileName = workpath + "host" + File.separator + "mach.id";
        File f = new File(idFileName);
        BufferedReader br = null;
        FileWriter fw = null;
        try {
            if (f.exists()) {
                br = new BufferedReader(new FileReader(idFileName));
                String line = br.readLine();
                if (!Validate.isNullOrEmptyOrAllSpace(line)) {
                    return line.trim();
                }
            } else {
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdir();
                }
                BigInteger id = FinalAlbianIdService.Instance.genId();
                f.createNewFile();
                fw = new FileWriter(f);
                fw.write(String.valueOf(id));
                fw.flush();
                return id.toString();
            }
        } catch (Exception e) {

        }finally {
            if(null != br){
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
            if(null != fw){
                try {
                    fw.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
