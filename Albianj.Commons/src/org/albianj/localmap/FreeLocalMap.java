package org.albianj.localmap;

import java.util.Map;
import java.util.Set;

import org.albianj.verify.Validate;

public abstract class FreeLocalMap implements ILocalMap {

	private Map<String, Object> map = null;

	public FreeLocalMap(Map<String, Object> map)
			throws IllegalArgumentException {
		if (null == map)
			throw new IllegalArgumentException("key");
		this.map = map;
	}

	public boolean exist(String key) throws IllegalArgumentException {
		if (Validate.isNullOrEmpty(key))
			throw new IllegalArgumentException("key");
		return map.containsKey(key);
	}

	public Object get(String key) throws IllegalArgumentException {
		if (Validate.isNullOrEmpty(key))
			throw new IllegalArgumentException("key");
		return map.get(key);
	}

	public void insert(String key, Object value)
			throws IllegalArgumentException {
		if (Validate.isNullOrEmpty(key))
			throw new IllegalArgumentException("key");
		if (null == value)
			throw new IllegalArgumentException("value");
		map.put(key, value);
		return;
	}

	public void remove(String key) throws IllegalArgumentException {
		if (Validate.isNullOrEmpty(key))
			throw new IllegalArgumentException("key");
		map.remove(key);
		return;

	}

	public void clear() {
		map.clear();
	}

	public Set<String> getKeys() {
		return map.keySet();
	}
}
