package org.albianj.persistence.object;

import org.albianj.persistence.object.filter.IFilterExpression;

@Deprecated
public class FilterCondition implements IFilterCondition {
	private RelationalOperator relationalOperator = RelationalOperator.And;
	private String fieldName = null;
	private String aliasName = null;
	private Class<?> fieldClass = null;
	private LogicalOperation logicalOperation = LogicalOperation.Equal;
	private Object value = null;
	private boolean beginSub = false;
	private boolean closeSub = false;
	private boolean  isAddition = false;
	
	public FilterCondition() {
		
	}

	
	public FilterCondition(IFilterExpression f){
		this.relationalOperator = f.getRelationalOperator();
		this.fieldName = f.getFieldName();
		this.aliasName = f.getAliasName();
		this.fieldClass = f.getFieldClass();
		this.logicalOperation = f.getLogicalOperation();
		this.value = f.getValue();
	}
	
	public FilterCondition(String aliasName,String fieldName,Object value) {
		this.aliasName = aliasName;
		this.fieldName = fieldName;
		this.value = value;
	}
	
	public FilterCondition(String aliasName,String fieldName,Object value,boolean isAddition) {
		this.aliasName = aliasName;
		this.fieldName = fieldName;
		this.value = value;
		this.isAddition = isAddition;
	}
	
	public FilterCondition(String fieldName,Object value) {
		this.fieldName = fieldName;
		this.value = value;
	}
	
	public FilterCondition(String fieldName,Object value,boolean isAddition) {
		this.fieldName = fieldName;
		this.value = value;
		this.isAddition = isAddition;
	}

	
	public FilterCondition(RelationalOperator relationalOperator,
			String fieldName,LogicalOperation logicalOperation,Object value) {
		this.fieldName = fieldName;
		this.value = value;
		this.relationalOperator = relationalOperator;
		this.logicalOperation = logicalOperation;
	}
	
	public FilterCondition(RelationalOperator relationalOperator,String aliasName,
			String fieldName,LogicalOperation logicalOperation,Object value) {
		this.aliasName = aliasName;
		this.fieldName = fieldName;
		this.value = value;
		this.relationalOperator = relationalOperator;
		this.logicalOperation = logicalOperation;
	}
	
	public RelationalOperator getRelationalOperator() {
		return relationalOperator;
	}

	public void setRelationalOperator(RelationalOperator relationalOperator) {
		this.relationalOperator = relationalOperator;
	}
	
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName){
		this.aliasName = aliasName;
	}
	

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Class<?> getFieldClass() {
		return fieldClass;
	}

	public void setFieldClass(Class<?> fieldClass) {
		this.fieldClass = fieldClass;
	}

	public LogicalOperation getLogicalOperation() {
		return logicalOperation;
	}

	public void setLogicalOperation(LogicalOperation logicalOperation) {
		this.logicalOperation = logicalOperation;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void beginSub() {
		this.beginSub = true;
	}

	public void closeSub() {
		this.closeSub = true;
	}

	public boolean isBeginSub() {
		return this.beginSub;
	}

	public boolean isCloseSub() {
		return this.closeSub;
	}

	public void set(RelationalOperator ro, String fieldName, Class<?> cls,
			LogicalOperation lo, Object v) {
		this.relationalOperator = ro;
		this.fieldName = fieldName;
		this.fieldClass = cls;
		this.logicalOperation = lo;
		this.value = v;
	}

	public void set(String fieldName, Class<?> cls, LogicalOperation lo,
			Object v) {
		set(RelationalOperator.And, fieldName, cls, lo, v);
	}

	@Override
	public boolean isAddition() {
		// TODO Auto-generated method stub
		return this.isAddition;
	}

	@Override
	public void setAddition(boolean isAddition) {
		// TODO Auto-generated method stub
		this.isAddition = isAddition;
	}
}
