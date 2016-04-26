package org.albianj.persistence.context;

import org.albianj.persistence.context.IWriterJob;

/**
 * 存储操作发生异常时的通知类
 * @author seapeak
 *
 */
public interface IPersistenceCompensateNotify {
	
	/**
	 * 存储发生异常的时候，发送的通知
	 * @param isAutoRollbackSuccess 自动回滚事务是否成功
	 * @param isManualRollbackSuccess albianj回滚事务是否成功
	 * @param job 被存储层执行的job信息
	 */
	public void send(boolean isAutoRollbackSuccess,boolean isManualRollbackSuccess,IWriterJob job);
}
