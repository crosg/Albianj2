package org.albianj.persistence.impl.context;

import java.util.Map;

import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IAlbianObjectDataRouter;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.persistence.object.IDataRoutersAttribute;
import org.albianj.persistence.object.IFilterCondition;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.service.IAlbianDataRouterParserService;
import org.albianj.persistence.service.IAlbianMappingParserService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

public class ReaderJobAdapter extends FreeReaderJobAdapter implements IReaderJobAdapter {
	protected IDataRouterAttribute parserReaderRouting(Class<?> cls, String jobId, boolean isExact, String routingName,
			Map<String, IFilterCondition> hashWheres, Map<String, IOrderByCondition> hashOrderbys)
					throws AlbianDataServiceException {
		String className = cls.getName();
		IAlbianDataRouterParserService adrps = AlbianServiceRouter.getService(IAlbianDataRouterParserService.class,IAlbianDataRouterParserService.Name);
		IDataRoutersAttribute routings = adrps.getDataRouterAttribute(className);

		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute albianObject = amps.getAlbianObjectAttribute(className);
		
//		IAlbianObjectAttribute albianObject = (IAlbianObjectAttribute) AlbianObjectsMap.get(className);
		if (null == albianObject) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, "DataService is error.",
					"albian-object:%s attribute is not found.job id:%s.", className, jobId);
		}

		Map<String, IDataRouterAttribute> routers = isExact ? routings.getWriterRouters() : routings.getReaderRouters();

		if (null == routings || Validate.isNullOrEmpty(routers)) {
			IDataRouterAttribute dra = albianObject.getDefaultRouting();
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"albian-object:%s reader-data-router is null or empty and use default:%s.job id:%s.", className,
					dra.getName(), jobId);
			return dra;
		}

		if (Validate.isNullOrEmptyOrAllSpace(routingName)) {
			if (isExact) {
				if (!routings.getWriterRouterEnable()) {
					IDataRouterAttribute dra = albianObject.getDefaultRouting();
					AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
							"the reader-date-router is not appoint and the object:%s all reader router are disable. then use defaut:%s.job id:%s.",
							className, dra.getName(), jobId);
					return dra;
				}
			} else {
				if (!routings.getReaderRouterEnable()) {
					IDataRouterAttribute dra = albianObject.getDefaultRouting();
					AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
							"the reader-date-router is not appoint and the object:%s all reader router are disable. then use defaut:%s.job id:%s.",
							className, dra.getName(), jobId);
					return dra;
				}
			}
			IAlbianObjectDataRouter hashMapping = routings.getDataRouter();
			if (null != hashMapping) {
				IDataRouterAttribute routing = null;
				if (isExact) {
					routing = hashMapping.mappingExactReaderRouting(routings.getWriterRouters(), hashWheres,
							hashOrderbys);
				} else {
					routing = hashMapping.mappingReaderRouting(routings.getReaderRouters(), hashWheres, hashOrderbys);
				}
				if (null == routing) {
					IDataRouterAttribute dra = albianObject.getDefaultRouting();
					AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
							"the reader-date-router is not appoint and the object:%s not found router. then use defaut:%s.job id:%s.",
							className, dra.getName(), jobId);
					return dra;
				}
				if (!routing.getEnable()) {
					IDataRouterAttribute dra = albianObject.getDefaultRouting();
					AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
							"the reader-date-router is not appoint and the object:%s found router:%s but it disable. then use defaut:%s.job id:%s.",
							className, routing.getName(), dra.getName(), jobId);
					return dra;
				}
				return routing;
			}
			IDataRouterAttribute dra = albianObject.getDefaultRouting();
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"the reader-date-router is not appoint and the object:%s reader-date-router arithmetic is null. then use defaut:%s.job id:%s.",
					className, dra.getName(), jobId);
			return dra;
		}

		IDataRouterAttribute routing = routers.get(routingName);

		if (null == routing) {
			IDataRouterAttribute dra = albianObject.getDefaultRouting();
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"albian-object:%s reader-data-router is not found and use default:%s.job id:%s.", className,
					dra.getName(), jobId);
			return dra;
		}
		if (!routing.getEnable()) {
			IDataRouterAttribute dra = albianObject.getDefaultRouting();
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"the reader-date-router is not appoint and the object:%s found router:%s but it disable. then use defaut:%s.job id:%s.",
					className, routing.getName(), dra.getName(), jobId);
			return dra;
		}
		return routing;
	}

	protected String parserRoutingStorage(Class<?> cls, String jobId,boolean isExact, IDataRouterAttribute readerRouting,
			Map<String, IFilterCondition> hashWheres, Map<String, IOrderByCondition> hashOrderbys)
					throws AlbianDataServiceException {
		String className = cls.getName();
		IAlbianDataRouterParserService adrps = AlbianServiceRouter.getService(IAlbianDataRouterParserService.class,IAlbianDataRouterParserService.Name);
		IDataRoutersAttribute routings = adrps.getDataRouterAttribute(className);

		if (null == readerRouting) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, "DataService is error.",
					"the reader data router of object:%s is null.job id:%s.", className, jobId);
		}

		if (null == routings) {
			String name = readerRouting.getStorageName();
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"albian-object:%s reader-data-router is not found and use default storage:%s.job id:%s.", className,
					name, jobId);
			return name;
		}
		IAlbianObjectDataRouter router = routings.getDataRouter();
		if (null == router) {
			String name = readerRouting.getStorageName();
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"albian-object:%s reader-data-router arithmetic is not found and use default storage:%s.job id:%s.",
					className, name, jobId);
			return name;
		}

		String name = isExact ? router.mappingExactReaderRoutingStorage(readerRouting, hashWheres, hashOrderbys) 
				: router.mappingReaderRoutingStorage(readerRouting, hashWheres, hashOrderbys);
		if (Validate.isNullOrEmpty(name)) {
			String dname = readerRouting.getStorageName();
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"albian-object:%s reader-data-router is not found by arithmetic and use default storage:%s.job id:%s.",
					className, dname, jobId);
			return dname;
		} else {
			return name;
		}
	}

}
