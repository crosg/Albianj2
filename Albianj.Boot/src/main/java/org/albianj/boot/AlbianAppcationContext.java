package org.albianj.boot;

import org.albianj.boot.entry.AlbianBootAttribute;
import org.albianj.boot.entry.AlbianBundleAttribute;

import java.util.HashMap;
import java.util.Map;

public class AlbianAppcationContext {

    private Class<?> mainClzz = null;
    private String workFolder = null;
    private String logsPath = null;
    private boolean isOpenConsole = false;
    private Map<String, AlbianBundleAttribute> attAttrs = new HashMap<>();
    private Map<String,AlbianBundleContext> bundleContextMap = new HashMap<>();

    private AlbianBundleContext bootCtx;
    AlbianBootAttribute bootAttr;


}
