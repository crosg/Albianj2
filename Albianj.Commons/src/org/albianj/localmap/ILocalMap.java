package org.albianj.localmap;

import java.util.Set;

public interface ILocalMap {
	public boolean exist(String key) throws IllegalArgumentException;

	public Object get(String key) throws IllegalArgumentException;

	public void insert(String key, Object value)
			throws IllegalArgumentException;

	public void remove(String key) throws IllegalArgumentException;

	public void clear();

	public Set<String> getKeys();

	// public boolean hasNext();
	// public Object getNext();
}
