package org.albianj.persistence.object;

/**
 * sql语句查询的条件表达式逻辑操作
 * @author seapeak
 *
 */
public enum LogicalOperation {
	/**
	 * 等于，相当于sql的=
	 */
	Equal, 
	/**
	 * 不等于，相当于sql的 !=
	 */
	NotEqual,
	/**
	 * 大于，相当于sql的 >
	 */
	Greater, 
	/**
	 * 小于，相当于sql语句的 <
	 */
	Less, 
	/**
	 * 大于等于，相当于sql语句的 >=
	 */
	GreaterOrEqual, 
	/**
	 * 小于等于，相当于sql语句的 <=
	 */
	LessOrEqual, 
	/**
	 * 是判断，同sql语句的 IS
	 */
	Is,
}
