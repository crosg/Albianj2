package org.albianj.persistence.object;

public class OrderByCondition implements IOrderByCondition {
	private String fieldName = null;
	private String aliasName = null;
	private SortStyle sortStyle = SortStyle.Asc;
	
	public OrderByCondition() {
		
	}
	
	public OrderByCondition(String fieldName) {
		this.fieldName = fieldName;
	}

	public OrderByCondition(String fieldName,SortStyle sortStyle) {
		this.fieldName = fieldName;
		this.sortStyle = sortStyle;
	}
 

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public SortStyle getSortStyle() {
		return sortStyle;
	}

	public void setSortStyle(SortStyle sortStyle) {
		this.sortStyle = sortStyle;
	}
	
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName){
		this.aliasName = aliasName;
	}
	
	
}
