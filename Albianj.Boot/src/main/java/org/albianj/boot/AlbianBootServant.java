package org.albianj.boot;

import org.albianj.boot.helpers.AlbianCollectServant;
import org.albianj.boot.helpers.AlbianOptConvertServant;
import org.albianj.boot.helpers.AlbianStringServant;
import org.albianj.boot.helpers.AlbianXmlParserContext;
import org.albianj.boot.logging.IAlbianLoggerAttribute;
import org.albianj.boot.logging.impl.AlbianLoggerAttribute;
import org.albianj.boot.entry.AlbianBootAttribute;
import org.albianj.boot.entry.AlbianBundleAttribute;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbianBootServant {

    public static AlbianBootServant Instance = null;

    static {
        if(null == Instance) {
            Instance = new AlbianBootServant();
        }
    }

    protected AlbianBootServant() {

    }

    public AlbianBootAttribute newBootAttribute(){
        AlbianBootAttribute bootAttribute = new AlbianBootAttribute();
        return bootAttribute;
    }

    public AlbianXmlParserContext loadBootConf(String sessionId, AlbianBundleContext rootBundleCtx){
        String bootXmlFilePath = rootBundleCtx.findConfigFile("boot.xml");
        AlbianXmlParserContext xmlParserCtx = AlbianXmlParserContext.makeXmlParserContext(sessionId,AlbianXmlParserContext.class,bootXmlFilePath);
        return xmlParserCtx;
    }

    public AlbianBootAttribute parserBootBundleConf(AlbianXmlParserContext xmlParserCtx, String logsPath){
        AlbianBootAttribute bootAttr = AlbianBootServant.Instance.newBootAttribute();
        String  mid = xmlParserCtx.findNodeValue("Boot/MachineId",true,false);
        String mkey = xmlParserCtx.findNodeValue("Boot/MachineKey",true,false);
        String appName = xmlParserCtx.findNodeValue("Boot/AppName",false,true);
        String runtimeLevel = xmlParserCtx.findNodeValue("Boot/RuntimeLevel",true,false);
        if(AlbianStringServant.Instance.isNotNullOrEmptyOrAllSpace(runtimeLevel)) {
            bootAttr.setRuntimeLevel(runtimeLevel);
        }
        if(AlbianStringServant.Instance.isNotNullOrEmptyOrAllSpace(appName)) {
            bootAttr.setAppName(appName);
        }
        if(AlbianStringServant.Instance.isNotNullOrEmptyOrAllSpace(mkey)) {
            bootAttr.setMachineKey(mkey);
        }
        if(AlbianStringServant.Instance.isNotNullOrEmptyOrAllSpace(mid)) {
            bootAttr.setMachineId(mid);
        }


        Node loggersNode = xmlParserCtx.selectNode("Boot/Logger",false);
        if(null == loggersNode){
            IAlbianLoggerAttribute logAttr = new AlbianLoggerAttribute("Runtime",logsPath,"DEBUG",true,"10MB");
            bootAttr.setRootLoggerAttr(logAttr);
        } else {
            IAlbianLoggerAttribute logAttr =  parserRootLoggerConf(xmlParserCtx,loggersNode,logsPath);
            bootAttr.setRootLoggerAttr(logAttr);
//            Map<String,IAlbianLoggerAttribute> logsAttr = parserLoggersConf(xmlParserCtx,loggersNode,logsPath);
//            bootAttr.setLoggerAttrs(logsAttr);
        }
        Map<String,AlbianBundleAttribute> bundles =  parserChildBundlesConf(xmlParserCtx);
        if(!AlbianCollectServant.Instance.isNullOrEmpty(bundles)){
            bootAttr.setBundleAttrs(bundles);
        }
        return bootAttr;
    }

//    private Map<String,IAlbianLoggerAttribute> parserLoggersConf(AlbianXmlParserContext xmlParserCtx,Node parent,String logsPath){
//        List<Node> nodes = xmlParserCtx.selectNodes(parent,"Logger",false);
//        Map<String,IAlbianLoggerAttribute> logsAttr = new HashMap<>();
//        if(!AlbianCollectServant.Instance.isNullOrEmpty(nodes)){
//            for(Node n : nodes){
//                IAlbianLoggerAttribute logAttr =  parserLoggerConf(xmlParserCtx, n,logsPath);
//                logsAttr.put(logAttr.getLoggerName(),logAttr);
//            }
//        }
//        return AlbianCollectServant.Instance.isNullOrEmpty(logsAttr) ? null : logsAttr;
//    }

    private IAlbianLoggerAttribute parserRootLoggerConf(AlbianXmlParserContext xmlParserCtx,Node logNode,String logsPath){
//        String rootPath = xmlParserCtx.findAttributeValue(logNode,"Path",true,false);
        String rootLevel = xmlParserCtx.findAttributeValue(logNode,"Level","INFO");
        String rootConsole = xmlParserCtx.findAttributeValue(logNode,"Console","true");
        String rootMaxFilesize = xmlParserCtx.findAttributeValue(logNode,"MaxFilesize","10MB");
        IAlbianLoggerAttribute logAttr = new AlbianLoggerAttribute("Runtime", logsPath,rootLevel,
                AlbianOptConvertServant.Instance.toBoolean(rootConsole,true),rootMaxFilesize);
        return logAttr;
    }

//    private IAlbianLoggerAttribute parserLoggerConf(AlbianXmlParserContext xmlParserCtx,Node logNode,String logsPath){
//        String name = xmlParserCtx.findAttributeValue(logNode,"Name",false,true);
////        String path = xmlParserCtx.findAttributeValue(logNode,"Path",true,false);
//        String level = xmlParserCtx.findAttributeValue(logNode,"Level","INFO");
//        String console = xmlParserCtx.findAttributeValue(logNode,"Console","true");
//        String maxFilesize = xmlParserCtx.findAttributeValue(logNode,"MaxFilesize","10MB");
//
//        IAlbianLoggerAttribute logAttr = new AlbianLoggerAttribute(name, logsPath,level,
//                AlbianOptConvertServant.Instance.toBoolean(console,true),maxFilesize);
//        return logAttr;
//    }

    private Map<String,AlbianBundleAttribute> parserChildBundlesConf(AlbianXmlParserContext xmlParserCtx){
        List<Node> bundles = xmlParserCtx.selectNodes("Boot/Bundles/Bundle",false);
        if(null == bundles || 0 == bundles.size()){

        }
        /**
         * key -> bundleName
         * value -> bundleAttr
         */
        Map<String,AlbianBundleAttribute> bundlesAttr = new HashMap<>();
        for(Node n : bundles){
            AlbianBundleAttribute bundleAttr =  parserChildBundlesConf(xmlParserCtx,n);
            bundlesAttr.put(bundleAttr.getName(),bundleAttr);
        }
        return bundlesAttr;
    }

    private AlbianBundleAttribute parserChildBundlesConf(AlbianXmlParserContext xmlParserCtx, Node bundleNode){
        String name = xmlParserCtx.findAttributeValue(bundleNode,"Name",false,true);
        String workPath = xmlParserCtx.findAttributeValue(bundleNode,"WorkPath",false,true);
        String startup = xmlParserCtx.findAttributeValue(bundleNode,"Startup",false,true);
        AlbianBundleAttribute bundleAttr = new AlbianBundleAttribute(name,workPath,startup);
        return bundleAttr;
    }

//    public void buildBootContext(AlbianBundleContext bootCtx,AlbianBootAttribute bootAttr){
//        IAlbianLoggerAttribute logAttr = bootAttr.getRootLoggerAttr();
//        AlbianLogServant.Instance.updateRuntimeLogger(logAttr.getLevel(),logAttr.isOpenConsole(),logAttr.getMaxFilesize());
//    }


}
