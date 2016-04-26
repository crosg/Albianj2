package org.albianj.persistence.object;

import java.util.Map;


public interface IAlbianObjectAttribute {
	// <AlbianObject Type="AppTest.Model.Imp.Order">
	// <Cache Enable="true" LifeTime="300"></Cache>
	// <Members>
	// <Member Name="Id" FieldName="OrderId" AllowNull="false" Length="32"
	// PrimaryKey="true" DbType="string" IsSave="true"/>
	// </Members>
	// </AlbianObject>
	public String getInterface();

	public void setInterface(String inter);

	public String getType();

	public void setType(String type);

	public ICacheAttribute getCache();

	public void setCache(ICacheAttribute cache);

	public IDataRouterAttribute getDefaultRouting();

	public void setDefaultRouting(IDataRouterAttribute defaultRouting);

	public Map<String, IMemberAttribute> getMembers();

	public void setMembers(Map<String, IMemberAttribute> members);
}
