package org.albianj.persistence.context;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.object.IRunningStorageAttribute;

/**
 * 存储层读取job
 * @author seapeak
 *
 */
public interface IReaderJob extends IPersistenceJob {
	/**
	 * 得到job操作的storage属性
	 * @return
	 */
	public IRunningStorageAttribute getStorage();

	/**
	 * 设置job操作的storage属性
	 * @param storage
	 */
	public void setStorage(IRunningStorageAttribute storage);

	/**
	 * 得到job的操作命令
	 * @return
	 */
	public IPersistenceCommand getCommand();

	/**
	 * 设置job操作的命令
	 * @param command
	 */
	public void setCommand(IPersistenceCommand command);

	/**
	 * 得到执行job的连接
	 * @return
	 */
	public Connection getConnection();

	/**
	 * 设置执行job的链接
	 * @param connection 执行job的链接
	 */
	public void setConnection(Connection connection);

	/**
	 * @return 得到执行job的sql语句
	 */
	public Statement getStatement();

	/**
	 * @param statement 设置执行job的sql语句
	 */
	public void setStatement(Statement statement);

	/**
	 * @return 得到执行job返回的结果集
	 */
	public ResultSet getResult();

	/**
	 * @param result 设置执行job返回的结果集
	 */
	public void setResult(ResultSet result);
}
