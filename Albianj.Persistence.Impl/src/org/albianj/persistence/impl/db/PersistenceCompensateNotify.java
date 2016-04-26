package org.albianj.persistence.impl.db;

import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.context.IPersistenceCompensateNotify;
import org.albianj.persistence.context.IWriterJob;
import org.albianj.persistence.context.IWriterTask;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.impl.toolkit.ListConvert;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

public class PersistenceCompensateNotify implements IPersistenceCompensateNotify {
	
	private static IPersistenceCompensateNotify notify = null;
	public static synchronized IPersistenceCompensateNotify getInstance(){
	
		if(null == notify)
			notify = new PersistenceCompensateNotify();
		return notify;
		
	}

	@Override
	public void send(boolean isAutoRollbackSuccess, boolean isManualRollbackSuccess, IWriterJob job) {
		StringBuilder sb = null;
		
		try {
			sb = writerJobCommandToString(job) ;
		} catch (AlbianDataServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianSqlLoggerName,
				"the job:%s is not compensate then the all sql is:%s", job.getId(),sb.toString());
		
	}
	
	public StringBuilder writerJobCommandToString(IWriterJob writerJob) throws AlbianDataServiceException{
		StringBuilder sb = new StringBuilder();
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
					IPersistenceCommand cmd = cmds.get(i);
					sb.append("JobId:").append(writerJob.getId())
					.append("Storage:").append(task.getKey())
					.append("SqlText:").append(cmd.getCommandText())
					.append("paras:").append(ListConvert.toString(cmd.getParameters()))
					.append("RollbackText:").append(cmd.getRollbackCommandText())
					.append("RollbackParas:").append(ListConvert.toString(cmd.getRollbackParameters()))
					.append("\n");
					
			}
		}
		
		return sb;
	}

}
