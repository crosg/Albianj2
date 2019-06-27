package org.albianj.kernel.impl;

import org.albianj.kernel.AlbianState;
import org.albianj.boot.BundleContext;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.ILoggerService;
import org.albianj.logger.ILoggerService2;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FinalAlbianBootStartupService {

    private static AlbianState state = AlbianState.Normal;

    public boolean init(BundleContext bundleContext, boolean needBootStart) throws Exception {
        return initService(bundleContext,needBootStart);
    }

    public boolean initService(BundleContext bundleContext, boolean needBootStart) throws Exception {
        AlbianBuiltinServiceLoader bltSevLoader = new AlbianBuiltinServiceLoader();
        bltSevLoader.loadServices(bundleContext);
        Map<String, IServiceAttribute> bltSrvAttrs = bltSevLoader.getBltSrvAttrs();

        //do load bussiness service
//        Map<String, IServiceAttribute> bnsSrvAttrs = (Map<String, IServiceAttribute>)
//                AlbianBundleServiceConf
//                        .get(FreeServiceParser.AlbianServiceModuleName);

        AlbianBundleModuleKeyValueConf bnsSrvAttrs =  bundleContext.getModuleConf(BuiltinNames.Conf.Service);
//        AlbianBundleModuleKeyValueConf bnsSrvAttrs = (AlbianBundleModuleKeyValueConf) serviceFromConf;

        Map<String, IServiceAttribute> mapAttr = new HashMap<>();
        mapAttr.putAll(bnsSrvAttrs); // copy it for field setter

        for (String bltServKey : bltSrvAttrs.keySet()) { // remove builtin service in service.xml
            if (mapAttr.containsKey(bltServKey)) {
                mapAttr.remove(bltServKey);
            }
        }

        Map<String, IServiceAttribute> failMap = new LinkedHashMap<String, IServiceAttribute>();
        int lastFailSize = 0;
        int currentFailSize = 0;
        Exception e = null;
        while (true) {
            lastFailSize = currentFailSize;
            currentFailSize = 0;
            String sType = null;
            String id = null;
            String sInterface = null;
            for (Map.Entry<String, IServiceAttribute> entry : mapAttr
                    .entrySet())
                try {
                    IServiceAttribute serviceAttr = entry.getValue();
                    IService service = AlbianServiceLoader.makeupServiceAndAttachBundleContext(bundleContext,serviceAttr,mapAttr);
//                    ServiceContainer.addService(serviceAttr.getId(), service);
//                    bundleContext.addBundleService(serviceAttr.getId(),service);
                } catch (Exception exc) {
                    AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                            ILoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Info, exc, AlbianModuleType.AlbianKernel,
                            "Kernel is error.", "load and loadConf service:%s with class:%s is fail.",
                            id, sType);
                    e = exc;
                    currentFailSize++;
                    failMap.put(entry.getKey(), entry.getValue());
                }
            if (0 == currentFailSize) {
                // if open the distributed mode,
                // please contact to manager machine to logout the system.
                AlbianServiceRouter.getLogger2().log(ILoggerService.AlbianRunningLoggerName,
                        ILoggerService2.InnerThreadName,
                        AlbianLoggerLevel.Info,
                        "load service is success,then set field in the services!");

                break;// load service successen
            }

            if (lastFailSize == currentFailSize) {
                // startup the service fail in this times,
                // so throw the exception and stop the albianj engine
                state = AlbianState.Unloading;
                AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                        ILoggerService2.InnerThreadName,
                        AlbianLoggerLevel.Error, "startup albianj engine is fail,maybe cross refernce.");
                if (null != e) {
                    AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                            ILoggerService2.InnerThreadName,
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

        AlbianBundleModuleKeyValueConf moduleConf =  bundleContext.getModuleConfAndNewIfNotExist(BuiltinNames.Conf.Service,AlbianBundleModuleKeyValueConf.class);

        moduleConf.putAll(bnsSrvAttrs);
        moduleConf.putAll(bltSrvAttrs);

        state = AlbianState.Running;
        AlbianServiceRouter.getLogger2().log(ILoggerService.AlbianRunningLoggerName,
                ILoggerService2.InnerThreadName,
                AlbianLoggerLevel.Info,
                "set fields in the service over.Startup albianj is success!");
        return true;

    }

}
