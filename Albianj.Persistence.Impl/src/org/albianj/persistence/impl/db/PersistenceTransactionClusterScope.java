package org.albianj.persistence.impl.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.context.IWriterJob;
import org.albianj.persistence.context.IWriterTask;
import org.albianj.persistence.context.WriterJobLifeTime;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.impl.toolkit.ListConvert;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

public class PersistenceTransactionClusterScope extends FreePersistenceTransactionClusterScope
		implements IPersistenceTransactionClusterScope {
	protected void preExecute(IWriterJob writerJob) throws AlbianDataServiceException {
		writerJob.setWriterJobLifeTime(WriterJobLifeTime.Opening);
		Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
		if (Validate.isNullOrEmpty(tasks)) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class,
					"DataService is error.","the task for the job is null or empty.job id:%s.",writerJob.getId());
		}

		for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
			writerJob.setCurrentStorage(task.getKey());
			IWriterTask t = task.getValue();
			IRunningStorageAttribute rsa = t.getStorage();
			IStorageAttribute storage = rsa.getStorageAttribute();
			if (null == storage) {
				AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
						AlbianDataServiceException.class,
						"DataService is error.","The storage for task is null.job id:%s.",writerJob.getId());
			}
			try {
				IAlbianStorageParserService asps = AlbianServiceRouter.getService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
				t.setConnection(asps.getConnection(rsa));
			} catch (Exception e) {
				AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
						AlbianDataServiceException.class,
						"DataService is error.","get the connect to storage:%s is error.job id:%s.",
						storage.getName(),writerJob.getId());
			}
			List<IPersistenceCommand> cmds = t.getCommands();
			if (Validate.isNullOrEmpty(cmds)) {
				AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
						AlbianDataServiceException.class,
						"DataService is error.","The commands for task is empty or null.job id:%s.",writerJob.getId());
			}
			List<Statement> statements = new Vector<Statement>();
			try {
			for (IPersistenceCommand cmd : cmds) {
				PreparedStatement prepareStatement = t
						.getConnection().prepareStatement(cmd.getCommandText());
				Map<Integer, String> map = cmd.getParameterMapper();
				if (Validate.isNullOrEmpty(map)) {
					continue;
				} else {
					for (int i = 1; i <= map.size(); i++) {
						String paraName = map.get(i);
						ISqlParameter para = cmd.getParameters().get(paraName);
						if (null == para.getValue()) {
							prepareStatement.setNull(i, para.getSqlType());
						} else {
							prepareStatement.setObject(i, para.getValue(),
									para.getSqlType());
						}
					}
				}
				statements.add(prepareStatement);
			}
			}catch(SQLException e){
				AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
						AlbianDataServiceException.class,e,
						"DataService is error.","make sql command for task is empty or null.job id:%s.",writerJob.getId());
			}
			t.setStatements(statements);
		}
	}

	protected void executeHandler(IWriterJob writerJob) throws AlbianDataServiceException {
		writerJob.setWriterJobLifeTime(WriterJobLifeTime.Running);
		Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
		if (Validate.isNullOrEmpty(tasks)) {
			throw new RuntimeException("The task is null or empty.");
		}
		
		for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
			IWriterTask t = task.getValue();
			writerJob.setCurrentStorage(task.getKey());
			List<Statement> statements = t.getStatements();
			List<IPersistenceCommand> cmds = t.getCommands();
			for(int i = 0;i< statements.size();i++){
				try {
					IPersistenceCommand cmd = cmds.get(i);
					AlbianServiceRouter.getLogger().info(IAlbianLoggerService.AlbianSqlLoggerName, 
							"Job id:%s,storage:%s,sqltext:%s,parars:%s.", 
							writerJob.getId(),task.getKey(),cmd.getCommandText(),ListConvert.toString(cmd.getParameters()));
					((PreparedStatement) statements.get(i)).executeUpdate();
				} catch (SQLException e) {
					IRunningStorageAttribute rsa = t.getStorage();
					AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
							AlbianDataServiceException.class,e,
							"DataService is error.","execute to storage:%s dtabase:%s is error.job id:%s.",
							rsa.getStorageAttribute().getName(),rsa.getDatabase(), writerJob.getId());
				}
			}
		}
	}

	protected void commit(IWriterJob writerJob) throws AlbianDataServiceException {
		writerJob.setWriterJobLifeTime(WriterJobLifeTime.Commiting);
		Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
		if (Validate.isNullOrEmpty(tasks)) {
			throw new RuntimeException("The task is null or empty.");
		}
		for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
			IWriterTask t = task.getValue();
			try {
				writerJob.setCurrentStorage(task.getKey());
				t.getConnection().commit();
				t.setIsCommited(true);
				writerJob.setNeedManualRollbackIfException(true);
			} catch (SQLException e) {
				IRunningStorageAttribute rsa = t.getStorage();
				AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
						AlbianDataServiceException.class,e,
						"DataService is error.","commit to storage:%s database:%s is error.job id:%s.", 
						rsa.getStorageAttribute().getName(),rsa.getDatabase(),writerJob.getId());
			}
		}
	}

	protected void exceptionHandler(IWriterJob writerJob) throws AlbianDataServiceException {
		boolean isThrow = false;
		Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
		if (Validate.isNullOrEmpty(tasks)) {
			throw new RuntimeException("The task is null or empty.");
		}
		for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
			IWriterTask t = task.getValue();
			try {
				if(!t.getIsCommited()) {
					t.getConnection().rollback();
				}
			} catch (Exception e) {
				IRunningStorageAttribute rsa = t.getStorage();
				AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianSqlLoggerName,
						e,"rollback to storage:%s database:%s is error.job id:%s.", 
						rsa.getStorageAttribute().getName(),rsa.getDatabase(),writerJob.getId());
				isThrow = true;
			}
		}
		if (isThrow)
			throw new AlbianDataServiceException("DataService is error.");
	}
	
	protected boolean exceptionManualRollback(IWriterJob writerJob) throws AlbianDataServiceException {
		try {
			manualRollbackPreExecute(writerJob);
			manualRollbackExecuteHandler(writerJob);
			manualRollbackCommit(writerJob);
			return true;
		}catch(Exception e){
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianSqlLoggerName,
					e,"manual rollback is error.job id:%s.",writerJob.getId());
			return false;
		}
	}
	

	protected void unLoadExecute(IWriterJob writerJob) throws AlbianDataServiceException {
		boolean isThrow = false;
		Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
		if (Validate.isNullOrEmpty(tasks)) {
			throw new RuntimeException("The task is null or empty.");
		}
		for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
			IWriterTask t = task.getValue();
			try {
				List<Statement> statements = t.getStatements();
				for (Statement statement : statements) {
					try {
						((PreparedStatement) statement).clearParameters();
						statement.close();
					} catch (Exception e) {
						isThrow = true;
						IRunningStorageAttribute rsa = t.getStorage();
						AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianSqlLoggerName,
								e,"clear the statement to storage:%s database:%s is error.job id:%s.", 
								rsa.getStorageAttribute().getName(),rsa.getDatabase(),writerJob.getId());
					}
				}
				t.getConnection().close();
			} catch (Exception exc) {
				isThrow = true;
				IRunningStorageAttribute rsa = t.getStorage();
				AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianSqlLoggerName,
						exc,"close the connect to storage:%s database:%s is error.job id:%s.", 
						rsa.getStorageAttribute().getName(),rsa.getDatabase(),writerJob.getId());
			}
		}
		if (isThrow)
			throw new AlbianDataServiceException(
					"there is error in the unload trancation scope.");
	}
	
	
	private void manualRollbackPreExecute(IWriterJob writerJob) throws AlbianDataServiceException {
		Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
		if (Validate.isNullOrEmpty(tasks)) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class,
					"DataService is error.","the task for the job is null or empty when manual rollbacking job id:%s.",writerJob.getId());
		}

		for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
			IWriterTask t = task.getValue();
			if(!t.getIsCommited()) continue;// not commit then use auto rollback
//			IStorageAttribute storage = t.getStorage();
//			if (null == storage) {
//				AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
//						AlbianDataServiceException.class,
//						"DataService is error.","The storage for task is null when manual rollbacking.job id:%s.",writerJob.getId());
//			}
			
			List<IPersistenceCommand> cmds = t.getCommands();
			if (Validate.isNullOrEmpty(cmds)) {
				AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
						AlbianDataServiceException.class,
						"DataService is error.","The commands for task is empty or null when manual rollbacking.job id:%s.",writerJob.getId());
			}
			List<Statement> statements = new Vector<Statement>();
			try {
			for (IPersistenceCommand cmd : cmds) {
				PreparedStatement prepareStatement = t
						.getConnection().prepareStatement(cmd.getRollbackCommandText());
				Map<Integer, String> map = cmd.getRollbackParameterMapper();
				if (Validate.isNullOrEmpty(map)) {
					continue;
				} else {
					for (int i = 1; i <= map.size(); i++) {
						String paraName = map.get(i);
						ISqlParameter para = cmd.getRollbackParameters().get(paraName);
						if (null == para.getValue()) {
							prepareStatement.setNull(i, para.getSqlType());
						} else {
							prepareStatement.setObject(i, para.getValue(),
									para.getSqlType());
						}
					}
				}
				statements.add(prepareStatement);
			}
			}catch(SQLException e){
				AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
						AlbianDataServiceException.class,e,
						"DataService is error.","make sql command for task is empty or null when maunal rollbacking.job id:%s.",writerJob.getId());
			}
			t.setRollbackStatements(statements);
		}
	}

	private void  manualRollbackExecuteHandler(IWriterJob writerJob) throws AlbianDataServiceException {
		Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
		if (Validate.isNullOrEmpty(tasks)) {
			throw new RuntimeException("The task is null or empty.");
		}
		
		for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
			IWriterTask t = task.getValue();
			if(!t.getIsCommited()) continue;
			List<Statement> statements = t.getRollbackStatements();
			List<IPersistenceCommand> cmds = t.getCommands();
			for(int i = 0;i< statements.size();i++){
				try {
					IPersistenceCommand cmd = cmds.get(i);
					AlbianServiceRouter.getLogger().info(IAlbianLoggerService.AlbianSqlLoggerName, 
							"manual-rollback Job id:%s,storage:%s,sqltext:%s,parars:%s.", 
							writerJob.getId(),task.getKey(),cmd.getRollbackCommandText(),ListConvert.toString(cmd.getRollbackParameters()));
					((PreparedStatement) statements.get(i)).executeUpdate();
				} catch (SQLException e) {
					IRunningStorageAttribute rsa = t.getStorage();
					AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
							AlbianDataServiceException.class,e,
							"DataService is error.","execute to storage:%s database:%s is error when manual rollbacking.job id:%s.", 
							rsa.getStorageAttribute().getName(),rsa.getDatabase(),writerJob.getId());
				}
			}
		}
	}

	private void  manualRollbackCommit(IWriterJob writerJob) throws AlbianDataServiceException {
		Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
		if (Validate.isNullOrEmpty(tasks)) {
			throw new RuntimeException("The task is null or empty.");
		}
		for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
			IWriterTask t = task.getValue();
			if(!t.getIsCommited()) continue;
			try {
				t.getConnection().commit();
			} catch (SQLException e) {
				IRunningStorageAttribute rsa = t.getStorage();
				AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
						AlbianDataServiceException.class,e,
						"DataService is error.","commit to storage:%s database:%s is error when manual rollbacking.job id:%s.",
						rsa.getStorageAttribute().getName(),rsa.getDatabase(),writerJob.getId());
			}
		}
	}
	

}
