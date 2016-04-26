package org.albianj.persistence.context;

/**
 * 写job的生命周期
 * @author seapeak
 *
 */
public enum WriterJobLifeTime {
	/**
	 * 事务的初始状态
	 */
	Normal, 
	/**
	 * 已经初始化，但是没有开始
	 */
	NoStarted, 
	/**
	 * 正在打开事务
	 */
	Opening, 
	/**
	 * 事务已经被打开
	 */
	Opened, 
	/**
	 * 事务正在执行
	 */
	Running, 
	/**
	 * 事务执行结束，但是还没有提交
	 */
	Runned, 
	/**
	 * 开始提交，在提交过程中
	 */
	Commiting, 
	/**
	 * 提交成功
	 */
	Commited, 
	/**
	 * 正在自动回滚
	 */
	AutoRollbacking, 
	/**
	 * 正在手动回滚
	 */
	ManualRollbacking, 
	/**
	 * 回滚结束
	 */
	Rollbacked,
}
