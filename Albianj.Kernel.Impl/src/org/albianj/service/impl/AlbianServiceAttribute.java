package org.albianj.service.impl;

import org.albianj.service.IAlbianServiceAttribute;
import org.albianj.verify.Validate;

public class AlbianServiceAttribute implements IAlbianServiceAttribute {

	private String id = "";
	private String type = "";

	public String getId() {
		return this.id;
	}

	public void setId(String id) throws IllegalArgumentException {
		if (Validate.isNullOrEmptyOrAllSpace(id)) {
			throw new IllegalArgumentException("id");
		}
		this.id = id;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) throws IllegalArgumentException {
		if (Validate.isNullOrEmptyOrAllSpace(type)) {
			throw new IllegalArgumentException("type");
		}
		this.type = type;

	}

}
