package org.albianj.persistence.impl.db;

import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.context.IPersistenceCompensateNotify;
import org.albianj.persistence.context.IWriterJob;
import org.albianj.persistence.context.WriterJobLifeTime;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.service.AlbianServiceRouter;

public abstract class FreePersistenceTransactionClusterScope implements IPersistenceTransactionClusterScope {

	public boolean execute(IWriterJob writerJob) {
		boolean isSuccess = true;
		boolean isAutoRollbackSuccess = true;
		boolean isManualRollbackSuccess = true;
		StringBuilder sbMsg = new StringBuilder();
		try {
			writerJob.setWriterJobLifeTime(WriterJobLifeTime.NoStarted);
			this.preExecute(writerJob);
			writerJob.setWriterJobLifeTime(WriterJobLifeTime.Opened);
			this.executeHandler(writerJob);
			writerJob.setWriterJobLifeTime(WriterJobLifeTime.Runned);
			this.commit(writerJob);
			writerJob.setWriterJobLifeTime(WriterJobLifeTime.Commited);
		} catch (Exception e) {
			isSuccess = false;
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianSqlLoggerName, e,
					"Execute the job is error.job id:%s.", writerJob.getId());

			sbMsg.append("Execute job is error.Job lifetime is:").append(writerJob.getWriterJobLifeTime())
					.append(",exception msg:").append(e.getMessage()).append(",Current task:")
					.append(writerJob.getCurrentStorage()).append(",job id:").append(writerJob.getId());

			try {
				switch (writerJob.getWriterJobLifeTime()) {
				case Opened:
				case Opening: {
					break;
				}
				case Running:
				case Runned:
				case Commiting:
				case Commited: {
					// commited then manua rollback the data by albian
					// and it can not keep the data consistency
					writerJob.setWriterJobLifeTime(WriterJobLifeTime.AutoRollbacking);
					try {
						this.exceptionHandler(writerJob);
					} catch (Exception exc) {
						isAutoRollbackSuccess = false;
						AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianSqlLoggerName, exc,
								"auto rollback  the job is error.job id:%s.", writerJob.getId());
					}
					if(writerJob.getNeedManualRollbackIfException()){
						writerJob.setWriterJobLifeTime(WriterJobLifeTime.ManualRollbacking);
						try {
							isManualRollbackSuccess = this.exceptionManualRollback(writerJob);
						} catch (Exception exc) {
							isManualRollbackSuccess = false;
							AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianSqlLoggerName, exc,
									"manual rollback the job is error.job id:%s.", writerJob.getId());
						}
					}

					writerJob.setWriterJobLifeTime(WriterJobLifeTime.Rollbacked);
					break;
				}
				default:
					break;
				}

			} catch (Exception exc) {
				AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianSqlLoggerName, e,
						"rollback the query the job is error.job id:%s.", writerJob.getId());
			}

			try {
				if(!isManualRollbackSuccess) {
					if(writerJob.getNeedManualRollbackIfException()) {
					IPersistenceCompensateNotify callback = null;
					callback = writerJob.getCompensateNotify();
					
					if (null ==callback) {
						callback = PersistenceCompensateNotify.getInstance();
					}
					callback.send(isAutoRollbackSuccess,isManualRollbackSuccess, writerJob);
					}
				}
			} catch (Exception exc) {
				AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianSqlLoggerName, exc,
						"Execute the compensate callback of job job is error.job id:%s.", writerJob.getId());
			}

		} finally {
			try {
				unLoadExecute(writerJob);
			} catch (Exception exc) {
				AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianSqlLoggerName, exc,
						"unload the job is error.job id:%s.", writerJob.getId());
			}
			if (null != writerJob.getNotifyCallback()) {
				try {

					writerJob.getNotifyCallback().notice(isSuccess, sbMsg.toString(),
							writerJob.getNotifyCallbackObject());
				} catch (Exception exc) {
					AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianSqlLoggerName, exc,
							"Execute the notice of job is error.job id:%s.", writerJob.getId());
				}
			}
			writerJob.setCurrentStorage(null);
		}

		return isSuccess;
	}

	protected abstract void preExecute(IWriterJob writerJob) throws AlbianDataServiceException;

	protected abstract void executeHandler(IWriterJob writerJob) throws AlbianDataServiceException;

	protected abstract void commit(IWriterJob writerJob) throws AlbianDataServiceException;

	protected abstract void exceptionHandler(IWriterJob writerJob) throws AlbianDataServiceException;

	protected abstract void unLoadExecute(IWriterJob writerJob) throws AlbianDataServiceException;

	protected abstract boolean exceptionManualRollback(IWriterJob writerJob) throws AlbianDataServiceException;
}
