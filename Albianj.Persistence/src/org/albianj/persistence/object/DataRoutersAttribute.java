package org.albianj.persistence.object;

import java.util.Map;

public class DataRoutersAttribute implements IDataRoutersAttribute {

	private boolean writerHash = false;
	private boolean readerHash = false;
	private Map<String, IDataRouterAttribute> writerRoutings = null;
	private Map<String, IDataRouterAttribute> readerRoutings = null;
	private IAlbianObjectDataRouter hashMapping = null;
	private String type = null;
	private String inter = null;

	public String getInterface() {
		return inter;
	}

	public void setInterface(String inter) {
		this.inter = inter;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getWriterRouterEnable() {
		// TODO Auto-generated method stub
		return this.writerHash;
	}

	public void setWriterRouterEnable(boolean writerHash) {
		// TODO Auto-generated method stub
		this.writerHash = writerHash;
	}

	public boolean getReaderRouterEnable() {
		// TODO Auto-generated method stub
		return this.readerHash;
	}

	public void setReaderRouterEnable(boolean readerHash) {
		// TODO Auto-generated method stub
		this.readerHash = readerHash;
	}

	public Map<String, IDataRouterAttribute> getWriterRouters() {
		// TODO Auto-generated method stub
		return this.writerRoutings;
	}

	public void setWriterRouters(Map<String, IDataRouterAttribute> writerRoutings) {
		// TODO Auto-generated method stub
		this.writerRoutings = writerRoutings;
	}

	public Map<String, IDataRouterAttribute> getReaderRouters() {
		// TODO Auto-generated method stub
		return this.readerRoutings;
	}

	public void setReaderRouters(Map<String, IDataRouterAttribute> readerRoutings) {
		// TODO Auto-generated method stub
		this.readerRoutings = readerRoutings;
	}

	public IAlbianObjectDataRouter getDataRouter() {
		return this.hashMapping;
	}

	public void setDataRouter(IAlbianObjectDataRouter hashMapping) {
		this.hashMapping = hashMapping;
	}
}
