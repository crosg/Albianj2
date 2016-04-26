package org.albianj.persistence.db;

import java.util.Map;


/**
 * 存储层的命令
 * @author seapeak
 *
 */
public interface IPersistenceCommand {
	/**
	 * 得到命令的文本
	 * @return
	 */
	public String getCommandText();
	/**
	 * 设置命令的文本
	 * @param commandText
	 */
	public void setCommandText(String commandText);
	/**
	 * 命令的类型
	 * @return
	 */
	public PersistenceCommandType getCommandType();
	/**
	 * 命令的类型
	 * @param commandType
	 */
	public void setCommandType(PersistenceCommandType commandType);
	/**
	 * 命令的执行参数
	 * @return
	 */
	public Map<Integer, String> getParameterMapper();
	/**
	 * 命令的执行参数
	 * @param parameterMapper
	 */
	public void setParameterMapper(Map<Integer, String> parameterMapper);
	/**
	 * 命令的sql参数，由命令参数转换而来
	 * @return
	 */
	public Map<String, ISqlParameter> getParameters();
	/**	 
	 * 命令的sql参数，由命令参数转换而来
	 * @param parameters
	 */
	public void setParameters(Map<String, ISqlParameter> parameters);
	
	/**
	 * 得到“补偿事务”回滚的命令文本
	 * @return
	 */
	public String getRollbackCommandText();
	/**
	 * “补偿事务”回滚的命令文本
	 * @param commandText
	 */
	public void setRollbackCommandText(String commandText);
	/**
	 * 回滚命令的类型
	 * @return
	 */
	public PersistenceCommandType getRollbackCommandType();
	/**
	 * 回滚命令的类型
	 * @param commandType
	 */
	public void setRollbackCommandType(PersistenceCommandType commandType);
	/**
	 * 回滚命令的执行参数
	 * @return
	 */
	public Map<Integer, String> getRollbackParameterMapper();
	/**
	 * 回滚命令的执行参数
	 * @param parameterMapper
	 */
	public void setRollbackParameterMapper(Map<Integer, String> parameterMapper);
	/**
	 * 回滚命令的执行sql参数
	 * @return
	 */
	public Map<String, ISqlParameter> getRollbackParameters();
	/**
	 * 回滚命令的执行sql参数
	 * @param parameters
	 */
	public void setRollbackParameters(Map<String, ISqlParameter> parameters);
}
