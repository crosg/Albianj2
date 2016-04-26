package org.albianj.persistence.context;

/**
 * 存储层的job
 * @author seapeak
 *
 */
public interface IPersistenceJob {
	
	/**
	 * 得到当前job的id，
	 * 如果执行job的时候传入sessionid，这个方法获取该sessionid
	 * 如果没有传入sessionid，那么albianj会自动生成一个
	 * @return
	 */
	String getId();
}
