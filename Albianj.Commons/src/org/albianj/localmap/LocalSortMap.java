package org.albianj.localmap;

import java.util.LinkedHashMap;
import java.util.Map;

public class LocalSortMap extends FreeLocalMap implements ILocalMap {
	public LocalSortMap() {
		super(new LinkedHashMap<String, Object>());
	}

	public LocalSortMap(Map<String, Object> map) {
		super(map);
	}
}
