package org.albianj.persistence.db;

/**
 * 存储层命令的类型
 * @author seapeak
 *
 */
public enum PersistenceCommandType {
	/**
	 *sql语句类型 
	 */
	Text, 
	/**
	 * 存储过程
	 */
	StoredProcedures
}
