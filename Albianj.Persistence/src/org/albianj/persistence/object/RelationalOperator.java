package org.albianj.persistence.object;


/**
 * sql查询表达式逻辑操作
 * @author seapeak
 *
 */
public enum RelationalOperator {
	
	/**
	 * and，等同于sql语句的and 
	 */
	And, 
	/**
	 * or，等同于sql语句的or
	 */
	OR,
	
	/*
	 * 没有任何的逻辑操作，仅仅表示把表达式进入表达式集合
	 */
	Normal,
}
