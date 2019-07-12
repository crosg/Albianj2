package org.albianj.persistence.service;

import org.albianj.persistence.context.dactx.IDataAccessContext;
import org.albianj.persistence.context.dactx.IQueryContext;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.filter.IFilterExpression;
import org.albianj.persistence.object.filter.IFilterGroupExpression;

public interface IAlbianStoreService {

    IQueryContext newQueryContext();

    IDataAccessContext newDataAccessContext();

    IAlbianObject newAlbianObject(String sessionId, Class<? extends IAlbianObject> clazz);

    IFilterExpression newFilterExpression();

    IFilterGroupExpression newFilterGroupExpression();

    /**
     *  将src中的值按照field复制到dest
     * @param dest
     * @param src
     * @param ctrlFields 控制字段
     *                      1. !fielename，field前加!,表示此字段不被复制
     *                      2. destFieldName=secFieldName，表示src中的field和dest中的对应复制
     */
    void copyObject(IAlbianObject dest,IAlbianObject src,String[] ctrlFields);

    boolean isNeedUpdate(String sessionId,IAlbianObject entry, Class<? extends IAlbianObject> itf);
}
