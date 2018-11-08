package org.albianj.service;

public class AlbianBuiltinServicePair {
      public final static String[] AlbianLoggerServicePair = {"AlbianLoggerService","org.albianj.logger.impl.AlbianLoggerService"};
        public final static String[] AlbianLoggerService2Pair = {"AlbianLoggerService2","org.albianj.logger.impl.AlbianLoggerService2"};
   public final static String[] AlbianKernelServicePair = {"AlbianKernelService","org.albianj.kernel.impl.AlbianKernelParserService"};
   public final static String[] AlbianLogicIdServicePair = {"AlbianLogicIdService","org.albianj.kernel.impl.AlbianLogicIdService"};
   public final static String[] AlbianThreadPoolServicePair = {"AlbianThreadPoolService","org.albianj.concurrent.impl.AlbianThreadPoolService"};
   public final static String[] AlbianSecurityServicePair = {"AlbianSecurityService","org.albianj.security.impl.AlbianSecurityService"};
    // persistence
   public final static String[] AlbianStorageServicePair = {"AlbianStorageService","org.albianj.persistence.impl.storage.AlbianStorageParserService"};
   public final static String[] AlbianMappingServicePair = {"AlbianMappingService","org.albianj.persistence.impl.mapping.AlbianMappingParserService"};
   public final static String[] AlbianDataRouterServicePair = {"AlbianDataRouterService","org.albianj.persistence.impl.routing.AlbianDataRouterParserService"};
   public final static String[] AlbianPersistenceServicePair = {"AlbianPersistenceService","org.albianj.persistence.impl.service.AlbianPersistenceService"};
   public final static String[] AlbianDataAccessServicePair = {"AlbianDataAccessService","org.albianj.persistence.impl.service.AlbianDataAccessService"};

    //pplog monitor
   public final static String[] YuewenPPLogPair = {"YuewenPPLog","com.yuewen.pplogstat.impl.YuewenPPLogStatService"};

    // web mvf framework
   public final static String[] AlbianMvcConfigurtionServicePair = {"AlbianMvcConfigurtionService","org.albianj.mvc.config.impl.AlbianMVCConfigurtionService"};
   public final static String[] AlbianFileUploadServicePair = {"AlbianFileUploadService","org.albianj.mvc.service.impl.AlbianFileUploadService"};
   public final static String[] AlbianResourceServicePair = {"AlbianResourceService","org.albianj.mvc.service.impl.AlbianResourceService"};
   public final static String[] AlbianTemplateServicePair = {"AlbianTemplateService","org.albianj.mvc.service.impl.AlbianBeetlTemplateService"};
   public final static String[] AlbianBrushingServicePair = {"AlbianBrushingService","org.albianj.mvc.service.impl.AlbianBrushingService"};

    // load service.xml
   public final static String[] AlbianServiceParserPair = {"AlbianServiceParser","org.albianj.service.impl.AlbianServiceParser"};
}
