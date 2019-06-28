package org.albianj.bundle.impl;

import org.albianj.boot.BundleContext;
import org.albianj.boot.entry.BootAttribute;
import org.albianj.boot.helpers.OptConvertServant;
import org.albianj.boot.helpers.StringServant;
import org.albianj.boot.helpers.XmlParserContext;
import org.albianj.boot.logging.ILoggerAttribute;
import org.albianj.boot.logging.LogServant;
import org.albianj.boot.logging.impl.LoggerAttribute;
import org.w3c.dom.Node;

public class BundleServant {

    public static BundleServant Instance = null;

    static {
        if(null == Instance) {
            Instance = new BundleServant();
        }
    }

    protected BundleServant() {

    }

    private XmlParserContext loadBundleConf(String sessionId, BundleContext bctx){
        String bootXmlFilePath = bctx.findConfigFile("bundle.xml");
        XmlParserContext xmlParserCtx = XmlParserContext.makeXmlParserContext(sessionId, XmlParserContext.class,bootXmlFilePath);
        return xmlParserCtx;
    }

    private BootAttribute parserBundleConf(XmlParserContext xmlParserCtx){
        BootAttribute bAttr = new BootAttribute();
        String mkey = xmlParserCtx.findNodeValue("Bundle/MachineKey",true,false);
        String runtimeLevel = xmlParserCtx.findNodeValue("Bundle/RuntimeLevel",true,false);
        if(StringServant.Instance.isNotNullOrEmptyOrAllSpace(runtimeLevel)) {
            bAttr.setRuntimeLevel(runtimeLevel);
        }

        if(StringServant.Instance.isNotNullOrEmptyOrAllSpace(mkey)) {
            bAttr.setMachineKey(mkey);
        }

        Node loggersNode = xmlParserCtx.selectNode("Bundle/Logger",false);
        if(null != loggersNode){
            ILoggerAttribute logAttr =  parserRuntimeLoggerConf(xmlParserCtx,loggersNode);
            bAttr.setRootLoggerAttr(logAttr);
        }
        return bAttr;
    }

    private ILoggerAttribute parserRuntimeLoggerConf(XmlParserContext xmlParserCtx, Node logNode){
        String rootPath = xmlParserCtx.findAttributeValue(logNode,"Path",true,false);
        String rootLevel = xmlParserCtx.findAttributeValue(logNode,"Level","INFO");
        String rootConsole = xmlParserCtx.findAttributeValue(logNode,"Console","true");
        String rootMaxFilesize = xmlParserCtx.findAttributeValue(logNode,"MaxFilesize","10MB");
        ILoggerAttribute logAttr = new LoggerAttribute("Runtime", rootPath,rootLevel,
                OptConvertServant.Instance.toBoolean(rootConsole,true),rootMaxFilesize);
        return logAttr;
    }

    public void repair(String sessionId,BundleContext bctx){
        XmlParserContext confCtx =  loadBundleConf(sessionId,bctx);
        BootAttribute attr = parserBundleConf(confCtx);
        bctx.setAttr(attr);
        ILoggerAttribute logAttr = attr.getRootLoggerAttr();
        LogServant.Instance.newRuntimeLogger(logAttr);
    }

}
