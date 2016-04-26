package org.albianj.persistence.context;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.object.IRunningStorageAttribute;

/**
 * 写事务的任务
 * 相对于job来说，task是job的细分
 * 对于albianj来说，每个task对应一个storage，task是albianj写操作的最小单位
 * @author seapeak
 *
 */
public interface IWriterTask {
	/**
	 * 得到task所对应操作的storage
	 * @return
	 */
	public IRunningStorageAttribute getStorage();

	/**
	 * 设置task所对应操作的storage
	 * @param storage
	 */
	public void setStorage(IRunningStorageAttribute storage);

	/**
	 * 得到task所对应的命令
	 * @return
	 */
	public List<IPersistenceCommand> getCommands();

	/**
	 * 设置task所对应的命令
	 * @param commands
	 */
	public void setCommands(List<IPersistenceCommand> commands);

	/**
	 * 得到task对应storage的链接
	 * @return
	 */
	public Connection getConnection();

	/**
	 * 设置task对应的storage的链接
	 * @param connection
	 */
	public void setConnection(Connection connection);

	/**
	 * 得到task对应的sql命令
	 * @return
	 */
	public List<Statement> getStatements();

	/**
	 * 设置task对应的sql命令
	 * @param statements
	 */
	public void setStatements(List<Statement> statements);
	
	/**
	 * task是否已经提交
	 * @return
	 */
	public boolean getIsCommited();
	
	/**
	 * 设置task是否已经提交
	 * @param iscommited
	 */
	public void setIsCommited(boolean iscommited);
	
	/**
	 * 获取albianj“补偿事务”的sql语句
	 * @return
	 */
	public List<Statement> getRollbackStatements();

	/**
	 * 设置albanj的“补偿事务”的sql语句
	 * @param statements
	 */
	public void setRollbackStatements(List<Statement> statements);
}
