package org.albianj.kernel.impl;

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
    private Map<String, IAlbianServiceAttribute> bltSrvAttrs = null;

    public AlbianBuiltinServiceLoader() {
        bltServ = new LinkedHashMap<>();
        bltSrvAttrs = new LinkedHashMap<>();
        // kernel
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianLoggerServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianLoggerServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianLoggerServicePair[1], true));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianLoggerService2Pair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianLoggerService2Pair[0],
                        AlbianBuiltinServiceNamePair.AlbianLoggerService2Pair[1], true));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianKernelServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianKernelServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianKernelServicePair[1], true));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianLogicIdServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianLogicIdServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianLogicIdServicePair[1], true));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianThreadPoolServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianThreadPoolServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianThreadPoolServicePair[1], true));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianSecurityServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianSecurityServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianSecurityServicePair[1], true));

        // persistence
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianStorageServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianStorageServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianStorageServicePair[1], false));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianMappingServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianMappingServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianMappingServicePair[1], false));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianDataRouterServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianDataRouterServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianDataRouterServicePair[1], false));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianPersistenceServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianPersistenceServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianPersistenceServicePair[1], false));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianDataAccessServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianDataAccessServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianDataAccessServicePair[1], false));

        //pplog monitor
        bltServ.put(AlbianBuiltinServiceNamePair.YuewenPPLogPair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.YuewenPPLogPair[0],
                        AlbianBuiltinServiceNamePair.YuewenPPLogPair[1], false));
        // web mvf framework
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianMvcConfigurtionServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianMvcConfigurtionServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianMvcConfigurtionServicePair[1], false));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianFileUploadServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianFileUploadServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianFileUploadServicePair[1], false));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianResourceServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianResourceServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianResourceServicePair[1], false));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianTemplateServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianTemplateServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianTemplateServicePair[1], false));
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianBrushingServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianBrushingServicePair[0],
                        AlbianBuiltinServiceNamePair.AlbianBrushingServicePair[1], false));

        // load service.xml
        bltServ.put(AlbianBuiltinServiceNamePair.AlbianServiceParserPair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServiceNamePair.AlbianServiceParserPair[0],
                        AlbianBuiltinServiceNamePair.AlbianServiceParserPair[1], true));
    }

    public void loadServices() {

        Map<String,IAlbianServiceAttribute> bltServMap = sacnService();
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
                    IAlbianServiceAttribute attr = bltServMap.get(id);
                    IAlbianService service = AlbianServiceLoader.makeupService(attr,bltServMap);
                    ServiceContainer.addService(id, service);
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

    public Map<String, IAlbianServiceAttribute> getBltSrvAttrs() {
        return this.bltSrvAttrs;
    }

    public Map<String,IAlbianServiceAttribute> sacnService(){
        bltSrvAttrs = new LinkedHashMap<>();
        for (AlbianBuiltinServiceAttribute bltSerAttr : this.bltServ.values()) {
            String id = bltSerAttr.getId();
            String sImplClzz = bltSerAttr.getImplClzz();
            try {
                Class<?> implClzz = AlbianClassLoader.getInstance().loadClass(sImplClzz);
                IAlbianServiceAttribute attr = AlbianServiceRantParser.scanAlbianService(implClzz);
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
