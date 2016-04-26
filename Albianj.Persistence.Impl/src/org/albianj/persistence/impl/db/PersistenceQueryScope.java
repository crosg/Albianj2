package org.albianj.persistence.impl.db;

import java.beans.PropertyDescriptor;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.context.IReaderJob;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.db.PersistenceCommandType;
import org.albianj.persistence.impl.toolkit.ListConvert;
import org.albianj.persistence.impl.toolkit.ResultConvert;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IMemberAttribute;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.service.IAlbianMappingParserService;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

public class PersistenceQueryScope extends FreePersistenceQueryScope implements IPersistenceQueryScope {
	protected void perExecute(IReaderJob job) throws AlbianDataServiceException {
		PersistenceNamedParameter.parseSql(job.getCommand());
		IRunningStorageAttribute rsa = job.getStorage();
		IAlbianStorageParserService asps = AlbianServiceRouter.getService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
		job.setConnection(asps.getConnection(rsa));
		IPersistenceCommand cmd = job.getCommand();
		PreparedStatement statement = null;
		try {
			statement = job.getConnection().prepareStatement(cmd.getCommandText());
		} catch (SQLException e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, e, "DataService is error.",
					"get the statement is error.job id:%s.", job.getId());
		}
		Map<Integer, String> map = cmd.getParameterMapper();
		if (!Validate.isNullOrEmpty(map)) {
			for (int i = 1; i <= map.size(); i++) {
				String paraName = map.get(i);
				ISqlParameter para = cmd.getParameters().get(paraName);
				try {
					if (null == para.getValue()) {

						statement.setNull(i, para.getSqlType());
					} else {
						statement.setObject(i, para.getValue(), para.getSqlType());
					}
				} catch (SQLException e) {
					AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
							AlbianDataServiceException.class, e, "DataService is error.",
							"set the sql paras is error.para name:%s,para value:%s.job id:%s.", para.getName(),
							ResultConvert.sqlValueToString(para.getSqlType(), para.getValue()), job.getId());
				}
			}
		}
		job.setStatement(statement);
		return;
	}

	protected void executing(IReaderJob job) throws AlbianDataServiceException {
		String text = job.getCommand().getCommandText();
		Map<String, ISqlParameter> map = job.getCommand().getParameters();
		IRunningStorageAttribute st = job.getStorage();

		ResultSet result = null;
		try {
			AlbianServiceRouter.getLogger().info(IAlbianLoggerService.AlbianSqlLoggerName,
					"job id:%s,Storage:%s,database:%s,SqlText:%s,paras:%s.", job.getId(),
					st.getStorageAttribute().getName(), st.getDatabase(), text, ListConvert.toString(map));
			result = ((PreparedStatement) job.getStatement()).executeQuery();
		} catch (SQLException e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, e, "DataService is error.",
					"execute the reader job is error.job id:%s.", job.getId());
		}
		job.setResult(result);
	}

	protected <T extends IAlbianObject> List<T> executed(Class<T> cls, IReaderJob job)
			throws AlbianDataServiceException {
		return executed(cls, job.getId(), job.getResult());
	}

	protected void unloadExecute(IReaderJob job) throws AlbianDataServiceException {
		try {
			job.getResult().close();
			job.setResult(null);
		} catch (SQLException e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, e, "DataService is error.", "unload job is fail.job id:%s.",
					job.getId());
		} finally {
			try {
				((PreparedStatement) job.getStatement()).clearParameters();
				job.getStatement().close();
				job.setStatement(null);
			} catch (SQLException e) {
				AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianSqlLoggerName, e,
						"close the statement when unload exec job is fail.job id:%s.", job.getId());
			} finally {
				try {
					job.getConnection().close();
				} catch (SQLException e) {
					AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianSqlLoggerName, e,
							"close the connection when unload exec job is fail.job id:%s.", job.getId());
				}
			}
		}
	}

	protected ResultSet executing(PersistenceCommandType cmdType, Statement statement)
			throws AlbianDataServiceException {
		try {
			if (PersistenceCommandType.Text == cmdType) {
				return ((PreparedStatement) statement).executeQuery();
			}
			return ((CallableStatement) statement).executeQuery();
		} catch (SQLException e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, e, "DataService is error.", "execute the reader job is error.");
		}

		return null;
	}

	protected <T extends IAlbianObject> List<T> executed(Class<T> cli, String jobId, ResultSet result)
			throws AlbianDataServiceException {
		String inter = cli.getName();

		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute attr = amps.getAlbianObjectAttribute(inter);
//		IAlbianObjectAttribute attr = (IAlbianObjectAttribute) AlbianObjectsMap.get(inter);
		String className = attr.getType();
		PropertyDescriptor[] propertyDesc =amps.getAlbianObjectPropertyDescriptor(className);
		Map<String, IMemberAttribute> members = attr.getMembers();
		Class<?> cls = null;
		try {
			cls = AlbianClassLoader.getInstance().loadClass(className);
		} catch (ClassNotFoundException e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, e, "DataService is error.", "class:%s is not found.job id:%s.",
					className, jobId);
		}
		List<T> list = new Vector<T>();
		try {
			while (result.next()) {
				try {
					@SuppressWarnings("unchecked")
					T obj = (T) cls.newInstance();
					for (PropertyDescriptor desc : propertyDesc) {
						String name = desc.getName();
						IMemberAttribute ma = members.get(name.toLowerCase());
						if (null == ma)
							continue;
						if (!ma.getIsSave()) {
							if (name.equals("isAlbianNew")) {
								desc.getWriteMethod().invoke(obj, false);
							}
							continue;
						}

						Object v = result.getObject(name);
						if (null != v) {
							Object rc = ResultConvert.toBoxValue(desc.getPropertyType(), v);
							desc.getWriteMethod().invoke(obj, rc);
							obj.setOldAlbianObject(name, rc);
						}
					}
					list.add(obj);
				} catch (Exception e) {
					AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
							AlbianDataServiceException.class, e, "DataService is error.",
							"create object from class:%s is not found.job id:%s.", className, jobId);
				}
			}
		} catch (Exception e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, e, "DataService is error.",
					"loop the result from database for class is error.job id:%s.", className, jobId);
		}

		return list;
	}

	@Override
	protected Object executed(String jobId, IReaderJob job) throws AlbianDataServiceException {
		Object v = null;
		ResultSet result = job.getResult();
		try {
			if (result.next()) {
				v = result.getObject("COUNT");
			}
		} catch (Exception e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, e, "DataService is error.", "get pagesize is null.job id:%s.",
					jobId);
		}

		return v;
	}
}