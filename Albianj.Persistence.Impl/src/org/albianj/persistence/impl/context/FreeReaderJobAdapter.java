package org.albianj.persistence.impl.context;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.context.IReaderJob;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.db.PersistenceCommandType;
import org.albianj.persistence.impl.db.PersistenceCommand;
import org.albianj.persistence.impl.db.SqlParameter;
import org.albianj.persistence.impl.toolkit.Convert;
import org.albianj.persistence.impl.toolkit.EnumMapping;
import org.albianj.persistence.impl.toolkit.ListConvert;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.persistence.object.IDataRoutersAttribute;
import org.albianj.persistence.object.IFilterCondition;
import org.albianj.persistence.object.IMemberAttribute;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.object.PersistenceDatabaseStyle;
import org.albianj.persistence.object.RunningStorageAttribute;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.persistence.service.IAlbianDataRouterParserService;
import org.albianj.persistence.service.IAlbianMappingParserService;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

public abstract class FreeReaderJobAdapter implements IReaderJobAdapter {
	
	@Deprecated
	public IReaderJob buildReaderJob(String sessionId,Class<?> cls,boolean isExact, String routingName,
			int start, int step, LinkedList<IFilterCondition> wheres,
			LinkedList<IOrderByCondition> orderbys) throws AlbianDataServiceException {
		IReaderJob job = new ReaderJob(sessionId);
		String className = cls.getName();
		IAlbianDataRouterParserService adrps = AlbianServiceRouter.getService(IAlbianDataRouterParserService.class,IAlbianDataRouterParserService.Name);
		IDataRoutersAttribute routings = adrps.getDataRouterAttribute(className);

		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute albianObject = amps.getAlbianObjectAttribute(className);
		
//		IAlbianObjectAttribute albianObject = (IAlbianObjectAttribute) AlbianObjectsMap
//				.get(className);
		if (null == albianObject) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
					AlbianDataServiceException.class, "DataService is error.",
					"albian-object:%s attribute is not found.job is:%s.", className,job.getId());
		}

		Map<String, IFilterCondition> hashWheres = ListConvert
				.toLinkedHashMap(wheres);
		Map<String, IOrderByCondition> hashOrderbys = ListConvert
				.toLinkedHashMap(orderbys);

		IDataRouterAttribute readerRouting = parserReaderRouting(cls,job.getId(), isExact,routingName,
				hashWheres, hashOrderbys);
		if (null == readerRouting) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
					AlbianDataServiceException.class, "DataService is error.",
					"albian-object:%s reader-data-router is not found.job id:%s.", className,job.getId());
		}
		String storageName = parserRoutingStorage(cls, job.getId(),isExact,readerRouting,
				hashWheres, hashOrderbys);
		
		IAlbianStorageParserService asps = AlbianServiceRouter.getService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
		IStorageAttribute storage =asps.getStorageAttribute(storageName);

		StringBuilder sbCols = new StringBuilder();
		StringBuilder sbWhere = new StringBuilder();
		StringBuilder sbOrderby = new StringBuilder();
		StringBuilder sbStatement = new StringBuilder();
		Map<String, ISqlParameter> paras = new HashMap<String, ISqlParameter>();
		for (String key : albianObject.getMembers().keySet()) {
			IMemberAttribute member = albianObject.getMembers().get(key);
			if(null == member) {
				AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
						AlbianDataServiceException.class, "DataService is error.",
						"albian-object:%s member:%s is not found.job id:%s.", className,key,job.getId());
			}
			if (!member.getIsSave())
				continue;
			if (member.getSqlFieldName().equals(member.getName())) {
				if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
					sbCols.append("`").append(member.getSqlFieldName()).append("`").append(",");
				} else {
					sbCols.append("[").append( member.getSqlFieldName()).append("]").append(",");
				}
			} else {
				if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
					sbCols.append("`").append(member.getSqlFieldName()).append("`")
							.append(" AS ")
							.append("`").append(member.getSqlFieldName()).append("`").append(",");
				} else {
					sbCols.append("[").append( member.getSqlFieldName()).append("]")
							.append(" AS ")
							.append("[").append( member.getSqlFieldName()).append("]").append(",");
				}
			}
		}
		if (0 != sbCols.length())
			sbCols.deleteCharAt(sbCols.length() - 1);
		if (null != wheres) {
			for (IFilterCondition where : wheres) {
				if(where.isAddition()) continue;
				IMemberAttribute member = albianObject.getMembers().get(
						where.getFieldName().toLowerCase());
				
				if(null == member) {
					AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
							AlbianDataServiceException.class, "DataService is error.",
							"albian-object:%s member:%s is not found.job id:%s.", className,where.getFieldName(),job.getId());
				}
				
				sbWhere.append(" ")
						.append(EnumMapping.toRelationalOperators(where
										.getRelationalOperator()))
						.append(where.isBeginSub() ? "(" : " ");
				if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
					sbWhere.append("`").append(member.getSqlFieldName()).append("`");
				} else {
					sbWhere.append("[").append( member.getSqlFieldName()).append("]");
				}
				sbWhere.append(
						EnumMapping.toLogicalOperation(where
								.getLogicalOperation())).append("#")
				.append(Validate.isNullOrEmptyOrAllSpace(where.getAliasName()) ? member.getSqlFieldName() : where.getAliasName() )
					//	.append(member.getSqlFieldName())
						.append("#")
						.append(where.isCloseSub() ? ")" : "");
				ISqlParameter para = new SqlParameter();
				para.setName(member.getSqlFieldName());
				para.setSqlFieldName(member.getSqlFieldName());
				if(null == where.getFieldClass()) {
					para.setSqlType(member.getDatabaseType());
				} else {
					para.setSqlType(Convert.toSqlType(where.getFieldClass()));
				}
				para.setValue(where.getValue());
				paras.put(String.format("#%1$s#", Validate.isNullOrEmptyOrAllSpace(where.getAliasName()) ? member.getSqlFieldName() : where.getAliasName()),
						para);
				
				//paras.put(String.format("#%1$s#", member.getSqlFieldName()),
				//		para);
			}
		}
		if (null != orderbys) {
			for (IOrderByCondition orderby : orderbys) {
				IMemberAttribute member = albianObject.getMembers().get(
						orderby.getFieldName().toLowerCase());
				if(null == member) {
					AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
							AlbianDataServiceException.class, "DataService is error.",
							"albian-object:%s member:%s is not found.job id:%s.", className,orderby.getFieldName(),job.getId());
				}
				
				if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
					sbOrderby.append("`").append(member.getSqlFieldName()).append("`");
				} else {
					sbOrderby.append("[").append( member.getSqlFieldName()).append("]");
				}
				sbOrderby
						.append(" ")
						.append(EnumMapping.toSortOperation(orderby
								.getSortStyle())).append(",");
			}
		}
		if (0 != sbOrderby.length())
			sbOrderby.deleteCharAt(sbOrderby.length() - 1);
		String tableName = null;
		if (null == routings || null == routings.getDataRouter()) {
			tableName = readerRouting.getTableName();
		} else {

			tableName = isExact ?  routings.getDataRouter().mappingExactReaderTable(
					readerRouting, hashWheres, hashOrderbys) 
					: routings.getDataRouter().mappingReaderTable(
					readerRouting, hashWheres, hashOrderbys);

			tableName = Validate.isNullOrEmptyOrAllSpace(tableName) ? readerRouting
					.getTableName() : tableName;
		}
		
		String database = null;
		if (null == routings || null == routings.getDataRouter()) {
			database = storage.getDatabase();
		} else {

			database = isExact ?  routings.getDataRouter().mappingExactReaderRoutingDatabase(storage, hashWheres, hashOrderbys) 
					: routings.getDataRouter().mappingReaderRoutingDatabase(storage, hashWheres, hashOrderbys);

			database = Validate.isNullOrEmptyOrAllSpace(database) ? storage.getDatabase() : database;
		}

		sbStatement.append("SELECT ").append(sbCols).append(" FROM ");
		if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
			sbStatement.append("`").append(tableName).append("`");
		} else {
			sbStatement.append("[").append(tableName).append("]");
		}
		sbStatement.append(" WHERE 1=1 ").append(sbWhere);
		if (0 != sbOrderby.length()) {
			sbStatement.append(" ORDER BY ").append(sbOrderby);
		}
		if (0 <= start && 0 < step) {
			sbStatement.append(" LIMIT ").append(start).append(", ")
					.append(step);
		}
		if (0 > start && 0 < step) {
			sbStatement.append(" LIMIT ").append(step);
		}

		//AlbianLoggerService.debug(sbStatement.toString());

		IPersistenceCommand cmd = new PersistenceCommand();
		cmd.setCommandText(sbStatement.toString());
		cmd.setParameters(paras);
		cmd.setCommandType(PersistenceCommandType.Text);

		
		job.setCommand(cmd);
		job.setStorage(new RunningStorageAttribute(storage, database));
		return job;
	}
	
	@Deprecated
	public IReaderJob buildReaderJob(String sessionId,Class<?> cls,boolean isExact,String routingName,
			 LinkedList<IFilterCondition> wheres,LinkedList<IOrderByCondition> orderbys) throws AlbianDataServiceException {
		IReaderJob job = new ReaderJob(sessionId);
		String className = cls.getName();
		IAlbianDataRouterParserService adrps = AlbianServiceRouter.getService(IAlbianDataRouterParserService.class,IAlbianDataRouterParserService.Name);
		IDataRoutersAttribute routings = adrps.getDataRouterAttribute(className);
		
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute albianObject = amps.getAlbianObjectAttribute(className);
		

//		IAlbianObjectAttribute albianObject = (IAlbianObjectAttribute) AlbianObjectsMap
//				.get(className);
		if (null == albianObject) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
					AlbianDataServiceException.class, "DataService is error.",
					"albian-object:%s attribute is not found.job is:%s.", className,job.getId());
		}

		Map<String, IFilterCondition> hashWheres = ListConvert
				.toLinkedHashMap(wheres);
		Map<String, IOrderByCondition> hashOrderbys = ListConvert
				.toLinkedHashMap(orderbys);

		IDataRouterAttribute readerRouting = parserReaderRouting(cls,job.getId(), isExact,routingName,
				hashWheres, hashOrderbys);
		if (null == readerRouting) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
					AlbianDataServiceException.class, "DataService is error.",
					"albian-object:%s reader-data-router is not found.job id:%s.", className,job.getId());
		}
		String storageName = parserRoutingStorage(cls, job.getId(),isExact,readerRouting,
				hashWheres, hashOrderbys);
		
		IAlbianStorageParserService asps = AlbianServiceRouter.getService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
		
		IStorageAttribute storage = asps.getStorageAttribute(storageName);
		

		StringBuilder sbCols = new StringBuilder();
		StringBuilder sbWhere = new StringBuilder();
		StringBuilder sbStatement = new StringBuilder();
		Map<String, ISqlParameter> paras = new HashMap<String, ISqlParameter>();

				if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
					sbCols.append(" COUNT(1) ")
							.append(" AS ")
							.append(" `COUNT` ");
				} else {
					sbCols.append(" COUNT(1) ")
							.append(" AS ")
							.append(" [COUNT] ");
				}
				
		if (null != wheres) {
			for (IFilterCondition where : wheres) {
				if(where.isAddition()) continue;
				IMemberAttribute member = albianObject.getMembers().get(
						where.getFieldName().toLowerCase());
				
				if(null == member) {
					AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
							AlbianDataServiceException.class, "DataService is error.",
							"albian-object:%s member:%s is not found.job id:%s.", className,where.getFieldName(),job.getId());
				}
				
				sbWhere.append(" ")
						.append(EnumMapping.toRelationalOperators(where
										.getRelationalOperator()))
						.append(where.isBeginSub() ? "(" : " ");
				if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
					sbWhere.append(" `").append(member.getSqlFieldName()).append("` ");
				} else {
					sbWhere.append(" [").append( member.getSqlFieldName()).append("] ");
				}
				sbWhere.append(
						EnumMapping.toLogicalOperation(where
								.getLogicalOperation())).append("#")
				.append(Validate.isNullOrEmptyOrAllSpace(where.getAliasName()) ? member.getSqlFieldName() : where.getAliasName() )
					//	.append(member.getSqlFieldName())
						.append("#")
						.append(where.isCloseSub() ? ")" : "");
				ISqlParameter para = new SqlParameter();
				para.setName(member.getSqlFieldName());
				para.setSqlFieldName(member.getSqlFieldName());
				if(null == where.getFieldClass()) {
					para.setSqlType(member.getDatabaseType());
				} else {
					para.setSqlType(Convert.toSqlType(where.getFieldClass()));
				}
				para.setValue(where.getValue());
				paras.put(String.format("#%1$s#", Validate.isNullOrEmptyOrAllSpace(where.getAliasName()) ? member.getSqlFieldName() : where.getAliasName()),
						para);
				
				//paras.put(String.format("#%1$s#", member.getSqlFieldName()),
				//		para);
			}
		}
		String tableName = null;
		if (null == routings || null == routings.getDataRouter()) {
			tableName = readerRouting.getTableName();
		} else {

			tableName = isExact ?  routings.getDataRouter().mappingExactReaderTable(
					readerRouting, hashWheres, hashOrderbys) 
					: routings.getDataRouter().mappingReaderTable(
					readerRouting, hashWheres, hashOrderbys);

			tableName = Validate.isNullOrEmptyOrAllSpace(tableName) ? readerRouting
					.getTableName() : tableName;
		}
		
		String database = null;
		if (null == routings || null == routings.getDataRouter()) {
			database = storage.getDatabase();
		} else {

			database = isExact ?  routings.getDataRouter().mappingExactReaderRoutingDatabase(storage, hashWheres, hashOrderbys) 
					: routings.getDataRouter().mappingReaderRoutingDatabase(storage, hashWheres, hashOrderbys);

			database = Validate.isNullOrEmptyOrAllSpace(database) ? storage.getDatabase() : database;
		}

		sbStatement.append("SELECT ").append(sbCols).append(" FROM ");
		if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
			sbStatement.append("`").append(tableName).append("`");
		} else {
			sbStatement.append("[").append(tableName).append("]");
		}
		sbStatement.append(" WHERE 1=1 ").append(sbWhere);

		//AlbianLoggerService.debug(sbStatement.toString());

		IPersistenceCommand cmd = new PersistenceCommand();
		cmd.setCommandText(sbStatement.toString());
		cmd.setParameters(paras);
		cmd.setCommandType(PersistenceCommandType.Text);

		
		job.setCommand(cmd);
		job.setStorage(new RunningStorageAttribute(storage, database));
		return job;

	}
	
	protected abstract IDataRouterAttribute parserReaderRouting(Class<?> cls,String jobId,boolean isExact,
			String routingName, Map<String, IFilterCondition> hashWheres,
			Map<String, IOrderByCondition> hashOrderbys) throws AlbianDataServiceException;

	protected abstract String parserRoutingStorage(Class<?> cls,String jobId,boolean isExact,
			IDataRouterAttribute readerRouting,
			Map<String, IFilterCondition> hashWheres,
			Map<String, IOrderByCondition> hashOrderbys) throws AlbianDataServiceException;

	public IReaderJob buildReaderJob(String sessionId,Class<?> cls,boolean isExact, String routingName,
			int start, int step, IChainExpression f,
			LinkedList<IOrderByCondition> orderbys) throws AlbianDataServiceException {
		IReaderJob job = new ReaderJob(sessionId);
		String className = cls.getName();
		IAlbianDataRouterParserService adrps = AlbianServiceRouter.getService(IAlbianDataRouterParserService.class,IAlbianDataRouterParserService.Name);
		IDataRoutersAttribute routings = adrps.getDataRouterAttribute(className);

		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute albianObject = amps.getAlbianObjectAttribute(className);
		
		if (null == albianObject) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
					AlbianDataServiceException.class, "DataService is error.",
					"albian-object:%s attribute is not found.job is:%s.", className,job.getId());
		}

		
		Map<String, IOrderByCondition> hashOrderbys = ListConvert
				.toLinkedHashMap(orderbys);
		
		Map<String, IFilterCondition> hashWheres = new HashMap<>();
		
		ChainExpressionParser.toFilterConditionMap(f,hashWheres);

		IDataRouterAttribute readerRouting = parserReaderRouting(cls,job.getId(), isExact,routingName,
				hashWheres, hashOrderbys);
		if (null == readerRouting) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
					AlbianDataServiceException.class, "DataService is error.",
					"albian-object:%s reader-data-router is not found.job id:%s.", className,job.getId());
		}
		String storageName = parserRoutingStorage(cls, job.getId(),isExact,readerRouting,
				hashWheres, hashOrderbys);
		
		IAlbianStorageParserService asps = AlbianServiceRouter.getService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
		IStorageAttribute storage =asps.getStorageAttribute(storageName);

		StringBuilder sbCols = new StringBuilder();
		StringBuilder sbWhere = new StringBuilder();
		StringBuilder sbOrderby = new StringBuilder();
		StringBuilder sbStatement = new StringBuilder();
		Map<String, ISqlParameter> paras = new HashMap<String, ISqlParameter>();
		for (String key : albianObject.getMembers().keySet()) {
			IMemberAttribute member = albianObject.getMembers().get(key);
			if(null == member) {
				AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
						AlbianDataServiceException.class, "DataService is error.",
						"albian-object:%s member:%s is not found.job id:%s.", className,key,job.getId());
			}
			if (!member.getIsSave())
				continue;
			if (member.getSqlFieldName().equals(member.getName())) {
				if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
					sbCols.append("`").append(member.getSqlFieldName()).append("`").append(",");
				} else {
					sbCols.append("[").append( member.getSqlFieldName()).append("]").append(",");
				}
			} else {
				if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
					sbCols.append("`").append(member.getSqlFieldName()).append("`")
							.append(" AS ")
							.append("`").append(member.getSqlFieldName()).append("`").append(",");
				} else {
					sbCols.append("[").append( member.getSqlFieldName()).append("]")
							.append(" AS ")
							.append("[").append( member.getSqlFieldName()).append("]").append(",");
				}
			}
		}
		if (0 != sbCols.length())
			sbCols.deleteCharAt(sbCols.length() - 1);
		
		ChainExpressionParser.toConditionText(sessionId,cls,albianObject,storage,f,sbWhere,paras);
		
		if (null != orderbys) {
			for (IOrderByCondition orderby : orderbys) {
				IMemberAttribute member = albianObject.getMembers().get(
						orderby.getFieldName().toLowerCase());
				if(null == member) {
					AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
							AlbianDataServiceException.class, "DataService is error.",
							"albian-object:%s member:%s is not found.job id:%s.", className,orderby.getFieldName(),job.getId());
				}
				
				if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
					sbOrderby.append("`").append(member.getSqlFieldName()).append("`");
				} else {
					sbOrderby.append("[").append( member.getSqlFieldName()).append("]");
				}
				sbOrderby
						.append(" ")
						.append(EnumMapping.toSortOperation(orderby
								.getSortStyle())).append(",");
			}
		}
		if (0 != sbOrderby.length())
			sbOrderby.deleteCharAt(sbOrderby.length() - 1);
		String tableName = null;
		if (null == routings || null == routings.getDataRouter()) {
			tableName = readerRouting.getTableName();
		} else {

			tableName = isExact ?  routings.getDataRouter().mappingExactReaderTable(
					readerRouting, hashWheres, hashOrderbys) 
					: routings.getDataRouter().mappingReaderTable(
					readerRouting, hashWheres, hashOrderbys);

			tableName = Validate.isNullOrEmptyOrAllSpace(tableName) ? readerRouting
					.getTableName() : tableName;
		}
		
		String database = null;
		if (null == routings || null == routings.getDataRouter()) {
			database = storage.getDatabase();
		} else {

			database = isExact ?  routings.getDataRouter().mappingExactReaderRoutingDatabase(storage, hashWheres, hashOrderbys) 
					: routings.getDataRouter().mappingReaderRoutingDatabase(storage, hashWheres, hashOrderbys);

			database = Validate.isNullOrEmptyOrAllSpace(database) ? storage.getDatabase() : database;
		}

		sbStatement.append("SELECT ").append(sbCols).append(" FROM ");
		if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
			sbStatement.append("`").append(tableName).append("`");
		} else {
			sbStatement.append("[").append(tableName).append("]");
		}
		sbStatement.append(" WHERE ").append(sbWhere);
		if (0 != sbOrderby.length()) {
			sbStatement.append(" ORDER BY ").append(sbOrderby);
		}
		if (0 <= start && 0 < step) {
			sbStatement.append(" LIMIT ").append(start).append(", ")
					.append(step);
		}
		if (0 > start && 0 < step) {
			sbStatement.append(" LIMIT ").append(step);
		}

		//AlbianLoggerService.debug(sbStatement.toString());

		IPersistenceCommand cmd = new PersistenceCommand();
		cmd.setCommandText(sbStatement.toString());
		cmd.setParameters(paras);
		cmd.setCommandType(PersistenceCommandType.Text);

		
		job.setCommand(cmd);
		job.setStorage(new RunningStorageAttribute(storage, database));
		return job;
	}
	
	public IReaderJob buildReaderJob(String sessionId,Class<?> cls,boolean isExact, String routingName,
			 IChainExpression f,
			LinkedList<IOrderByCondition> orderbys) throws AlbianDataServiceException {
		IReaderJob job = new ReaderJob(sessionId);
		String className = cls.getName();
		IAlbianDataRouterParserService adrps = AlbianServiceRouter.getService(IAlbianDataRouterParserService.class,IAlbianDataRouterParserService.Name);
		IDataRoutersAttribute routings = adrps.getDataRouterAttribute(className);

		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute albianObject = amps.getAlbianObjectAttribute(className);
		
		if (null == albianObject) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
					AlbianDataServiceException.class, "DataService is error.",
					"albian-object:%s attribute is not found.job is:%s.", className,job.getId());
		}

		
		Map<String, IOrderByCondition> hashOrderbys = ListConvert
				.toLinkedHashMap(orderbys);
		
		Map<String, IFilterCondition> hashWheres = new HashMap<>();
		
		ChainExpressionParser.toFilterConditionMap(f,hashWheres);

		IDataRouterAttribute readerRouting = parserReaderRouting(cls,job.getId(), isExact,routingName,
				hashWheres, hashOrderbys);
		if (null == readerRouting) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
					AlbianDataServiceException.class, "DataService is error.",
					"albian-object:%s reader-data-router is not found.job id:%s.", className,job.getId());
		}
		String storageName = parserRoutingStorage(cls, job.getId(),isExact,readerRouting,
				hashWheres, hashOrderbys);
		
		IAlbianStorageParserService asps = AlbianServiceRouter.getService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
		IStorageAttribute storage =asps.getStorageAttribute(storageName);

		StringBuilder sbCols = new StringBuilder();
		StringBuilder sbWhere = new StringBuilder();
		StringBuilder sbOrderby = new StringBuilder();
		StringBuilder sbStatement = new StringBuilder();
		Map<String, ISqlParameter> paras = new HashMap<String, ISqlParameter>();
		
		if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
			sbCols.append(" COUNT(1) ")
					.append(" AS ")
					.append(" `COUNT` ");
		} else {
			sbCols.append(" COUNT(1) ")
					.append(" AS ")
					.append(" [COUNT] ");
		}
		
		ChainExpressionParser.toConditionText(sessionId,cls,albianObject,storage,f,sbWhere,paras);
		
		if (null != orderbys) {
			for (IOrderByCondition orderby : orderbys) {
				IMemberAttribute member = albianObject.getMembers().get(
						orderby.getFieldName().toLowerCase());
				if(null == member) {
					AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
							AlbianDataServiceException.class, "DataService is error.",
							"albian-object:%s member:%s is not found.job id:%s.", className,orderby.getFieldName(),job.getId());
				}
				
				if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
					sbOrderby.append("`").append(member.getSqlFieldName()).append("`");
				} else {
					sbOrderby.append("[").append( member.getSqlFieldName()).append("]");
				}
				sbOrderby
						.append(" ")
						.append(EnumMapping.toSortOperation(orderby
								.getSortStyle())).append(",");
			}
		}
		if (0 != sbOrderby.length())
			sbOrderby.deleteCharAt(sbOrderby.length() - 1);
		String tableName = null;
		if (null == routings || null == routings.getDataRouter()) {
			tableName = readerRouting.getTableName();
		} else {

			tableName = isExact ?  routings.getDataRouter().mappingExactReaderTable(
					readerRouting, hashWheres, hashOrderbys) 
					: routings.getDataRouter().mappingReaderTable(
					readerRouting, hashWheres, hashOrderbys);

			tableName = Validate.isNullOrEmptyOrAllSpace(tableName) ? readerRouting
					.getTableName() : tableName;
		}
		
		String database = null;
		if (null == routings || null == routings.getDataRouter()) {
			database = storage.getDatabase();
		} else {

			database = isExact ?  routings.getDataRouter().mappingExactReaderRoutingDatabase(storage, hashWheres, hashOrderbys) 
					: routings.getDataRouter().mappingReaderRoutingDatabase(storage, hashWheres, hashOrderbys);

			database = Validate.isNullOrEmptyOrAllSpace(database) ? storage.getDatabase() : database;
		}

		sbStatement.append("SELECT ").append(sbCols).append(" FROM ");
		if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
			sbStatement.append("`").append(tableName).append("`");
		} else {
			sbStatement.append("[").append(tableName).append("]");
		}
		sbStatement.append(" WHERE ").append(sbWhere);
		if (0 != sbOrderby.length()) {
			sbStatement.append(" ORDER BY ").append(sbOrderby);
		}
		

		//AlbianLoggerService.debug(sbStatement.toString());

		IPersistenceCommand cmd = new PersistenceCommand();
		cmd.setCommandText(sbStatement.toString());
		cmd.setParameters(paras);
		cmd.setCommandType(PersistenceCommandType.Text);

		
		job.setCommand(cmd);
		job.setStorage(new RunningStorageAttribute(storage, database));
		return job;
	}

}
