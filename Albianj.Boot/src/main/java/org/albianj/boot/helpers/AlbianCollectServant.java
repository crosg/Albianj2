package org.albianj.boot.helpers;

import java.util.Collection;
import java.util.Map;

public class AlbianCollectServant {
    public static AlbianCollectServant Instance = null;

    static {
        if(null == Instance) {
            Instance = new AlbianCollectServant();
        }
    }

    protected AlbianCollectServant() {

    }

    public boolean isNullOrEmpty(Collection<?> collection) {
        return null == collection || collection.isEmpty();
    }

    public boolean isNullOrEmpty(@SuppressWarnings("rawtypes") Map map) {
        return null == map || map.isEmpty();
    }

    public boolean isNull(@SuppressWarnings("rawtypes") Map map) {
        return null == map;
    }

}
