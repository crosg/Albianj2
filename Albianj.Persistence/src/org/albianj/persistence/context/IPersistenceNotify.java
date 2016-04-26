package org.albianj.persistence.context;

/**
 * 存储层完成后执行的通知
 * 和IPersistenceCompensateNotify的差别就是，这个通知不会受事务是否成功的影响，总会被执行
 * @author seapeak
 *
 */
public interface IPersistenceNotify {
	/**
	 * 存储层完成后执行的通知
	 * @param isSuccess 存储事务是否成功
	 * @param msg 通知的信息
	 * @param obj 通知需要使用的参数对象
	 */
	public void notice(boolean isSuccess,String msg,Object obj);
}
