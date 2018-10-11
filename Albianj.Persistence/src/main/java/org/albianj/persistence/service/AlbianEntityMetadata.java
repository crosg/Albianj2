package org.albianj.persistence.service;

import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IMemberAttribute;
import org.albianj.verify.Validate;

import javax.smartcardio.ATR;
import java.util.HashMap;
import java.util.Map;

public final  class AlbianEntityMetadata {
    private static Map<String,Object> entityMetadata = new HashMap<>();
    private static Map<String,String> type2itf = new HashMap<>();

    public static IAlbianObjectAttribute getEntityMetadata(String  itf){
        return (IAlbianObjectAttribute) entityMetadata.get(itf);
    }

    public static IAlbianObjectAttribute getEntityMetadata(Class<?> itfClzz){
        return (IAlbianObjectAttribute) entityMetadata.get(itfClzz.getName());
    }

    public static boolean exist(String itf){
        return entityMetadata.containsKey(itf);
    }

    public static boolean exist(Class<?> itfClzz){
        return entityMetadata.containsKey(itfClzz.getName());
    }

    public static void put(String itf,IAlbianObjectAttribute attr){
        type2itf.put(attr.getType(), itf);
        entityMetadata.put(itf,attr);
    }

    public static void put(Class<?> itf,IAlbianObjectAttribute attr){
       put(itf.getName(),attr);
    }

    public static void putAll(Map<String,Object> map){
        //can not use putAll
        for(Object entry : map.values()){
            IAlbianObjectAttribute objAttr = (IAlbianObjectAttribute) entry;
            put(objAttr.getInterface(),objAttr);
        }
    }

    public static IAlbianObjectAttribute getEntityMetadataByType(String  type){
        return (IAlbianObjectAttribute) entityMetadata.get(type2Interface(type));
    }

    public static IAlbianObjectAttribute getEntityMetadataByType(Class<?> implClzz){
        return getEntityMetadataByType(implClzz.getName());
    }


    public static String makeFieldsKey(String propertyName){
        return propertyName.toLowerCase();
    }

    public static String type2Interface(String type){
        return type2itf.get(type);
    }
}
