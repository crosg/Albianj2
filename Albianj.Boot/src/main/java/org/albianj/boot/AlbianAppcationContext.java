package org.albianj.boot;

import org.albianj.boot.entry.BootAttribute;
import org.albianj.boot.entry.BundleAttribute;

import java.util.HashMap;
import java.util.Map;

public class AlbianAppcationContext {

    private Class<?> mainClzz = null;
    private String workFolder = null;
    private String logsPath = null;
    private boolean isOpenConsole = false;
    private Map<String, BundleAttribute> attAttrs = new HashMap<>();
    private Map<String, BundleContext> bundleContextMap = new HashMap<>();

    private BundleContext bootCtx;
    BootAttribute bootAttr;


}
