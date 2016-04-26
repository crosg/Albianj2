package org.albianj.service;

public interface IAlbianServiceAttribute {
	public String getId();

	public void setId(String id) throws IllegalArgumentException;

	public String getType();

	public void setType(String type) throws IllegalArgumentException;
}
