package org.albianj.persistence.object;

public interface IOrderByCondition extends ICondition {
	public String getFieldName();

	public void setFieldName(String fieldName);

	public SortStyle getSortStyle();

	public void setSortStyle(SortStyle sortStyle);
}
