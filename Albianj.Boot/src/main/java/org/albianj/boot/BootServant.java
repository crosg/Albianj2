package org.albianj.boot;

import org.albianj.boot.entry.BootAttribute;
import org.albianj.boot.helpers.CollectServant;
import org.albianj.boot.helpers.OptConvertServant;
import org.albianj.boot.helpers.StringServant;
import org.albianj.boot.helpers.XmlParserContext;
import org.albianj.boot.logging.ILoggerAttribute;
import org.albianj.boot.logging.LogServant;
import org.albianj.boot.logging.impl.LoggerAttribute;
import org.albianj.boot.entry.BundleAttribute;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BootServant {

    public static BootServant Instance = null;

    static {
        if(null == Instance) {
            Instance = new BootServant();
        }
    }

    protected BootServant() {

    }

    public BootAttribute newBootAttribute(){
        BootAttribute bootAttribute = new BootAttribute();
        return bootAttribute;
    }

    public XmlParserContext loadBootConf(String sessionId, BundleContext rootBundleCtx){
        String bootXmlFilePath = rootBundleCtx.findConfigFile("boot.xml");
        XmlParserContext xmlParserCtx = XmlParserContext.makeXmlParserContext(sessionId, XmlParserContext.class,bootXmlFilePath);
        return xmlParserCtx;
    }

    public BootAttribute parserBootBundleConf(XmlParserContext xmlParserCtx, String logsPath){
        BootAttribute bootAttr = BootServant.Instance.newBootAttribute();
        String  mid = xmlParserCtx.findNodeValue("Boot/MachineId",true,false);
        String mkey = xmlParserCtx.findNodeValue("Boot/MachineKey",true,false);
        String appName = xmlParserCtx.findNodeValue("Boot/AppName",false,true);
        String runtimeLevel = xmlParserCtx.findNodeValue("Boot/RuntimeLevel",true,false);
        if(StringServant.Instance.isNotNullOrEmptyOrAllSpace(runtimeLevel)) {
            bootAttr.setRuntimeLevel(runtimeLevel);
        }
        if(StringServant.Instance.isNotNullOrEmptyOrAllSpace(appName)) {
            bootAttr.setAppName(appName);
        }
        if(StringServant.Instance.isNotNullOrEmptyOrAllSpace(mkey)) {
            bootAttr.setMachineKey(mkey);
        }
        if(StringServant.Instance.isNotNullOrEmptyOrAllSpace(mid)) {
            bootAttr.setMachineId(mid);
        }


        Node loggersNode = xmlParserCtx.selectNode("Boot/Logger",false);
        if(null == loggersNode){
            ILoggerAttribute logAttr = new LoggerAttribute("Runtime",logsPath,"DEBUG",true,"10MB");
            bootAttr.setRootLoggerAttr(logAttr);
        } else {
            ILoggerAttribute logAttr =  parserRootLoggerConf(xmlParserCtx,loggersNode,logsPath);
            bootAttr.setRootLoggerAttr(logAttr);
//            Map<String,ILoggerAttribute> logsAttr = parserLoggersConf(xmlParserCtx,loggersNode,logsPath);
//            bootAttr.setLoggerAttrs(logsAttr);
        }
        Map<String, BundleAttribute> bundles =  parserChildBundlesConf(xmlParserCtx);
        if(!CollectServant.Instance.isNullOrEmpty(bundles)){
            bootAttr.setBundleAttrs(bundles);
        }
        return bootAttr;
    }

//    private Map<String,ILoggerAttribute> parserLoggersConf(XmlParserContext xmlParserCtx,Node parent,String logsPath){
//        List<Node> nodes = xmlParserCtx.selectNodes(parent,"Logger",false);
//        Map<String,ILoggerAttribute> logsAttr = new HashMap<>();
//        if(!CollectServant.Instance.isNullOrEmpty(nodes)){
//            for(Node n : nodes){
//                ILoggerAttribute logAttr =  parserLoggerConf(xmlParserCtx, n,logsPath);
//                logsAttr.put(logAttr.getLoggerName(),logAttr);
//            }
//        }
//        return CollectServant.Instance.isNullOrEmpty(logsAttr) ? null : logsAttr;
//    }

    private ILoggerAttribute parserRootLoggerConf(XmlParserContext xmlParserCtx, Node logNode, String logsPath){
//        String rootPath = xmlParserCtx.findAttributeValue(logNode,"Path",true,false);
        String rootLevel = xmlParserCtx.findAttributeValue(logNode,"Level","INFO");
        String rootConsole = xmlParserCtx.findAttributeValue(logNode,"Console","true");
        String rootMaxFilesize = xmlParserCtx.findAttributeValue(logNode,"MaxFilesize","10MB");
        ILoggerAttribute logAttr = new LoggerAttribute("Runtime", logsPath,rootLevel,
                OptConvertServant.Instance.toBoolean(rootConsole,true),rootMaxFilesize);
        return logAttr;
    }

//    private ILoggerAttribute parserLoggerConf(XmlParserContext xmlParserCtx,Node logNode,String logsPath){
//        String name = xmlParserCtx.findAttributeValue(logNode,"Name",false,true);
////        String path = xmlParserCtx.findAttributeValue(logNode,"Path",true,false);
//        String level = xmlParserCtx.findAttributeValue(logNode,"Level","INFO");
//        String console = xmlParserCtx.findAttributeValue(logNode,"Console","true");
//        String maxFilesize = xmlParserCtx.findAttributeValue(logNode,"MaxFilesize","10MB");
//
//        ILoggerAttribute logAttr = new LoggerAttribute(name, logsPath,level,
//                OptConvertServant.Instance.toBoolean(console,true),maxFilesize);
//        return logAttr;
//    }

    private Map<String, BundleAttribute> parserChildBundlesConf(XmlParserContext xmlParserCtx){
        List<Node> bundles = xmlParserCtx.selectNodes("Boot/Bundles/Bundle",false);
        if(null == bundles || 0 == bundles.size()){

        }
        /**
         * key -> bundleName
         * value -> bundleAttr
         */
        Map<String, BundleAttribute> bundlesAttr = new HashMap<>();
        for(Node n : bundles){
            BundleAttribute bundleAttr =  parserChildBundlesConf(xmlParserCtx,n);
            bundlesAttr.put(bundleAttr.getName(),bundleAttr);
        }
        return bundlesAttr;
    }

    private BundleAttribute parserChildBundlesConf(XmlParserContext xmlParserCtx, Node bundleNode){
        String name = xmlParserCtx.findAttributeValue(bundleNode,"Name",false,true);
        String workPath = xmlParserCtx.findAttributeValue(bundleNode,"WorkPath",false,true);
        String startup = xmlParserCtx.findAttributeValue(bundleNode,"Startup",false,true);
        BundleAttribute bundleAttr = new BundleAttribute(name,workPath,startup);
        return bundleAttr;
    }

//    public void buildBootContext(BundleContext bootCtx,BootAttribute bootAttr){
//        ILoggerAttribute logAttr = bootAttr.getRootLoggerAttr();
//        LogServant.Instance.updateRuntimeLogger(logAttr.getLevel(),logAttr.isOpenConsole(),logAttr.getMaxFilesize());
//    }
public void repair(String sessionId,BundleContext bctx,String logsPath,Map<String, BundleAttribute> attAttrs){
    XmlParserContext confCtx =  loadBootConf(sessionId,bctx);
    BootAttribute attr = parserBootBundleConf(confCtx,logsPath);
    bctx.setAttr(attr);
    ILoggerAttribute logAttr = attr.getRootLoggerAttr();
    LogServant.Instance.updateRuntimeLogger(logAttr.getLevel(),logAttr.isOpenConsole(),logAttr.getMaxFilesize());
    Map<String, BundleAttribute>  confBundlesAttr = attr.getBundleAttrs();
    if(CollectServant.Instance.isNullOrEmpty(confBundlesAttr)) {
        attAttrs.putAll(confBundlesAttr);
    }
}

}
