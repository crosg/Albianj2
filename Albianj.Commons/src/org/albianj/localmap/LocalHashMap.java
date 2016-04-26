package org.albianj.localmap;

import java.util.HashMap;
import java.util.Map;

public class LocalHashMap extends FreeLocalMap implements ILocalMap {
	public LocalHashMap() {
		super(new HashMap<String, Object>());
	}

	public LocalHashMap(Map<String, Object> map) {
		super(map);
	}

}
