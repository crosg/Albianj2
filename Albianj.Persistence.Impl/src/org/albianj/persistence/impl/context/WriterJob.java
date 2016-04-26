package org.albianj.persistence.impl.context;

import java.util.Map;

import org.albianj.persistence.context.IPersistenceCompensateNotify;
import org.albianj.persistence.context.IPersistenceNotify;
import org.albianj.persistence.context.IWriterJob;
import org.albianj.persistence.context.IWriterTask;
import org.albianj.persistence.context.WriterJobLifeTime;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

public class WriterJob implements IWriterJob {
	private Map<String, IWriterTask> writerTasks = null;
	private WriterJobLifeTime writerJobLifeTime = WriterJobLifeTime.Normal;
	private IPersistenceCompensateNotify compensateCallback = null;
	private Object compensateCallbackObject = null;
	private IPersistenceNotify notifyCallback = null;
	private String currentStorage = null;
	private Object notifyCallbackobject = null;
	private String id = null;
	private boolean needManualRollback = false;
	
	public WriterJob(String sessionId) {
		// TODO Auto-generated constructor stub
		if(Validate.isNullOrEmptyOrAllSpace(sessionId)) {
			this.id = AlbianServiceRouter.getLogIdService().makeLoggerId();
		} else {
			this.id = sessionId;
		}
	}

	@Override
	public void setNotifyCallbackObject(Object notifyCallbackObject) {
		// TODO Auto-generated method stub
		this.notifyCallbackobject = notifyCallbackObject;
	}

	@Override
	public Object getNotifyCallbackObject() {
		// TODO Auto-generated method stub
		return this.notifyCallbackobject;
	}

	public Map<String, IWriterTask> getWriterTasks() {
		// TODO Auto-generated method stub
		return this.writerTasks;
	}

	public void setWriterTasks(Map<String, IWriterTask> writerTasks) {
		// TODO Auto-generated method stub
		this.writerTasks = writerTasks;
	}

	public WriterJobLifeTime getWriterJobLifeTime() {
		// TODO Auto-generated method stub
		return this.writerJobLifeTime;
	}

	public void setWriterJobLifeTime(WriterJobLifeTime writerJobLifeTime) {
		// TODO Auto-generated method stub
		this.writerJobLifeTime = writerJobLifeTime;
	}

	public IPersistenceCompensateNotify getCompensateNotify() {
		// TODO Auto-generated method stub
		return this.compensateCallback;
	}

	public void setCompensateNotify(IPersistenceCompensateNotify compensateCallback) {
		// TODO Auto-generated method stub
		this.compensateCallback = compensateCallback;
	}

	public void setCompensateCallbackObject(Object compensateCallbackObject) {
		this.compensateCallbackObject = compensateCallbackObject;
	}

	public Object getCompensateCallbackObject() {
		return this.compensateCallbackObject;
	}

	public IPersistenceNotify getNotifyCallback() {
		return this.notifyCallback;
	}

	public void setNotifyCallback(IPersistenceNotify notifyCallback) {
		this.notifyCallback = notifyCallback;
	}

	public String getCurrentStorage() {
		return this.currentStorage;
	}

	public void setCurrentStorage(String currentStorage) {
		this.currentStorage = currentStorage;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public boolean getNeedManualRollbackIfException() {
		// TODO Auto-generated method stub
		return needManualRollback;
	}

	@Override
	public void setNeedManualRollbackIfException(boolean needManualRollback) {
		// TODO Auto-generated method stub
		this.needManualRollback = needManualRollback;
	}
}
