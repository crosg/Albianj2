package org.albianj.persistence.object.filter;

import org.albianj.persistence.object.LogicalOperation;

/**
 * 	链式表达式过滤项的接口定义，该类是IChainExpression的子接口
 * @author seapeak
 * @since v2.1
 */
public interface IFilterExpression extends IChainExpression {
	
	/**
	 * 过滤表达式的字段名称，是指实体的属性名而不是sql语句的数据库字段名
	 * @return
	 */
	public String getFieldName();
	
	/**
	 * 过滤表达式的字段名称，是指实体的属性名而不是sql语句的数据库字段名
	 * @param fieldName
	 */
	public void setFieldName(String fieldName);

	/**
	 * 当前过滤字段的类型信息
	 * @return
	 */
	public Class<?> getFieldClass();
	
	/**
	 *  当前过滤字段的类型信息
	 * @param cls
	 */
	public void setFieldClass(Class<?> cls);

	/**
	 * 当前过滤的逻辑操作
	 * @return
	 */
	public LogicalOperation getLogicalOperation();
	
	/**
	 * 当前过滤的逻辑操作
	 * @param logicalOperation
	 */
	public void setLogicalOperation(LogicalOperation logicalOperation);

	/**
	 * 当前过滤表达式的值
	 * @return
	 */
	public Object getValue();
	
	/**
	 *  当前过滤表达式的值
	 * @param value
	 */
	public void setValue(Object value);
	
	/**
	 * 是否是附加的条件
	 * true表示该表达式项是附加条件，这个条件只可能会提供给数据路由方法，而不会影响sql语句的where条件
	 * @return
	 */
	public boolean isAddition();
	/**
	 *  是否是附加的条件
	 * true表示该表达式项是附加条件，这个条件只可能会提供给数据路由方法，而不会影响sql语句的where条件
	 * @param isAddition
	 */
	public void setAddition(boolean isAddition);
	
	/**
	 * 当前表达式字段的别名
	 * 当一个属性有多个过滤条件的时候，需要使用别名，别名可以区分不同的条件
	 * 别名可以任意取名，只要不合过滤的字段重复即可
	 * 建议还是使用有意义的名字
	 * @return
	 */
	public String getAliasName();
	/**
	 * 当前表达式字段的别名
	 * 当一个属性有多个过滤条件的时候，需要使用别名，别名可以区分不同的条件
	 * 别名可以任意取名，只要不合过滤的字段重复即可
	 * 建议还是使用有意义的名字
	 * @param an
	 */
	public void setAliasName(String an);
	
}
