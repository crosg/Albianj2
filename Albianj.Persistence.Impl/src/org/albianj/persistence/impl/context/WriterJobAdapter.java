package org.albianj.persistence.impl.context;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.crypto.dsig.keyinfo.KeyValue;

import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.context.IWriterJob;
import org.albianj.persistence.context.IWriterTask;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.impl.db.IPersistenceUpdateCommand;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IAlbianObjectDataRouter;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.persistence.object.IDataRoutersAttribute;
import org.albianj.persistence.object.IMemberAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.object.RunningStorageAttribute;
import org.albianj.persistence.service.IAlbianDataRouterParserService;
import org.albianj.persistence.service.IAlbianMappingParserService;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

public class WriterJobAdapter extends FreeWriterJobAdapter {
	protected void buildWriterJob(IAlbianObject object, IWriterJob writerJob,
			IPersistenceUpdateCommand update) throws AlbianDataServiceException {
		String className = object.getClass().getName();

		IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		
		String interName = amps.getAlbianObjectInterface(className);
		IAlbianDataRouterParserService adrps = AlbianServiceRouter.getSingletonService(IAlbianDataRouterParserService.class,IAlbianDataRouterParserService.Name);
		IDataRoutersAttribute routings = adrps.getDataRouterAttribute(interName);
		
		IAlbianObjectAttribute albianObject = amps.getAlbianObjectAttribute(interName);
		if (null == albianObject) {
			AlbianServiceRouter.getLogger().errorAndThrow(
					IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, "DataService is error.",
					"albian-object:%s attribute is not found.job id:%s.",
					className,writerJob.getId());
		}
		PropertyDescriptor[] propertyDesc =amps.getAlbianObjectPropertyDescriptor(className);
		if (null == propertyDesc) {
			AlbianServiceRouter.getLogger().errorAndThrow(
					IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, "DataService is error.",
					"albian-object:%s PropertyDescriptor is not found.job id:%s.",
					className,writerJob.getId());
		}
		Map<String, Object> mapValue = buildSqlParameter(writerJob.getId(),object, albianObject,
				propertyDesc);

		List<IDataRouterAttribute> useRoutings = parserRoutings(writerJob.getId(),object,
				routings, albianObject);

		IAlbianStorageParserService asps = AlbianServiceRouter.getSingletonService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
		

		
		for (IDataRouterAttribute routing : useRoutings) {
			String storageName = parserRoutingStorage(writerJob.getId(),object, routing,
					routings.getDataRouter(), albianObject);

			IStorageAttribute storage =asps.getStorageAttribute(storageName);
			
			String database = parserRoutingDatabase(writerJob.getId(),object, storage,
					routings.getDataRouter(), albianObject);


			IPersistenceCommand cmd = update.builder(object, routings, albianObject,
					mapValue, routing, storage);

			String key = storageName + database;
			if (null == cmd)
				continue;// no the upload operator
			if (Validate.isNull(writerJob.getWriterTasks())) {
				Map<String, IWriterTask> tasks = new LinkedHashMap<String, IWriterTask>();
				IWriterTask task = new WriterTask();
				List<IPersistenceCommand> cmds = new Vector<IPersistenceCommand>();
				cmds.add(cmd);
				task.setCommands(cmds);
				task.setStorage(new RunningStorageAttribute(storage, database));
				tasks.put(key, task);
				writerJob.setWriterTasks(tasks);
			} else {
				if (writerJob.getWriterTasks().containsKey(key)) {
					writerJob.getWriterTasks().get(key).getCommands()
							.add(cmd);
				} else {
					IWriterTask task = new WriterTask();
					List<IPersistenceCommand> cmds = new Vector<IPersistenceCommand>();
					cmds.add(cmd);
					task.setCommands(cmds);
					task.setStorage(new RunningStorageAttribute(storage, database));
					writerJob.getWriterTasks().put(key, task);
				}
			}
		}
	}
	
	protected void buildWriterJob(IAlbianObject object, IWriterJob writerJob,
			IPersistenceUpdateCommand update,String[] members) throws AlbianDataServiceException {
		String className = object.getClass().getName();

		IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		
		String interName = amps.getAlbianObjectInterface(className);
//		String interName = (String) AlbianObjectInheritMap.get(className);
		IAlbianDataRouterParserService adrps = AlbianServiceRouter.getSingletonService(IAlbianDataRouterParserService.class,IAlbianDataRouterParserService.Name);
		IDataRoutersAttribute routings = adrps.getDataRouterAttribute(interName);
		
		IAlbianObjectAttribute albianObject = amps.getAlbianObjectAttribute(interName);
		
//		IAlbianObjectAttribute albianObject = (IAlbianObjectAttribute) AlbianObjectsMap
//				.get(interName);
		if (null == albianObject) {
			AlbianServiceRouter.getLogger().errorAndThrow(
					IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, "DataService is error.",
					"albian-object:%s attribute is not found.job id:%s.",
					className,writerJob.getId());
		}
		PropertyDescriptor[] propertyDesc =amps.getAlbianObjectPropertyDescriptor(className);
		if (null == propertyDesc) {
			AlbianServiceRouter.getLogger().errorAndThrow(
					IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, "DataService is error.",
					"albian-object:%s PropertyDescriptor is not found.job id:%s.",
					className,writerJob.getId());
		}
		Map<String, Object> mapValue = buildSqlParameter(writerJob.getId(),object, albianObject,
				propertyDesc);

		List<IDataRouterAttribute> useRoutings = parserRoutings(writerJob.getId(),object,
				routings, albianObject);
		
		IAlbianStorageParserService asps = AlbianServiceRouter.getSingletonService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);

		for (IDataRouterAttribute routing : useRoutings) {
			String storageName = parserRoutingStorage(writerJob.getId(),object, routing,
					routings.getDataRouter(), albianObject);

			IStorageAttribute storage =asps.getStorageAttribute(storageName);

			String database = parserRoutingDatabase(writerJob.getId(),object, storage,
					routings.getDataRouter(), albianObject);
			
			String key = storageName + database;
			
			IPersistenceCommand cmd = null;
			try {
				cmd = update.builder(object, routings, albianObject,
						mapValue, routing, storage,members);
			} catch (NoSuchMethodException e) {
				AlbianServiceRouter.getLogger().errorAndThrow(
						IAlbianLoggerService.AlbianSqlLoggerName,
						AlbianDataServiceException.class, "DataService is error.",
						"this sb-function is not impl",
						className,writerJob.getId());
			}

			if (null == cmd)
				continue;// no the upload operator
			if (Validate.isNull(writerJob.getWriterTasks())) {
				Map<String, IWriterTask> tasks = new LinkedHashMap<String, IWriterTask>();
				IWriterTask task = new WriterTask();
				List<IPersistenceCommand> cmds = new Vector<IPersistenceCommand>();
				cmds.add(cmd);
				task.setCommands(cmds);
				task.setStorage(new RunningStorageAttribute(storage, database));
				tasks.put(key, task);
				writerJob.setWriterTasks(tasks);
			} else {
				if (writerJob.getWriterTasks().containsKey(key)) {
					writerJob.getWriterTasks().get(key).getCommands()
							.add(cmd);
				} else {
					IWriterTask task = new WriterTask();
					List<IPersistenceCommand> cmds = new Vector<IPersistenceCommand>();
					cmds.add(cmd);
					task.setCommands(cmds);
					task.setStorage(new RunningStorageAttribute(storage, database));
					writerJob.getWriterTasks().put(key, task);
				}
			}
		}
	}

	protected Map<String, Object> buildSqlParameter(String jobId,IAlbianObject object,
			IAlbianObjectAttribute albianObject,
			PropertyDescriptor[] propertyDesc) throws AlbianDataServiceException {
		Map<String, Object> mapValue = new HashMap<String, Object>();
		String name = "";
		for (PropertyDescriptor p : propertyDesc) {
			try {
				name = p.getName();
				if ("string".equalsIgnoreCase(p.getPropertyType()
						.getSimpleName())) {
					Object oValue = p.getReadMethod().invoke(object);
					if (null == oValue) {
						mapValue.put(name, null);
					} else {
						String value = oValue.toString();
						IMemberAttribute member = albianObject.getMembers()
								.get(name.toLowerCase());
						if ((-1 == member.getLength()) || (member.getLength() >= value.length())) {
							mapValue.put(name, value);
						} else {
							mapValue.put(p.getName(),
									value.substring(0, member.getLength()));
						}
					}
				} else {
					mapValue.put(name, p.getReadMethod().invoke(object));
				}

			} catch (Exception e) {
				AlbianServiceRouter.getLogger().errorAndThrow(
						IAlbianLoggerService.AlbianSqlLoggerName,
						AlbianDataServiceException.class, e, "DataService is error.",
						"invoke bean read method is error.the property is:%s.job id:%s.",
						albianObject.getType(), name,jobId);
			}
		}
		return mapValue;
	}

	protected List<IDataRouterAttribute> parserRoutings(String jobId,IAlbianObject object,
			IDataRoutersAttribute routings, IAlbianObjectAttribute albianObject) {
		List<IDataRouterAttribute> useRoutings = new Vector<IDataRouterAttribute>();
		if (null == routings) {
			IDataRouterAttribute dra = albianObject.getDefaultRouting();
			AlbianServiceRouter.getLogger()
			.warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"albian-object:%s writer-data-routers are null then use default storage:%s.job id:%s.",
					albianObject.getType(), dra.getName(),jobId);
			useRoutings.add(dra);
		} else {
			if (Validate.isNullOrEmpty(routings.getWriterRouters())) {
				IDataRouterAttribute dra = albianObject.getDefaultRouting();
				AlbianServiceRouter.getLogger()
				.warn(IAlbianLoggerService.AlbianSqlLoggerName,
						"albian-object:%s writer-data-routers are null or empty then use default storage:%s.job id:%s.",
						albianObject.getType(), dra.getName(),jobId);
				useRoutings.add(dra);
			} else {
				if (routings.getWriterRouterEnable()) {
					IAlbianObjectDataRouter hashMapping = routings
							.getDataRouter();
					if (null == hashMapping) {
						Map<String, IDataRouterAttribute> wrs = routings.getWriterRouters();
						List<IDataRouterAttribute> ras = new Vector<IDataRouterAttribute>();
						for( IDataRouterAttribute dra : wrs.values() ) {
							if(dra.getEnable()){
								ras = new Vector<IDataRouterAttribute>();
								useRoutings.add(dra);
								
								AlbianServiceRouter.getLogger()
								.warn(IAlbianLoggerService.AlbianSqlLoggerName,
										"albian-object:%s writer-data-router arithmetic is null then use default storage:%s.job id:%s.",
										albianObject.getType(), dra.getName(),jobId);
								
								break;
								//return ras;
							}
						}
						
						
						
						
//						IDataRouterAttribute dra = albianObject.getDefaultRouting();
//						AlbianServiceRouter.getLogger()
//						.warn(IAlbianLoggerService.AlbianSqlLoggerName,
//								"albian-object:%s writer-data-router arithmetic is null then use default storage:%s.job id:%s.",
//								albianObject.getType(), dra.getName(),jobId);
//						useRoutings.add(dra);
					} else {
						List<IDataRouterAttribute> writerRoutings = hashMapping
								.mappingWriterRouting(
										routings.getWriterRouters(), object);
						if (Validate.isNullOrEmpty(writerRoutings)) {
							IDataRouterAttribute dra = albianObject.getDefaultRouting();
							AlbianServiceRouter.getLogger()
							.warn(IAlbianLoggerService.AlbianSqlLoggerName,
									"albian-object:%s writer-data-router arithmetic is null then use default storage:%s.job id:%s.",
									albianObject.getType(), dra.getName(),jobId);
							useRoutings.add(dra);
						} else {
							for (IDataRouterAttribute writerRouting : writerRoutings) {
								if (writerRouting.getEnable()) {
									useRoutings.add(writerRouting);
								}
							}
							if (Validate.isNullOrEmpty(useRoutings)) {
								IDataRouterAttribute dra = albianObject.getDefaultRouting();
								AlbianServiceRouter.getLogger()
								.warn(IAlbianLoggerService.AlbianSqlLoggerName,
										"albian-object:%s writer-data-routers is disable then use default storage:%s.job id:%s.",
										albianObject.getType(), dra.getName(),jobId);
								useRoutings.add(dra);
							}
						}
					}
				}
			}
		}
		return useRoutings;
	}

	protected String parserRoutingStorage(String jobId,IAlbianObject obj,
			IDataRouterAttribute routing, IAlbianObjectDataRouter hashMapping,
			IAlbianObjectAttribute albianObject) throws AlbianDataServiceException {
		if (null == routing) {
			AlbianServiceRouter.getLogger().errorAndThrow(
					IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, "DataService is error.",
					"the writer data router of object:%s is null.job id:%s.",
					albianObject.getType(),jobId);
		}

		if (null == hashMapping) {
			String name = routing.getStorageName();
			AlbianServiceRouter.getLogger()
					.warn(IAlbianLoggerService.AlbianSqlLoggerName,
							"albian-object:%s writer-data-router arithmetic is not found and use default storage:%s.job id:%s.",
							albianObject.getType(), name,jobId);
			return name;
		} else {
			String name = hashMapping.mappingWriterRoutingStorage(routing, obj);
			if (Validate.isNullOrEmpty(name)) {
				String dname = routing.getStorageName();
				AlbianServiceRouter.getLogger()
						.warn(IAlbianLoggerService.AlbianSqlLoggerName,
								"albian-object:%s writer-data-router is not found by arithmetic and use default storage:%s.job id:%s.",
								albianObject.getType(), dname,jobId);
				return dname;
			} else {
				return name;
			}
		}
	}
	
	protected String parserRoutingDatabase(String jobId,IAlbianObject obj,
			IStorageAttribute storage, IAlbianObjectDataRouter hashMapping,
			IAlbianObjectAttribute albianObject) throws AlbianDataServiceException {
		if (null == storage) {
			AlbianServiceRouter.getLogger().errorAndThrow(
					IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, "DataService is error.",
					"the writer data router of object:%s is null.job id:%s.",
					albianObject.getType(),jobId);
		}

		if (null == hashMapping) {
			String name = storage.getDatabase();
			AlbianServiceRouter.getLogger()
					.warn(IAlbianLoggerService.AlbianSqlLoggerName,
							"albian-object:%s writer-data-router arithmetic is not found and use default database:%s.job id:%s.",
							albianObject.getType(), name,jobId);
			return name;
		} else {
			String name = hashMapping.mappingWriterRoutingDatabase(storage, obj);
			if (Validate.isNullOrEmpty(name)) {
				String dname =  storage.getDatabase();
				AlbianServiceRouter.getLogger()
						.warn(IAlbianLoggerService.AlbianSqlLoggerName,
								"albian-object:%s writer-data-router is not found by arithmetic and use default database:%s.job id:%s.",
								albianObject.getType(), dname,jobId);
				return dname;
			} else {
				return name;
			}
		}
	}


}
