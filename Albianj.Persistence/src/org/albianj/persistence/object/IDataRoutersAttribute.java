package org.albianj.persistence.object;

import java.util.Map;

public interface IDataRoutersAttribute {
	public boolean getWriterRouterEnable();

	public void setWriterRouterEnable(boolean writerHash);

	public boolean getReaderRouterEnable();

	public void setReaderRouterEnable(boolean readerHash);

	public Map<String, IDataRouterAttribute> getWriterRouters();

	public void setWriterRouters(Map<String, IDataRouterAttribute> writerRoutings);

	public Map<String, IDataRouterAttribute> getReaderRouters();

	public void setReaderRouters(Map<String, IDataRouterAttribute> readerRoutings);

	public IAlbianObjectDataRouter getDataRouter();

	public void setDataRouter(IAlbianObjectDataRouter hashMapping);

	public String getInterface();

	public void setInterface(String inter);

	public String getType();

	public void setType(String type);
}
