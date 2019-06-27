package org.albianj.service;

public class BuiltinNames {

    /**
     * 配置文件的模块名称，bundle context中conf attribute的key的值
     */
    public class Conf{
        public final static String Service = "Service";
        public final static String Logger = "Logger";
        public final static String Storage = "Storage";
        public final static String Persistence = "Persistence";
    }


    public final static String AlbianLoggerServiceName = "AlbianLoggerService";
    public final static String AlbianLoggerService2Name = "AlbianLoggerService2";
    public final static String AlbianKernelServiceName = "AlbianKernelService";
    public final static String AlbianLogicIdServiceName = "AlbianLogicIdService";
    public final static String AlbianSecurityServiceName = "AlbianSecurityService";
    // persistence
    public final static String AlbianStorageServiceName = "AlbianStorageService";
    public final static String AlbianMappingServiceName = "AlbianMappingService";
    public final static String AlbianDataRouterServiceName = "AlbianDataRouterService";
    public final static String AlbianPersistenceServiceName = "AlbianPersistenceService";
    public final static String AlbianDataAccessServiceName = "AlbianDataAccessService";

    //pplog monitor
    public final static String YuewenPPLogServiceName = "YuewenPPLogService";
    public final static String AlbianMonitorLoggerServiceName = "AlbianMonitorLoggerService";

    // web mvf framework
    public final static String AlbianMvcConfigurtionServiceName = "AlbianMvcConfigurtionService";
    public final static String AlbianFileUploadServiceName = "AlbianFileUploadService";
    public final static String AlbianResourceServiceName = "AlbianResourceService";
    public final static String AlbianTemplateServiceName = "AlbianTemplateService";
    public final static String AlbianBrushingServiceName = "AlbianBrushingService";

    // load service.xml
    public final static String AlbianServiceParserName = "AlbianServiceParserService";


    public final static String[] AlbianLoggerServicePair = {AlbianLoggerServiceName, "org.albianj.logger.impl.AlbianLoggerService"};
    public final static String[] AlbianLoggerService2Pair = {AlbianLoggerService2Name, "org.albianj.logger.impl.AlbianLoggerService2"};
    public final static String[] AlbianKernelServicePair = {AlbianKernelServiceName, "org.albianj.kernel.impl.AlbianKernelParserService"};
    public final static String[] AlbianLogicIdServicePair = {AlbianLogicIdServiceName, "org.albianj.kernel.impl.AlbianLogicIdService"};
//    public final static String[] AlbianThreadPoolServicePair = {AlbianThreadPoolServiceName, "org.albianj.concurrent.impl.AlbianThreadPoolService"};
    public final static String[] AlbianSecurityServicePair = {AlbianSecurityServiceName, "org.albianj.security.impl.AlbianSecurityService"};
    // persistence
    public final static String[] AlbianStorageServicePair = {AlbianStorageServiceName, "org.albianj.persistence.impl.storage.AlbianStorageParserService"};
    public final static String[] AlbianMappingServicePair = {AlbianMappingServiceName, "org.albianj.persistence.impl.mapping.AlbianMappingParserService"};
    public final static String[] AlbianDataRouterServicePair = {AlbianDataRouterServiceName, "org.albianj.persistence.impl.routing.AlbianDataRouterParserService"};
    public final static String[] AlbianPersistenceServicePair = {AlbianPersistenceServiceName, "org.albianj.persistence.impl.service.AlbianPersistenceService"};
    public final static String[] AlbianDataAccessServicePair = {AlbianDataAccessServiceName, "org.albianj.persistence.impl.service.AlbianDataAccessService"};

    public final static String[] AlbianMonitorLoggerServicePair = {AlbianMonitorLoggerServiceName, "org.albianj.logger.impl.monitor.AlbianMonitorLoggerService"};

    //pplog monitor
    public final static String[] YuewenPPLogPair = {YuewenPPLogServiceName, "com.yuewen.pplogstat.impl.YuewenPPLogStatService"};

    // web mvf framework
    public final static String[] AlbianMvcConfigurtionServicePair = {AlbianMvcConfigurtionServiceName, "org.albianj.mvc.config.impl.AlbianMVCConfigurtionService"};
    public final static String[] AlbianFileUploadServicePair = {AlbianFileUploadServiceName, "org.albianj.mvc.service.impl.AlbianFileUploadService"};
    public final static String[] AlbianResourceServicePair = {AlbianResourceServiceName, "org.albianj.mvc.service.impl.AlbianResourceService"};
    public final static String[] AlbianTemplateServicePair = {AlbianTemplateServiceName, "org.albianj.mvc.service.impl.AlbianBeetlTemplateService"};
    public final static String[] AlbianBrushingServicePair = {AlbianBrushingServiceName, "org.albianj.mvc.service.impl.AlbianBrushingService"};

    // load service.xml
    public final static String[] AlbianServiceParserPair = {AlbianServiceParserName, "org.albianj.service.impl.AlbianServiceParser"};
}
