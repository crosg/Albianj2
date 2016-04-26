package org.albianj.persistence.db;

/**
 * 存储层sql参数
 * @author seapeak
 *
 */
public interface ISqlParameter {
	/**
	 * 参数的sql类型
	 * @return
	 */
	public int getSqlType();

	/**
	 * 参数的sql类型
	 * @param sqlType
	 */
	public void setSqlType(int sqlType);

	/**
	 * 参数的名称
	 * @return
	 */
	public String getName();

	/**
	 * 参数的名称
	 * @param name
	 */
	public void setName(String name);

	/**
	 * 参数的值
	 * @return
	 */
	public Object getValue();

	/**
	 * 参数的值
	 * @param value
	 */
	public void setValue(Object value);

	/**
	 * 参数的sql字段名
	 * @param sqlFieldName
	 */
	public void setSqlFieldName(String sqlFieldName);

	/**
	 * 参数的sql字段名
	 * @return
	 */
	public String getSqlFieldName();
}
