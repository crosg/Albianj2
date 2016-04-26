package org.albianj.persistence.impl.object;

import java.util.Map;

import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.ICacheAttribute;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.persistence.object.IMemberAttribute;

public class AlbianObjectAttribute implements IAlbianObjectAttribute {

	private ICacheAttribute cache = null;
	private IDataRouterAttribute defaultRouting = null;
	private Map<String, IMemberAttribute> members = null;
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

	public ICacheAttribute getCache() {
		// TODO Auto-generated method stub
		return this.cache;
	}

	public void setCache(ICacheAttribute cache) {
		// TODO Auto-generated method stub
		this.cache = cache;
	}

	public IDataRouterAttribute getDefaultRouting() {
		return this.defaultRouting;
	}

	public void setDefaultRouting(IDataRouterAttribute defaultRouting) {
		this.defaultRouting = defaultRouting;
	}

	public Map<String, IMemberAttribute> getMembers() {
		// TODO Auto-generated method stub
		return this.members;
	}

	public void setMembers(Map<String, IMemberAttribute> members) {
		// TODO Auto-generated method stub
		this.members = members;
	}
}