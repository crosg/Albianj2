package org.albianj.kernel.impl;

import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.service.*;
import org.albianj.service.impl.AlbianServiceRantParser;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

public class AlbianBuiltinServiceLoader {

    private LinkedHashMap<String,AlbianBuiltinServiceAttribute> bltServ = null;
    private Map<String,IAlbianServiceAttribute> bltSrvAttrs = null;
    public AlbianBuiltinServiceLoader(){
        bltServ = new LinkedHashMap<>();
        bltSrvAttrs = new LinkedHashMap<>();
        // kernel
        bltServ.put(AlbianBuiltinServicePair.AlbianLoggerServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianLoggerServicePair[0],
                        AlbianBuiltinServicePair.AlbianLoggerServicePair[1],true));
        bltServ.put(AlbianBuiltinServicePair.AlbianLoggerService2Pair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianLoggerService2Pair[0],
                        AlbianBuiltinServicePair.AlbianLoggerService2Pair[1],true));
        bltServ.put(AlbianBuiltinServicePair.AlbianKernelServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianKernelServicePair[0],
                        AlbianBuiltinServicePair.AlbianKernelServicePair[1],true));
        bltServ.put(AlbianBuiltinServicePair.AlbianLogicIdServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianLogicIdServicePair[0],
                        AlbianBuiltinServicePair.AlbianLogicIdServicePair[1],true));
        bltServ.put(AlbianBuiltinServicePair.AlbianThreadPoolServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianThreadPoolServicePair[0],
                        AlbianBuiltinServicePair.AlbianThreadPoolServicePair[1],true));
        bltServ.put(AlbianBuiltinServicePair.AlbianSecurityServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianSecurityServicePair[0],
                        AlbianBuiltinServicePair.AlbianSecurityServicePair[1],true));

        // persistence
        bltServ.put(AlbianBuiltinServicePair.AlbianStorageServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianStorageServicePair[0],
                        AlbianBuiltinServicePair.AlbianStorageServicePair[1],false));
        bltServ.put(AlbianBuiltinServicePair.AlbianMappingServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianMappingServicePair[0],
                        AlbianBuiltinServicePair.AlbianMappingServicePair[1],false));
        bltServ.put(AlbianBuiltinServicePair.AlbianDataRouterServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianDataRouterServicePair[0],
                        AlbianBuiltinServicePair.AlbianDataRouterServicePair[1],false));
        bltServ.put(AlbianBuiltinServicePair.AlbianPersistenceServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianPersistenceServicePair[0],
                        AlbianBuiltinServicePair.AlbianPersistenceServicePair[1],false));
        bltServ.put(AlbianBuiltinServicePair.AlbianDataAccessServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianDataAccessServicePair[0],
                        AlbianBuiltinServicePair.AlbianDataAccessServicePair[1],false));

        //pplog monitor
        bltServ.put(AlbianBuiltinServicePair.YuewenPPLogPair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.YuewenPPLogPair[0],
                        AlbianBuiltinServicePair.YuewenPPLogPair[1],false));
        // web mvf framework
        bltServ.put(AlbianBuiltinServicePair.AlbianMvcConfigurtionServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianMvcConfigurtionServicePair[0],
                        AlbianBuiltinServicePair.AlbianMvcConfigurtionServicePair[1],false));
        bltServ.put(AlbianBuiltinServicePair.AlbianFileUploadServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianFileUploadServicePair[0],
                        AlbianBuiltinServicePair.AlbianFileUploadServicePair[1],false));
        bltServ.put(AlbianBuiltinServicePair.AlbianResourceServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianResourceServicePair[0],
                        AlbianBuiltinServicePair.AlbianResourceServicePair[1],false));
        bltServ.put(AlbianBuiltinServicePair.AlbianTemplateServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianTemplateServicePair[0],
                        AlbianBuiltinServicePair.AlbianTemplateServicePair[1],false));
        bltServ.put(AlbianBuiltinServicePair.AlbianBrushingServicePair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianBrushingServicePair[0],
                        AlbianBuiltinServicePair.AlbianBrushingServicePair[1],false));

        // load service.xml
        bltServ.put(AlbianBuiltinServicePair.AlbianServiceParserPair[0],
                new AlbianBuiltinServiceAttribute(AlbianBuiltinServicePair.AlbianServiceParserPair[0],
                        AlbianBuiltinServicePair.AlbianServiceParserPair[1],true));
    }

    public void loadServices(){
        String id = null;
        String sImplClzz = null;
        int failCount  =0;
        int lastFailCount = 0;
        boolean requiredServiceFail = false;
        boolean pluginServiceFail = false;
        StringBuilder sbFailReqServiceBiref = new StringBuilder();
        StringBuilder sbFailPluginServiceBiref = new StringBuilder();
        while(true) {
            failCount = 0;
            requiredServiceFail = false;
            pluginServiceFail = false;
            if(0 != sbFailReqServiceBiref.length()) {
                sbFailReqServiceBiref.delete(0, sbFailReqServiceBiref.length() - 1);
            }
            if(0 != sbFailPluginServiceBiref.length()) {
                sbFailPluginServiceBiref.delete(0, sbFailPluginServiceBiref.length() - 1);
            }

            for (AlbianBuiltinServiceAttribute bltSerAttr : this.bltServ.values()) {
                if (bltSerAttr.isLoadOK()) continue;
                id = bltSerAttr.getId();
                sImplClzz = bltSerAttr.getImplClzz();
                try {
                    Class<?> implClzz = AlbianClassLoader.getInstance().loadClass(sImplClzz);
                    IAlbianServiceAttribute attr = AlbianServiceRantParser.scanAlbianService(implClzz);
                    IAlbianService service = AlbianServiceLoader.makeupService(attr);
                    ServiceContainer.addService(id, service);
                    bltSerAttr.setLoadOK(true);
                    bltSrvAttrs.put(attr.getId(),attr);
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
            if(0 == failCount){
                break;
            }

            if (lastFailCount != failCount) {
                lastFailCount = failCount; // load next
            } else {
                // the last fail service count is the same as this time
                // means no new service was loaded.

                if(pluginServiceFail){
                    // plugin service load fail is not throw exception
                    AlbianServiceRouter.addLog("Transmitter", IAlbianLoggerService2.AlbianRunningLoggerName,
                            AlbianLoggerLevel.Error,"loader plugin service -> %s fail.",sbFailPluginServiceBiref);
                } //can not return,check required service
                if(requiredServiceFail) {
                    //required service can not load fail.
                    AlbianServiceRouter.throwException("Transmitter",
                            IAlbianLoggerService2.AlbianRunningLoggerName,
                            sbFailReqServiceBiref.toString(),"loader required service is fail.");
                }
                return;
            }
        }
    }

    public Map<String,IAlbianServiceAttribute> getBltSrvAttrs(){
        return this.bltSrvAttrs;
    }
}
