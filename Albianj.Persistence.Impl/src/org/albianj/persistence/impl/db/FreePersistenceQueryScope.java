package org.albianj.persistence.impl.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.context.IReaderJob;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.PersistenceCommandType;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.service.AlbianServiceRouter;

public abstract class FreePersistenceQueryScope implements IPersistenceQueryScope {

	public <T extends IAlbianObject> List<T> execute(Class<T> cls,
			IReaderJob job) throws AlbianDataServiceException {
		try {
			perExecute(job);
			executing(job);
			List<T> list = executed(cls, job);
			return list;
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
					AlbianDataServiceException.class, e, "DataService is error.", 
					"exec job is fail.job id:%s.",job.getId());
		} finally {
			unloadExecute(job);
		}
		return null;
	}
	
	public Object execute(
			IReaderJob job) throws AlbianDataServiceException {
		try {
			perExecute(job);
			executing(job);
			Object o = executed(job.getId(), job);
			return o;
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
					AlbianDataServiceException.class, e, "DataService is error.", 
					"exec job is fail.job id:%s.",job.getId());
		} finally {
			unloadExecute(job);
		}
		return null;
	} 

	public <T extends IAlbianObject> List<T> execute(Class<T> cls,
			PersistenceCommandType cmdType, Statement statement) throws AlbianDataServiceException {
		ResultSet result = null;
		List<T> list = null;
		try {
			result = executing(cmdType, statement);
			list = executed(cls,AlbianServiceRouter.getLogIdService().makeJobId(), result);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
					AlbianDataServiceException.class, e, "DataService is error.", 
					"exec job is fail");
		} finally {
			if (null != result)
				try {
					result.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName, 
							AlbianDataServiceException.class, e, "DataService is error.", 
							"close the result from database is error.");
				}
		}
		return list;
	}

	protected abstract void perExecute(IReaderJob job) throws AlbianDataServiceException;

	protected abstract void executing(IReaderJob job) throws AlbianDataServiceException;

	protected abstract <T extends IAlbianObject> List<T> executed(Class<T> cls,
			IReaderJob job) throws AlbianDataServiceException;
	
	protected abstract Object executed(String jobId,IReaderJob job) 
			throws AlbianDataServiceException;

	protected abstract void unloadExecute(IReaderJob job) throws AlbianDataServiceException;

	protected abstract ResultSet executing(PersistenceCommandType cmdType,
			Statement statement) throws AlbianDataServiceException;

	protected abstract <T extends IAlbianObject> List<T> executed(Class<T> cls,String jobId,
			ResultSet result) throws AlbianDataServiceException;
}
