package org.albianj.kernel.impl;

import org.albianj.boot.BundleContext;
import org.albianj.boot.loader.AlbianClassLoader;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.ILoggerService2;
import org.albianj.service.*;
import org.albianj.service.impl.AlbianServiceRantParser;
import org.albianj.verify.Validate;

import java.util.LinkedHashMap;
import java.util.Map;

public class AlbianBuiltinServiceLoader {

    private LinkedHashMap<String, AlbianBuiltinServiceAttribute> bltServ = null;
    private Map<String, IServiceAttribute> bltSrvAttrs = null;

    public AlbianBuiltinServiceLoader() {
        bltServ = new LinkedHashMap<>();
        bltSrvAttrs = new LinkedHashMap<>();
        // kernel
        bltServ.put(BuiltinNames.AlbianLoggerServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianLoggerServicePair[0],
                        BuiltinNames.AlbianLoggerServicePair[1], true));
        bltServ.put(BuiltinNames.AlbianLoggerService2Pair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianLoggerService2Pair[0],
                        BuiltinNames.AlbianLoggerService2Pair[1], true));
        bltServ.put(BuiltinNames.AlbianKernelServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianKernelServicePair[0],
                        BuiltinNames.AlbianKernelServicePair[1], true));
        bltServ.put(BuiltinNames.AlbianLogicIdServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianLogicIdServicePair[0],
                        BuiltinNames.AlbianLogicIdServicePair[1], true));
//        bltServ.put(BuiltinNames.AlbianThreadPoolServicePair[0],
//                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianThreadPoolServicePair[0],
//                        BuiltinNames.AlbianThreadPoolServicePair[1], true));
        bltServ.put(BuiltinNames.AlbianSecurityServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianSecurityServicePair[0],
                        BuiltinNames.AlbianSecurityServicePair[1], true));

        // persistence
        bltServ.put(BuiltinNames.AlbianStorageServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianStorageServicePair[0],
                        BuiltinNames.AlbianStorageServicePair[1], false));
        bltServ.put(BuiltinNames.AlbianMappingServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianMappingServicePair[0],
                        BuiltinNames.AlbianMappingServicePair[1], false));
        bltServ.put(BuiltinNames.AlbianDataRouterServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianDataRouterServicePair[0],
                        BuiltinNames.AlbianDataRouterServicePair[1], false));
        bltServ.put(BuiltinNames.AlbianPersistenceServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianPersistenceServicePair[0],
                        BuiltinNames.AlbianPersistenceServicePair[1], false));
        bltServ.put(BuiltinNames.AlbianDataAccessServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianDataAccessServicePair[0],
                        BuiltinNames.AlbianDataAccessServicePair[1], false));

        //monitor
        bltServ.put(BuiltinNames.AlbianMonitorLoggerServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianMonitorLoggerServicePair[0],
                        BuiltinNames.AlbianMonitorLoggerServicePair[1], false));

        //pplog monitor
        bltServ.put(BuiltinNames.YuewenPPLogPair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.YuewenPPLogPair[0],
                        BuiltinNames.YuewenPPLogPair[1], false));
        // web mvf framework
        bltServ.put(BuiltinNames.AlbianMvcConfigurtionServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianMvcConfigurtionServicePair[0],
                        BuiltinNames.AlbianMvcConfigurtionServicePair[1], false));
        bltServ.put(BuiltinNames.AlbianFileUploadServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianFileUploadServicePair[0],
                        BuiltinNames.AlbianFileUploadServicePair[1], false));
        bltServ.put(BuiltinNames.AlbianResourceServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianResourceServicePair[0],
                        BuiltinNames.AlbianResourceServicePair[1], false));
        bltServ.put(BuiltinNames.AlbianTemplateServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianTemplateServicePair[0],
                        BuiltinNames.AlbianTemplateServicePair[1], false));
        bltServ.put(BuiltinNames.AlbianBrushingServicePair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianBrushingServicePair[0],
                        BuiltinNames.AlbianBrushingServicePair[1], false));

        // load service.xml
        bltServ.put(BuiltinNames.AlbianServiceParserPair[0],
                new AlbianBuiltinServiceAttribute(BuiltinNames.AlbianServiceParserPair[0],
                        BuiltinNames.AlbianServiceParserPair[1], true));
    }

    public void loadServices(BundleContext bundleContext) {

        Map<String, IServiceAttribute> bltServMap = sacnService(bundleContext);
        String id = null;
        String sImplClzz = null;
        int failCount = 0;
        int lastFailCount = 0;
        boolean requiredServiceFail = false;
        boolean pluginServiceFail = false;
        StringBuilder sbFailReqServiceBiref = new StringBuilder();
        StringBuilder sbFailPluginServiceBiref = new StringBuilder();
        while (true) {
            failCount = 0;
            requiredServiceFail = false;
            pluginServiceFail = false;
            if (0 != sbFailReqServiceBiref.length()) {
                sbFailReqServiceBiref.delete(0, sbFailReqServiceBiref.length() - 1);
            }
            if (0 != sbFailPluginServiceBiref.length()) {
                sbFailPluginServiceBiref.delete(0, sbFailPluginServiceBiref.length() - 1);
            }

            for (AlbianBuiltinServiceAttribute bltSerAttr : this.bltServ.values()) {
                if (bltSerAttr.isLoadOK()) continue;
                id = bltSerAttr.getId();
                try {
                    IServiceAttribute attr = bltServMap.get(id);
                    IService service = AlbianServiceLoader.makeupServiceAndAttachBundleContext(bundleContext,attr,bltServMap);
//                    ServiceContainer.addService(id, service);
                    bltSerAttr.setLoadOK(true);
                } catch (Exception e) {
                    bltSerAttr.setLoadOK(false);
                    if (bltSerAttr.isRequired()) {
                        sbFailReqServiceBiref.append(" ServiceId -> ").append(bltSerAttr.getId())
                                .append(" ImplClass -> ").append(bltSerAttr.getImplClzz());
                        requiredServiceFail = true;
                    } else {
                        sbFailPluginServiceBiref.append(" ServiceId -> ").append(bltSerAttr.getId())
                                .append(" ImplClass -> ").append(bltSerAttr.getImplClzz());
                        pluginServiceFail = true;
                    }
                    failCount++;
                }
            }
            // load all services success
            if (0 == failCount) {
                break;
            }

            if (lastFailCount != failCount) {
                lastFailCount = failCount; // load next
            } else {
                // the last fail service count is the same as this time
                // means no new service was loaded.

                if (pluginServiceFail) {
                    // plugin service load fail is not throw exception
                    AlbianServiceRouter.addLog("Transmitter", ILoggerService2.AlbianRunningLoggerName,
                            AlbianLoggerLevel.Error, "loader plugin service -> %s fail.", sbFailPluginServiceBiref);
                } //can not return,check required service
                if (requiredServiceFail) {
                    //required service can not load fail.
                    AlbianServiceRouter.throwException("Transmitter",
                            ILoggerService2.AlbianRunningLoggerName,
                            sbFailReqServiceBiref.toString(), "loader required service is fail.");
                }
                return;
            }
        }
    }

    public Map<String, IServiceAttribute> getBltSrvAttrs() {
        return this.bltSrvAttrs;
    }

    public Map<String, IServiceAttribute> sacnService(BundleContext bundleContext){
        bltSrvAttrs = new LinkedHashMap<>();
        for (AlbianBuiltinServiceAttribute bltSerAttr : this.bltServ.values()) {
            String id = bltSerAttr.getId();
            String sImplClzz = bltSerAttr.getImplClzz();
            try {
                Class<?> implClzz = AlbianClassLoader.getInstance().loadClass(sImplClzz);
                IServiceAttribute attr = AlbianServiceRantParser.scanAlbianService(implClzz);
//                attr.setBundleContext(bundleContext);
                bltSrvAttrs.put(id,attr);
            }catch (Exception e){
                if(bltSerAttr.isRequired()) {
                    AlbianServiceRouter.throwException("BuiltinServiceLoader",
                            ILoggerService2.AlbianRunningLoggerName,
                            "Load builtin service fail.",
                            String.format("loader builtin  service -> %s is fail.",
                                    bltSerAttr.getId()));
                } else {
                    AlbianServiceRouter.addLog("BuiltinServiceLoader",
                            ILoggerService2.AlbianRunningLoggerName,AlbianLoggerLevel.Warn,
                            String.format("loader builtin  service -> %s is fail. but it is not must load.",
                                    bltSerAttr.getId()));
                }
            }
        }
        if(Validate.isNullOrEmpty(bltSrvAttrs))  return null;
        return bltSrvAttrs;
    }

}
