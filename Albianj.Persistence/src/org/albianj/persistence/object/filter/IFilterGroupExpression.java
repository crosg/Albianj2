package org.albianj.persistence.object.filter;


/**
 * 	链式表达式组的接口定义，该类是IChainExpression的子接口
 * 
 * @author seapeak
 * @since v2.1
 *
 */
public interface IFilterGroupExpression extends IChainExpression {
	
	/**
	 *  单纯的加上当前的表达式组项。该表达式组项会被加入到sql语句的where条件中
	 *  一般此函数使用在有两个表达式组的情况下，类似于sql语句中的连续两个(())构成的语句的时候
	 * @param fge 当前表达式项
	 * @return 表达式项关系链的头对象
	 */
	IFilterGroupExpression addFilterGroup(IFilterGroupExpression fge);
	
}
