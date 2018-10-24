package org.albianj.persistence.impl.service;

import org.albianj.persistence.context.*;
import org.albianj.persistence.context.dactx.IDataAccessContext;
import org.albianj.persistence.context.dactx.IQueryContext;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.db.PersistenceCommandType;
import org.albianj.persistence.impl.context.*;
import org.albianj.persistence.impl.context.dactx.DataAccessContext;
import org.albianj.persistence.impl.context.dactx.QueryContext;
import org.albianj.persistence.impl.db.IPersistenceQueryScope;
import org.albianj.persistence.impl.db.IPersistenceTransactionClusterScope;
import org.albianj.persistence.impl.db.PersistenceQueryScope;
import org.albianj.persistence.impl.db.PersistenceTransactionClusterScope;
import org.albianj.persistence.object.*;
import org.albianj.persistence.object.filter.FilterExpression;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.persistence.service.IAlbianDataAccessService;
import org.albianj.persistence.service.LoadType;
import org.albianj.service.FreeAlbianService;
import org.albianj.verify.Validate;

import java.math.BigInteger;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuhaifeng on 16/12/22.
 */
public class AlbianDataAccessService extends FreeAlbianService implements IAlbianDataAccessService {

    public String getServiceName(){
        return Name;
    }


    public boolean remove(String sessionId, IAlbianObject object) throws AlbianDataServiceException {
        return this.remove(sessionId, object, null, null, null, null);
    }

    public boolean remove(String sessionId, IAlbianObject object, IPersistenceNotify notifyCallback,
                          Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
                          Object compensateCallbackObject) throws AlbianDataServiceException {
        IWriterJobAdapter ja = new WriterJobAdapter();
        IWriterJob job = ja.buildRemoved(sessionId, object);
        if (null != notifyCallback)
            job.setNotifyCallback(notifyCallback);
        if (null != notifyCallbackObject)
            job.setNotifyCallbackObject(notifyCallbackObject);
        if (null != compensateCallback)
            job.setCompensateNotify(compensateCallback);
        if (null != compensateCallbackObject)
            job.setCompensateCallbackObject(compensateCallbackObject);
        IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
        return tcs.execute(job);
    }

    public boolean remove(String sessionId, List<? extends IAlbianObject> objects) throws AlbianDataServiceException {
        return this.remove(sessionId, objects, null, null, null, null);
    }

    public boolean remove(String sessionId, List<? extends IAlbianObject> objects, IPersistenceNotify notifyCallback,
                          Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
                          Object compensateCallbackObject) throws AlbianDataServiceException {
        IWriterJobAdapter ja = new WriterJobAdapter();
        IWriterJob job = ja.buildRemoved(sessionId, objects);
        if (null != notifyCallback)
            job.setNotifyCallback(notifyCallback);
        if (null != notifyCallbackObject)
            job.setNotifyCallbackObject(notifyCallbackObject);
        if (null != compensateCallback)
            job.setCompensateNotify(compensateCallback);
        if (null != compensateCallbackObject)
            job.setCompensateCallbackObject(compensateCallbackObject);
        IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
        return tcs.execute(job);
    }

    public boolean save(String sessionId, IAlbianObject object) throws AlbianDataServiceException {
        return this.save(sessionId, object, null, null, null, null);
    }

    public boolean save(String sessionId, IAlbianObject object,
                        IPersistenceNotify notifyCallback, Object notifyCallbackObject,
                        IPersistenceCompensateNotify compensateCallback, Object compensateCallbackObject)
            throws AlbianDataServiceException {
        IWriterJobAdapter ja = new WriterJobAdapter();
        IWriterJob job = ja.buildSaving(sessionId, object);
        if (null != notifyCallback)
            job.setNotifyCallback(notifyCallback);
        if (null != notifyCallbackObject)
            job.setNotifyCallbackObject(notifyCallbackObject);
        if (null != compensateCallback)
            job.setCompensateNotify(compensateCallback);
        if (null != compensateCallbackObject)
            job.setCompensateCallbackObject(compensateCallbackObject);
        IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
        return tcs.execute(job);
    }

    public boolean save(String sessionId, List<? extends IAlbianObject> objects) throws AlbianDataServiceException {
        return this.save(sessionId, objects, null, null, null, null);
    }

    public boolean save(String sessionId, List<? extends IAlbianObject> objects, IPersistenceNotify notifyCallback,
                        Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
                        Object compensateCallbackObject) throws AlbianDataServiceException {
        IWriterJobAdapter ja = new WriterJobAdapter();
        IWriterJob job = ja.buildSaving(sessionId, objects);
        if (null != notifyCallback)
            job.setNotifyCallback(notifyCallback);
        if (null != notifyCallbackObject)
            job.setNotifyCallbackObject(notifyCallbackObject);
        if (null != compensateCallback)
            job.setCompensateNotify(compensateCallback);
        if (null != compensateCallbackObject)
            job.setCompensateCallbackObject(compensateCallbackObject);
        IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
        return tcs.execute(job);
    }

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls, LoadType loadType, IChainExpression wheres)
            throws AlbianDataServiceException {
        return this.loadObject(sessionId, cls, loadType, null, wheres);
    }

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls,
                                                  LoadType loadType, String rountingName, IChainExpression wheres)
            throws AlbianDataServiceException {

        List<T> list = doLoadObjects(sessionId, cls, LoadType.exact == loadType, rountingName, 0, 0, wheres, null,null);
        if (Validate.isNullOrEmpty(list))
            return null;
        return list.get(0);

//        if (LoadType.exact == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, 0, 0, wheres, null,null);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
//            return list.get(0);
//        }
//
//        if (LoadType.dirty == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, 0, 0, wheres, null,null);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
//            return list.get(0);
//        }
//
////        T obj = AlbianPersistenceCache.findObject(cls, wheres, null);
////        if (null != obj)
////            return obj;
//
//        T newObj = doLoadObject(sessionId, cls, false, rountingName, wheres);
//        if (null == newObj)
//            return null;
////        AlbianPersistenceCache.setObject(cls, wheres, null, newObj);
//        return newObj;
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, IChainExpression wheres)
            throws AlbianDataServiceException {
        return loadObjects(sessionId, cls, loadType, null, wheres, null);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         IChainExpression wheres, LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException {
        return loadObjects(sessionId, cls, loadType, null, wheres, orderbys);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, String rountingName,
                                                         IChainExpression wheres, LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException {
        return loadObjects(sessionId, cls, loadType, 0, 0, wheres, orderbys);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         int start, int step, IChainExpression wheres)
            throws AlbianDataServiceException {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, null);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         int start, int step, IChainExpression wheres, LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, orderbys);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, String rountingName,
                                                         int start, int step, IChainExpression wheres, LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException {
        List<T> list = doLoadObjects(sessionId, cls, LoadType.exact == loadType, rountingName, start, step, wheres, orderbys,null);
        if (Validate.isNullOrEmpty(list))
            return null;
        return list;

//        if (LoadType.exact == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, start, step, wheres, orderbys,null);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
//            return list;
//        }
//        if (LoadType.dirty == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,null);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
//            return list;
//        }

//        List<T> objs = AlbianPersistenceCache.findObjects(cls, start, step, wheres, orderbys);
//        if (null != objs)
//            return objs;

//        List<T> objs = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,null);
//        if (null == objs)
//            return null;
////        AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, objs);
//        return objs;
    }


    /**
     * 慎用
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     * @throws AlbianDataServiceException
     */
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, LoadType loadType)
            throws AlbianDataServiceException{
        return this.loadObjects(sessionId,cls,loadType,new FilterExpression());
    }

    /**
     * 慎用
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     * @throws AlbianDataServiceException
     */
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, LoadType loadType,LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException{
        return this.loadObjects(sessionId,cls,loadType,new FilterExpression(),orderbys);
    }

    /**
     * 慎用
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     * @throws AlbianDataServiceException
     */
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, LoadType loadType,String rountingName,LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException{
        return this.loadObjects(sessionId,cls,loadType,rountingName, new FilterExpression(),orderbys);

    }

    public <T extends IAlbianObject> T loadObjectById(String sessionId, Class<T> cls, LoadType loadType, BigInteger id)
            throws AlbianDataServiceException{
        IChainExpression ce = new FilterExpression("id", LogicalOperation.Equal,id);
        return loadObject(sessionId,cls, loadType,ce);
    }

    public <T extends IAlbianObject> T loadObjectById(String sessionId, Class<T> cls, LoadType loadType, String rountingName,  BigInteger id)
            throws AlbianDataServiceException{
        IChainExpression ce = new FilterExpression("id", LogicalOperation.Equal,id);
        return loadObject(sessionId,cls, loadType,rountingName,ce);
    }

    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           LoadType loadType, IChainExpression wheres)
            throws AlbianDataServiceException {
        return this.loadObjectsCount(sessionId, cls, loadType, null, wheres);
    }

    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           LoadType loadType, String rountingName, IChainExpression wheres)
            throws AlbianDataServiceException {

        return  doLoadPageingCount(sessionId, cls, LoadType.exact == loadType, rountingName, wheres, null,null);
//        long count = 0;
//        if (LoadType.exact == loadType) {
//            count = doLoadPageingCount(sessionId, cls, true, rountingName, wheres, null,null);
////            AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
//        } else {
//            if (LoadType.dirty == loadType) {
//                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,null);
////                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
//            } else {
////                count = AlbianPersistenceCache.findPagesize(cls, wheres, null);
////                if (0 <= count) {
////                    return count;
////                }
//                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,null);
////                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
//            }
//
//        }
//
//        return count;
    }

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls, PersistenceCommandType cmdType,
                                                  Statement statement) throws AlbianDataServiceException {
        List<T> list = doLoadObjects(sessionId,cls, cmdType, statement);
        if (Validate.isNullOrEmpty(list))
            return null;
        return list.get(0);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, PersistenceCommandType cmdType,
                                                         Statement statement) throws AlbianDataServiceException {
        List<T> list = doLoadObjects(sessionId,cls, cmdType, statement);
        if (Validate.isNullOrEmpty(list))
            return null;
        return list;
    }

    protected <T extends IAlbianObject> List<T> doLoadObjects(String sessionId,
            Class<T> cls, PersistenceCommandType cmdType, Statement statement)
            throws AlbianDataServiceException {
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        List<T> list = null;
        list = scope.execute(sessionId,cls, cmdType, statement);
        return list;
    }

    protected <T extends IAlbianObject> T doLoadObject(String sessionId,Class<T> cls,
                                                       PersistenceCommandType cmdType, Statement statement)
            throws AlbianDataServiceException {
        List<T> list = doLoadObjects(sessionId,cls, cmdType, statement);
        if (Validate.isNullOrEmpty(list))
            return null;
        return list.get(0);
    }

    protected <T extends IAlbianObject> T doLoadObject(String sessionId,
                                                       Class<T> cls, boolean isExact,
                                                       String routingName, IChainExpression wheres)
            throws AlbianDataServiceException {
        List<T> list = doLoadObjects(sessionId, cls, isExact, routingName, 0, 0, wheres, null,null);
        if (Validate.isNullOrEmpty(list))
            return null;
        return list.get(0);
    }

    protected <T extends IAlbianObject> List<T> doLoadObjects(String sessionId,
                                                              Class<T> cls, boolean isExact, String routingName, int start, int step,
                                                              IChainExpression wheres,
                                                              LinkedList<IOrderByCondition> orderbys,String idxName)
            throws AlbianDataServiceException {
        IReaderJobAdapter ad = new ReaderJobAdapter();
        List<T> list = null;
        IReaderJob job = ad.buildReaderJob(sessionId, cls, isExact,null,null, routingName, start, step,
                wheres, orderbys,idxName);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        list = scope.execute(cls, job);
        return list;
    }

    protected <T extends IAlbianObject> long doLoadPageingCount(String sessionId,
                                                                Class<T> cls, boolean isExact, String routingName,
                                                                IChainExpression wheres,
                                                                LinkedList<IOrderByCondition> orderbys,String idxName)
            throws AlbianDataServiceException {
        IReaderJobAdapter ad = new ReaderJobAdapter();
        IReaderJob job = ad.buildReaderJob(sessionId, cls, isExact,null,null, routingName,
                wheres, orderbys,idxName);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        Object o = scope.execute(job);
        return (long) o;
    }


  public   <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, IRunningStorageAttribute storage, PersistenceCommandType cmdType,
                                                         String text, Map<String,ISqlParameter> paras) throws AlbianDataServiceException{
      IReaderJobAdapter ad = new ReaderJobAdapter();
      IReaderJob job = ad.buildReaderJob(sessionId, cls, storage, cmdType,
              text, paras);
      IPersistenceQueryScope scope = new PersistenceQueryScope();
      List<T> list = scope.execute(cls,job);
      return list;
  }

   public  <T extends IAlbianObject> List<T> loadObject(String sessionId, Class<T> cls, IRunningStorageAttribute storage,PersistenceCommandType cmdType,
                                                 String text, Map<String,ISqlParameter> paras) throws AlbianDataServiceException{
        return loadObjects(sessionId,cls,storage,cmdType,text,paras);
   }


   //-------增加强制制定索引名字

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls, LoadType loadType, IChainExpression wheres,String idxName)
            throws AlbianDataServiceException {
        return this.loadObject(sessionId, cls, loadType, null, wheres,idxName);
    }

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls,
                                                  LoadType loadType, String rountingName, IChainExpression wheres,String idxName)
            throws AlbianDataServiceException {
        List<T> list = doLoadObjects(sessionId, cls, LoadType.exact == loadType, rountingName, 0, 0, wheres, null,idxName);
        if (Validate.isNullOrEmpty(list))
            return null;
        return list.get(0);

//        if (LoadType.exact == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, 0, 0, wheres, null,idxName);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
//            return list.get(0);
//        }
//
//        if (LoadType.dirty == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, 0, 0, wheres, null,idxName);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
//            return list.get(0);
//        }
//
////        T obj = AlbianPersistenceCache.findObject(cls, wheres, null);
////        if (null != obj)
////            return obj;
//
//        T newObj = doLoadObject(sessionId, cls, false, rountingName, wheres);
//        if (null == newObj)
//            return null;
////        AlbianPersistenceCache.setObject(cls, wheres, null, newObj);
//        return newObj;
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, IChainExpression wheres,String idxName)
            throws AlbianDataServiceException {
        return loadObjects(sessionId, cls, loadType, null, wheres, null,idxName);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         IChainExpression wheres, LinkedList<IOrderByCondition> orderbys,String idxName)
            throws AlbianDataServiceException {
        return loadObjects(sessionId, cls, loadType, null, wheres, orderbys,idxName);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, String rountingName,
                                                         IChainExpression wheres, LinkedList<IOrderByCondition> orderbys,String idxName)
            throws AlbianDataServiceException {
        return loadObjects(sessionId, cls, loadType, 0, 0, wheres, orderbys,idxName);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         int start, int step, IChainExpression wheres,String idxName)
            throws AlbianDataServiceException {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, null,idxName);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         int start, int step, IChainExpression wheres, LinkedList<IOrderByCondition> orderbys,String idxName)
            throws AlbianDataServiceException {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, orderbys,idxName);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, String rountingName,
                                                         int start, int step, IChainExpression wheres, LinkedList<IOrderByCondition> orderbys,String idxName)
            throws AlbianDataServiceException {
        List<T> list = doLoadObjects(sessionId, cls, LoadType.exact == loadType, rountingName, start, step, wheres, orderbys,idxName);
        if (Validate.isNullOrEmpty(list))
            return null;
        return list;
//        if (LoadType.exact == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, start, step, wheres, orderbys,idxName);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
//            return list;
//        }
//        if (LoadType.dirty == loadType) {
//            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,idxName);
//            if (Validate.isNullOrEmpty(list))
//                return null;
////            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
//            return list;
//        }
//
////        List<T> objs = AlbianPersistenceCache.findObjects(cls, start, step, wheres, orderbys);
////        if (null != objs)
////            return objs;
//
//        List<T> objs = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,idxName);
//        if (null == objs)
//            return null;
////        AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, objs);
//        return objs;
    }


    /**
     * 慎用
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     * @throws AlbianDataServiceException
     */
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, LoadType loadType,String idxName)
            throws AlbianDataServiceException{
        return this.loadObjects(sessionId,cls,loadType,new FilterExpression(),idxName);
    }

    /**
     * 慎用
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     * @throws AlbianDataServiceException
     */
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, LoadType loadType,LinkedList<IOrderByCondition> orderbys,String idxName)
            throws AlbianDataServiceException{
        return this.loadObjects(sessionId,cls,loadType,new FilterExpression(),orderbys,idxName);
    }

    /**
     * 慎用
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     * @throws AlbianDataServiceException
     */
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                            String rountingName,LinkedList<IOrderByCondition> orderbys,String idxName)
            throws AlbianDataServiceException{
        return this.loadObjects(sessionId,cls,loadType,rountingName, new FilterExpression(),orderbys,idxName);

    }

    public <T extends IAlbianObject> T loadObjectById(String sessionId, Class<T> cls, LoadType loadType, BigInteger id,String idxName)
            throws AlbianDataServiceException{
        IChainExpression ce = new FilterExpression("id", LogicalOperation.Equal,id);
        return loadObject(sessionId,cls, loadType,ce,idxName);
    }

    public <T extends IAlbianObject> T loadObjectById(String sessionId, Class<T> cls, LoadType loadType, String rountingName,  BigInteger id,String idxName)
            throws AlbianDataServiceException{
        IChainExpression ce = new FilterExpression("id", LogicalOperation.Equal,id);
        return loadObject(sessionId,cls, loadType,rountingName,ce,idxName);
    }

    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           LoadType loadType, IChainExpression wheres,String idxName)
            throws AlbianDataServiceException {
        return this.loadObjectsCount(sessionId, cls, loadType, null, wheres,idxName);
    }

    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           LoadType loadType, String rountingName, IChainExpression wheres,String idxName)
            throws AlbianDataServiceException {
        return doLoadPageingCount(sessionId, cls, LoadType.exact == loadType, rountingName, wheres, null,idxName);

//        long count = 0;
//        if (LoadType.exact == loadType) {
//            count = doLoadPageingCount(sessionId, cls, true, rountingName, wheres, null,idxName);
////            AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
//        } else {
//            if (LoadType.dirty == loadType) {
//                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,idxName);
////                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
//            } else {
////                count = AlbianPersistenceCache.findPagesize(cls, wheres, null);
////                if (0 <= count) {
////                    return count;
////                }
//                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,idxName);
////                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
//            }
//
//        }
//
//        return count;
    }

     // save chain entity
    public IDataAccessContext newDataAccessContext(){

        return new DataAccessContext();
    }

    public IQueryContext newQueryContext(){
        return new QueryContext();
    }

}
