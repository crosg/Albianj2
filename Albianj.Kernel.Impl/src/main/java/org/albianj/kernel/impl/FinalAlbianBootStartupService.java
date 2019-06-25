package org.albianj.kernel.impl;

import org.albianj.kernel.AlbianState;
import org.albianj.boot.AlbianBundleContext;
import org.albianj.loader.entry.AlbianBundleModuleKeyValueConf;
import org.albianj.loader.entry.IAlbianBundleModuleConf;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FinalAlbianBootStartupService {

    private static AlbianState state = AlbianState.Normal;

    public boolean init(AlbianBundleContext bundleContext,boolean needBootStart) throws Exception {
        return initService(bundleContext,needBootStart);
    }

    public boolean initService(AlbianBundleContext bundleContext,boolean needBootStart) throws Exception {
        AlbianBuiltinServiceLoader bltSevLoader = new AlbianBuiltinServiceLoader();
        bltSevLoader.loadServices(bundleContext);
        Map<String, IAlbianBundleServiceAttribute> bltSrvAttrs = bltSevLoader.getBltSrvAttrs();

        //do load bussiness service
//        Map<String, IAlbianBundleServiceAttribute> bnsSrvAttrs = (Map<String, IAlbianBundleServiceAttribute>)
//                AlbianBundleServiceConf
//                        .get(FreeAlbianServiceParser.AlbianServiceModuleName);

        AlbianBundleModuleKeyValueConf bnsSrvAttrs =  bundleContext.getModuleConf(AlbianBuiltinNames.Conf.Service);
//        AlbianBundleModuleKeyValueConf bnsSrvAttrs = (AlbianBundleModuleKeyValueConf) serviceFromConf;

        Map<String, IAlbianBundleServiceAttribute> mapAttr = new HashMap<>();
        mapAttr.putAll(bnsSrvAttrs); // copy it for field setter

        for (String bltServKey : bltSrvAttrs.keySet()) { // remove builtin service in service.xml
            if (mapAttr.containsKey(bltServKey)) {
                mapAttr.remove(bltServKey);
            }
        }

        Map<String, IAlbianBundleServiceAttribute> failMap = new LinkedHashMap<String, IAlbianBundleServiceAttribute>();
        int lastFailSize = 0;
        int currentFailSize = 0;
        Exception e = null;
        while (true) {
            lastFailSize = currentFailSize;
            currentFailSize = 0;
            String sType = null;
            String id = null;
            String sInterface = null;
            for (Map.Entry<String, IAlbianBundleServiceAttribute> entry : mapAttr
                    .entrySet())
                try {
                    IAlbianBundleServiceAttribute serviceAttr = entry.getValue();
                    IAlbianService service = AlbianServiceLoader.makeupServiceAndAttachBundleContext(bundleContext,serviceAttr,mapAttr);
//                    ServiceContainer.addService(serviceAttr.getId(), service);
//                    bundleContext.addBundleService(serviceAttr.getId(),service);
                } catch (Exception exc) {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                            IAlbianLoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Info, exc, AlbianModuleType.AlbianKernel,
                            "Kernel is error.", "load and init service:%s with class:%s is fail.",
                            id, sType);
                    e = exc;
                    currentFailSize++;
                    failMap.put(entry.getKey(), entry.getValue());
                }
            if (0 == currentFailSize) {
                // if open the distributed mode,
                // please contact to manager machine to logout the system.
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService.AlbianRunningLoggerName,
                        IAlbianLoggerService2.InnerThreadName,
                        AlbianLoggerLevel.Info,
                        "load service is success,then set field in the services!");

                break;// load service successen
            }

            if (lastFailSize == currentFailSize) {
                // startup the service fail in this times,
                // so throw the exception and stop the albianj engine
                state = AlbianState.Unloading;
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                        IAlbianLoggerService2.InnerThreadName,
                        AlbianLoggerLevel.Error, "startup albianj engine is fail,maybe cross refernce.");
                if (null != e) {
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                            IAlbianLoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Error, e, "startup the service:%s is fail.", failMap.keySet().toString());
                }
//                ServiceContainer.clear();
                bundleContext.cleanupBundleServices();
                state = AlbianState.Unloaded;
                throw e;
            } else {
                mapAttr.clear();
                mapAttr.putAll(failMap);
                failMap.clear();
            }
        }

        // merger kernel service and bussines service
        // then update the all service attribute

        AlbianBundleModuleKeyValueConf moduleConf =  bundleContext.getModuleConfAndNewIfNotExist(AlbianBuiltinNames.Conf.Service,AlbianBundleModuleKeyValueConf.class);

        moduleConf.putAll(bnsSrvAttrs);
        moduleConf.putAll(bltSrvAttrs);

        state = AlbianState.Running;
        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService.AlbianRunningLoggerName,
                IAlbianLoggerService2.InnerThreadName,
                AlbianLoggerLevel.Info,
                "set fields in the service over.Startup albianj is success!");
        return true;

    }

}
