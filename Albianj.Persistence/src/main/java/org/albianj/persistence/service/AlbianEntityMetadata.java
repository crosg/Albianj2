package org.albianj.persistence.service;

import org.albianj.loader.entry.IAlbianBundleModuleConf;
import org.albianj.persistence.object.IAlbianObjectAttribute;

import java.util.HashMap;
import java.util.Map;

public class AlbianEntityMetadata implements IAlbianBundleModuleConf {

    private  Map<String, Object> entityMetadata = new HashMap<>();
    private  Map<String, String> type2itf = new HashMap<>();

    public  IAlbianObjectAttribute getEntityMetadata(String itf) {
        return (IAlbianObjectAttribute) entityMetadata.get(itf);
    }

    public  IAlbianObjectAttribute getEntityMetadata(Class<?> itfClzz) {
        return (IAlbianObjectAttribute) entityMetadata.get(itfClzz.getName());
    }

    public  boolean exist(String itf) {
        return entityMetadata.containsKey(itf);
    }

    public  boolean exist(Class<?> itfClzz) {
        return entityMetadata.containsKey(itfClzz.getName());
    }

    public  void put(String itf, IAlbianObjectAttribute attr) {
        type2itf.put(attr.getType(), itf);
        entityMetadata.put(itf, attr);
    }

    public  void put(Class<?> itf, IAlbianObjectAttribute attr) {
        put(itf.getName(), attr);
    }

    public  void putAll(Map<String, Object> map) {
        //can not use putAll
        for (Object entry : map.values()) {
            IAlbianObjectAttribute objAttr = (IAlbianObjectAttribute) entry;
            put(objAttr.getInterface(), objAttr);
        }
    }

    public  IAlbianObjectAttribute getEntityMetadataByType(String type) {
        return (IAlbianObjectAttribute) entityMetadata.get(type2Interface(type));
    }

    public  IAlbianObjectAttribute getEntityMetadataByType(Class<?> implClzz) {
        return getEntityMetadataByType(implClzz.getName());
    }


    public  String makeFieldsKey(String propertyName) {
        return propertyName.toLowerCase();
    }

    public  String type2Interface(String type) {
        return type2itf.get(type);
    }
}
