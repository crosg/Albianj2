package org.albianj.persistence.object;

@Deprecated
public interface IFilterCondition extends ICondition {
	public RelationalOperator getRelationalOperator();

	public void setRelationalOperator(RelationalOperator relationalOperator);

	public String getFieldName();

	public void setFieldName(String fieldName);

	public Class<?> getFieldClass();

	public void setFieldClass(Class<?> cls);

	public LogicalOperation getLogicalOperation();

	public void setLogicalOperation(LogicalOperation logicalOperation);

	public Object getValue();

	public void setValue(Object value);

	public void beginSub();

	public void closeSub();

	public boolean isBeginSub();

	public boolean isCloseSub();
	
	public boolean isAddition();
	public void setAddition(boolean isAddition);
	

	public void set(RelationalOperator ro, String fieldName, Class<?> cls,
			LogicalOperation lo, Object v);

	public void set(String fieldName, Class<?> cls, LogicalOperation lo,
			Object v);
}
