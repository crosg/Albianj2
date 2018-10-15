package org.albianj.persistence.context.dactx;

import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.persistence.service.LoadType;

import java.util.LinkedList;
import java.util.List;

public interface IQueryContext {

    /*
      别问我为啥叫这个结构，只因为再支持方法级别的功能，方法就要爆炸了
     */
    /*
     query condition:
         itfClzz,LoadType,Where,Order,PageStart,PageSize,UseIndex,routering,storageAlias,tableAlias
         result object or list<object.
     */

    IQueryContext paging(int start,int pagesize);

    IQueryContext forceIndex(String idxName);

    IQueryContext orderby(LinkedList<IOrderByCondition> orderbys);

    IQueryContext useStorage(String storageAlias);

    IQueryContext fromTable(String tableAlias);

    IQueryContext byRouter(String drouterAlias);


    <T extends IAlbianObject> T loadObject(String sessionId,Class<T> itfClzz,LoadType loadType,IChainExpression where) throws AlbianDataServiceException;

    <T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> itfClzz,LoadType loadType,IChainExpression where) throws AlbianDataServiceException;

    void reset();
}
