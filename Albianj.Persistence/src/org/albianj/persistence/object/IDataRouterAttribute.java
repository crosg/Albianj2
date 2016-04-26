package org.albianj.persistence.object;

public interface IDataRouterAttribute {
	// <Routing Name="IdRouting" StorageName="2thStorage"
	// TableName="BizOfferById" Owner="dbo" Permission="WR"></Routing>
	public boolean getEnable();

	public void setEnable(boolean enable);

	public String getName();

	public void setName(String name);

	public String getStorageName();

	public void setStorageName(String storageName);

	public String getTableName();

	public void setTableName(String tableName);

	public String getOwner();

	public void setOwner(String owner);
}
