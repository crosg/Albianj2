package org.albianj.persistence.context;

import java.util.Map;

import org.albianj.persistence.context.WriterJobLifeTime;

/**
 * 存储层写操作job
 * @author seapeak
 *
 */
/**
 * @author seapeak
 *
 */
public interface IWriterJob  extends IPersistenceJob{
	
	/**
	 *	得到 写操作所有的任务
	 * @return
	 */
	public Map<String, IWriterTask> getWriterTasks();

	/**
	 * 设置写操作所有的任务
	 * @param writerTasks 写操作事务所有的任务
	 */
	public void setWriterTasks(Map<String, IWriterTask> writerTasks);

	/**
	 * 得到写操作的生命周期
	 * @return
	 */
	public WriterJobLifeTime getWriterJobLifeTime();

	/** 设置写操作事务的生命周期
	 * @param writerJobLifeTime 写事务的生命周期
	 */
	public void setWriterJobLifeTime(WriterJobLifeTime writerJobLifeTime);
	
	/**
	 * 得到写操作执行过程中当前的storage名字
	 * @return
	 */
	public String getCurrentStorage();

	/**
	 * 设置写操作事务执行过程中当前的storage名字
	 * @param currentStorage 当前storage的名字
	 */
	public void setCurrentStorage(String currentStorage);

	/**
	 * 得到写事务通知回调函数
	 * @return
	 */
	public IPersistenceNotify getNotifyCallback();

	/** 设置写事务通知回调方法
	 * @param notifyCallback
	 */
	public void setNotifyCallback(IPersistenceNotify notifyCallback);
	
	/**
	 * 设置写事务通知回调方法的参数对象
	 * @param notifyCallbackObject
	 */
	public void setNotifyCallbackObject(Object notifyCallbackObject);
	
	/**
	 * 得到写事务通知回调方法的参数对象
	 * @return
	 */
	public Object getNotifyCallbackObject();

	/**
	 * 得到写事务完整性通知的回调通知方法
	 * @return
	 */
	public IPersistenceCompensateNotify getCompensateNotify();

	/**
	 * 设置写事务完整性通知的回调通知方法
	 * @param compensateCallback
	 */
	public void setCompensateNotify(IPersistenceCompensateNotify compensateCallback);

	/**
	 * 设置写事务完整性通知的回调通知方法的参数对象
	 * @param compensateCallbackObject
	 */
	public void setCompensateCallbackObject(Object compensateCallbackObject);

	/**
	 * 得到写事务完整性通知的回调通知方法的参数对象
	 * @return
	 */
	public Object getCompensateCallbackObject();
	
	/**
	 * 是否需要albianj来执行“二次提交”补偿回滚事务
	 * @return
	 */
	public boolean getNeedManualRollbackIfException();

	/**
	 * 设置是否需要albianj来执行“二次提交”补偿回滚事务
	 * @param needManualRollback
	 */
	public void setNeedManualRollbackIfException(boolean needManualRollback);
}
