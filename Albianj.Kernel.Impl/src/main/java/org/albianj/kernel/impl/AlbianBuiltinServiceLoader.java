package org.albianj.kernel.impl;

import org.albianj.loader.AlbianBundleContext;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.service.*;
import org.albianj.service.impl.AlbianServiceRantParser;
import org.albianj.verify.Validate;

import java.util.LinkedHashMap;
import java.util.Map;

public class AlbianBuiltinServiceLoader {

    private LinkedHashMap<String, AlbianBuiltinServiceAttribute> bltServ = null;
    private Map<String, IAlbianBundleServiceAttribute> bltSrvAttrs = null;

    public AlbianBuiltinServiceLoader() {
        bltServ = new LinkedHashMap<>();
        bltSrvAttrs = new LinkedHashMap<>();
        // kernel
        bltServ.put(AlbianBuiltinNames.AlbianLoggerServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianLoggerServicePair[0],
                        AlbianBuiltinNames.AlbianLoggerServicePair[1], true));
        bltServ.put(AlbianBuiltinNames.AlbianLoggerService2Pair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianLoggerService2Pair[0],
                        AlbianBuiltinNames.AlbianLoggerService2Pair[1], true));
        bltServ.put(AlbianBuiltinNames.AlbianKernelServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianKernelServicePair[0],
                        AlbianBuiltinNames.AlbianKernelServicePair[1], true));
        bltServ.put(AlbianBuiltinNames.AlbianLogicIdServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianLogicIdServicePair[0],
                        AlbianBuiltinNames.AlbianLogicIdServicePair[1], true));
//        bltServ.put(AlbianBuiltinNames.AlbianThreadPoolServicePair[0],
//                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianThreadPoolServicePair[0],
//                        AlbianBuiltinNames.AlbianThreadPoolServicePair[1], true));
        bltServ.put(AlbianBuiltinNames.AlbianSecurityServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianSecurityServicePair[0],
                        AlbianBuiltinNames.AlbianSecurityServicePair[1], true));

        // persistence
        bltServ.put(AlbianBuiltinNames.AlbianStorageServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianStorageServicePair[0],
                        AlbianBuiltinNames.AlbianStorageServicePair[1], false));
        bltServ.put(AlbianBuiltinNames.AlbianMappingServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianMappingServicePair[0],
                        AlbianBuiltinNames.AlbianMappingServicePair[1], false));
        bltServ.put(AlbianBuiltinNames.AlbianDataRouterServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianDataRouterServicePair[0],
                        AlbianBuiltinNames.AlbianDataRouterServicePair[1], false));
        bltServ.put(AlbianBuiltinNames.AlbianPersistenceServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianPersistenceServicePair[0],
                        AlbianBuiltinNames.AlbianPersistenceServicePair[1], false));
        bltServ.put(AlbianBuiltinNames.AlbianDataAccessServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianDataAccessServicePair[0],
                        AlbianBuiltinNames.AlbianDataAccessServicePair[1], false));

        //monitor
        bltServ.put(AlbianBuiltinNames.AlbianMonitorLoggerServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianMonitorLoggerServicePair[0],
                        AlbianBuiltinNames.AlbianMonitorLoggerServicePair[1], false));

        //pplog monitor
        bltServ.put(AlbianBuiltinNames.YuewenPPLogPair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.YuewenPPLogPair[0],
                        AlbianBuiltinNames.YuewenPPLogPair[1], false));
        // web mvf framework
        bltServ.put(AlbianBuiltinNames.AlbianMvcConfigurtionServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianMvcConfigurtionServicePair[0],
                        AlbianBuiltinNames.AlbianMvcConfigurtionServicePair[1], false));
        bltServ.put(AlbianBuiltinNames.AlbianFileUploadServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianFileUploadServicePair[0],
                        AlbianBuiltinNames.AlbianFileUploadServicePair[1], false));
        bltServ.put(AlbianBuiltinNames.AlbianResourceServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianResourceServicePair[0],
                        AlbianBuiltinNames.AlbianResourceServicePair[1], false));
        bltServ.put(AlbianBuiltinNames.AlbianTemplateServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianTemplateServicePair[0],
                        AlbianBuiltinNames.AlbianTemplateServicePair[1], false));
        bltServ.put(AlbianBuiltinNames.AlbianBrushingServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianBrushingServicePair[0],
                        AlbianBuiltinNames.AlbianBrushingServicePair[1], false));

        // load service.xml
        bltServ.put(AlbianBuiltinNames.AlbianServiceParserPair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinNames.AlbianServiceParserPair[0],
                        AlbianBuiltinNames.AlbianServiceParserPair[1], true));
    }

    public void loadServices(AlbianBundleContext bundleContext) {

        Map<String, IAlbianBundleServiceAttribute> bltServMap = sacnService(bundleContext);
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
                    IAlbianBundleServiceAttribute attr = bltServMap.get(id);
                    IAlbianService service = AlbianServiceLoader.makeupServiceAndAttachBundleContext(bundleContext,attr,bltServMap);
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
                    AlbianServiceRouter.addLog("Transmitter", IAlbianLoggerService2.AlbianRunningLoggerName,
                            AlbianLoggerLevel.Error, "loader plugin service -> %s fail.", sbFailPluginServiceBiref);
                } //can not return,check required service
                if (requiredServiceFail) {
                    //required service can not load fail.
                    AlbianServiceRouter.throwException("Transmitter",
                            IAlbianLoggerService2.AlbianRunningLoggerName,
                            sbFailReqServiceBiref.toString(), "loader required service is fail.");
                }
                return;
            }
        }
    }

    public Map<String, IAlbianBundleServiceAttribute> getBltSrvAttrs() {
        return this.bltSrvAttrs;
    }

    public Map<String, IAlbianBundleServiceAttribute> sacnService(AlbianBundleContext bundleContext){
        bltSrvAttrs = new LinkedHashMap<>();
        for (AlbianBuiltinServiceAttribute bltSerAttr : this.bltServ.values()) {
            String id = bltSerAttr.getId();
            String sImplClzz = bltSerAttr.getImplClzz();
            try {
                Class<?> implClzz = AlbianClassLoader.getInstance().loadClass(sImplClzz);
                IAlbianBundleServiceAttribute attr = AlbianServiceRantParser.scanAlbianService(implClzz);
//                attr.setBundleContext(bundleContext);
                bltSrvAttrs.put(id,attr);
            }catch (Exception e){
                if(bltSerAttr.isRequired()) {
                    AlbianServiceRouter.throwException("BuiltinServiceLoader",
                            IAlbianLoggerService2.AlbianRunningLoggerName,
                            "Load builtin service fail.",
                            String.format("loader builtin  service -> %s is fail.",
                                    bltSerAttr.getId()));
                } else {
                    AlbianServiceRouter.addLog("BuiltinServiceLoader",
                            IAlbianLoggerService2.AlbianRunningLoggerName,AlbianLoggerLevel.Warn,
                            String.format("loader builtin  service -> %s is fail. but it is not must load.",
                                    bltSerAttr.getId()));
                }
            }
        }
        if(Validate.isNullOrEmpty(bltSrvAttrs))  return null;
        return bltSrvAttrs;
    }

}
