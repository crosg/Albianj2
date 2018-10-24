/*
Copyright (c) 2016, Shanghai YUEWEN Information Technology Co., Ltd. 
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Shanghai YUEWEN Information Technology Co., Ltd. 
* nor the names of its contributors may be used to endorse or promote products derived from 
* this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY SHANGHAI YUEWEN INFORMATION TECHNOLOGY CO., LTD. 
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright (c) 2016 著作权由上海阅文信息技术有限公司所有。著作权人保留一切权利。

这份授权条款，在使用者符合以下三条件的情形下，授予使用者使用及再散播本软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：

* 对于本软件源代码的再散播，必须保留上述的版权宣告、此三条件表列，以及下述的免责声明。
* 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附于散播包装中的媒介方式，重制上述之版权宣告、此三条件表列，以及下述的免责声明。
* 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称，来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。

免责声明：本软件是由上海阅文信息技术有限公司及本软件之贡献者以现状提供，本软件包装不负任何明示或默示之担保责任，
包括但不限于就适售性以及特定目的的适用性为默示性担保。加州大学董事会及本软件之贡献者，无论任何条件、无论成因或任何责任主义、
无论此责任为因合约关系、无过失责任主义或因非违约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的任何直接性、间接性、
偶发性、特殊性、惩罚性或任何结果的损害（包括但不限于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等），
不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
*/
package org.albianj.persistence.impl.service;

import org.albianj.persistence.context.IPersistenceCompensateNotify;
import org.albianj.persistence.context.IPersistenceNotify;
import org.albianj.persistence.context.IReaderJob;
import org.albianj.persistence.context.IWriterJob;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.PersistenceCommandType;
import org.albianj.persistence.impl.context.IReaderJobAdapter;
import org.albianj.persistence.impl.context.IWriterJobAdapter;
import org.albianj.persistence.impl.context.ReaderJobAdapter;
import org.albianj.persistence.impl.context.WriterJobAdapter;
import org.albianj.persistence.impl.db.IPersistenceQueryScope;
import org.albianj.persistence.impl.db.IPersistenceTransactionClusterScope;
import org.albianj.persistence.impl.db.PersistenceQueryScope;
import org.albianj.persistence.impl.db.PersistenceTransactionClusterScope;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IFilterCondition;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.persistence.service.IAlbianPersistenceService;
import org.albianj.persistence.service.LoadType;
import org.albianj.service.FreeAlbianService;
import org.albianj.verify.Validate;

import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class AlbianPersistenceService extends FreeAlbianService implements IAlbianPersistenceService {

    public String getServiceName(){
        return Name;
    }

    public String makeDetailLogSessionId(String sessionId){
        return sessionId + "_SPX_LOG";
    }


    @Deprecated
    protected static <T extends IAlbianObject> T doLoadObject(String sessionId,
                                                              Class<T> cls, boolean isExact,
                                                              String routingName, LinkedList<IFilterCondition> wheres)
            throws AlbianDataServiceException {
        List<T> list = doLoadObjects(sessionId, cls, isExact, routingName, 0, 0, wheres, null,null);
        if (Validate.isNullOrEmpty(list))
            return null;
        return list.get(0);
    }

    @Deprecated
    protected static <T extends IAlbianObject> List<T> doLoadObjects(String sessionId,
                                                                     Class<T> cls, boolean isExact, String routingName, int start, int step,
                                                                     LinkedList<IFilterCondition> wheres,
                                                                     LinkedList<IOrderByCondition> orderbys,String idxName)
            throws AlbianDataServiceException {
        IReaderJobAdapter ad = new ReaderJobAdapter();
        List<T> list = null;
        IReaderJob job = ad.buildReaderJob(sessionId, cls, isExact, routingName, start, step,
                wheres, orderbys,idxName);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        list = scope.execute(cls, job);
        return list;
    }

    @Deprecated
    protected static <T extends IAlbianObject> long doLoadPageingCount(String sessionId,
                                                                       Class<T> cls, boolean isExact, String routingName,
                                                                       LinkedList<IFilterCondition> wheres,
                                                                       LinkedList<IOrderByCondition> orderbys,String idxName)
            throws AlbianDataServiceException {
        IReaderJobAdapter ad = new ReaderJobAdapter();
        IReaderJob job = ad.buildReaderJob(sessionId, cls, isExact, routingName,
                wheres, orderbys,idxName);
        IPersistenceQueryScope scope = new PersistenceQueryScope();
        Object o = scope.execute(job);
        return (long) o;
    }

    @Deprecated
    public boolean create(String sessionId, IAlbianObject object) throws AlbianDataServiceException {
        return create(sessionId, object, null, null, null, null);
    }

    @Deprecated
    public boolean create(String sessionId, IAlbianObject object, IPersistenceNotify notifyCallback,
                          Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
                          Object compensateCallbackObject) throws AlbianDataServiceException {
        IWriterJobAdapter ja = new WriterJobAdapter();
        IWriterJob job = ja.buildCreation(sessionId, object);
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

    @Deprecated
    public boolean create(String sessionId, List<? extends IAlbianObject> objects) throws AlbianDataServiceException {
        return this.create(sessionId, objects, null, null, null, null);
    }

    @Deprecated
    public boolean create(String sessionId, List<? extends IAlbianObject> objects, IPersistenceNotify notifyCallback,
                          Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
                          Object compensateCallbackObject) throws AlbianDataServiceException {
        IWriterJobAdapter ja = new WriterJobAdapter();
        IWriterJob job = ja.buildCreation(sessionId, objects);
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

    @Deprecated
    public boolean modify(String sessionId, IAlbianObject object) throws AlbianDataServiceException {
        return this.modify(sessionId, object, null, null, null, null);
    }

    @Deprecated
    public boolean modify(String sessionId, IAlbianObject object, IPersistenceNotify notifyCallback,
                          Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
                          Object compensateCallbackObject) throws AlbianDataServiceException {
        IWriterJobAdapter ja = new WriterJobAdapter();
        IWriterJob job = ja.buildModification(sessionId, object);
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

    @Deprecated
    public boolean modify(String sessionId, List<? extends IAlbianObject> objects) throws AlbianDataServiceException {
        return this.modify(sessionId, objects, null, null, null, null);
    }

    @Deprecated
    public boolean modify(String sessionId, List<? extends IAlbianObject> objects,
                          IPersistenceNotify notifyCallback,
                          Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
                          Object compensateCallbackObject) throws AlbianDataServiceException {
        IWriterJobAdapter ja = new WriterJobAdapter();
        IWriterJob job = ja.buildModification(sessionId, objects);
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
        if (LoadType.exact == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, 0, 0, wheres, null,null);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

        if (LoadType.dirty == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, 0, 0, wheres, null,null);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

//        T obj = AlbianPersistenceCache.findObject(cls, wheres, null);
//        if (null != obj)
//            return obj;

        T newObj = doLoadObject(sessionId, cls, false, rountingName, wheres,null);
        if (null == newObj)
            return null;
//        AlbianPersistenceCache.setObject(cls, wheres, null, newObj);
        return newObj;
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
        if (LoadType.exact == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, start, step, wheres, orderbys,null);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }
        if (LoadType.dirty == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,null);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }

//        List<T> objs = AlbianPersistenceCache.findObjects(cls, start, step, wheres, orderbys);
//        if (null != objs)
//            return objs;

        List<T> objs = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,null);
        if (null == objs)
            return null;
//        AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, objs);
        return objs;
    }

    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           LoadType loadType, IChainExpression wheres)
            throws AlbianDataServiceException {
        return this.loadObjectsCount(sessionId, cls, loadType, null, wheres);
    }

    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           LoadType loadType, String rountingName, IChainExpression wheres)
            throws AlbianDataServiceException {
        long count = 0;
        if (LoadType.exact == loadType) {
            count = doLoadPageingCount(sessionId, cls, true, rountingName, wheres, null,null);
//            AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
        } else {
            if (LoadType.dirty == loadType) {
                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,null);
//                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
            } else {
//                count = AlbianPersistenceCache.findPagesize(cls, wheres, null);
//                if (0 <= count) {
//                    return count;
//                }
                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,null);
//                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
            }

        }

        return count;
    }

    @Deprecated
    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls, LoadType loadType, LinkedList<IFilterCondition> wheres)
            throws AlbianDataServiceException {
        return this.loadObject(sessionId, cls, loadType, null, wheres);
    }

    @Deprecated
    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls,
                                                  LoadType loadType, String rountingName, LinkedList<IFilterCondition> wheres)
            throws AlbianDataServiceException {
        if (LoadType.exact == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, 0, 0, wheres, null,null);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

        if (LoadType.dirty == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, 0, 0, wheres, null,null);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

//        T obj = AlbianPersistenceCache.findObject(cls, wheres, null);
//        if (null != obj)
//            return obj;

        T newObj = doLoadObject(sessionId, cls, false, rountingName, wheres);
        if (null == newObj)
            return null;
//        AlbianPersistenceCache.setObject(cls, wheres, null, newObj);
        return newObj;
    }

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls, PersistenceCommandType cmdType,
                                                  Statement statement) throws AlbianDataServiceException {
        List<T> list = doLoadObjects(sessionId, cls, cmdType, statement);
        if (Validate.isNullOrEmpty(list))
            return null;
        return list.get(0);
    }

    @Deprecated
    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, LinkedList<IFilterCondition> wheres)
            throws AlbianDataServiceException {
        return loadObjects(sessionId, cls, loadType, 0,0, wheres, null,null);
    }

    @Deprecated
    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         LinkedList<IFilterCondition> wheres, LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException {
        return loadObjects(sessionId, cls, loadType, 0,0, wheres, orderbys);
    }

    @Deprecated
    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, String rountingName,
                                                         LinkedList<IFilterCondition> wheres, LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException {
        return loadObjects(sessionId, cls, loadType,rountingName, 0, 0, wheres, orderbys);
    }

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, PersistenceCommandType cmdType,
                                                         Statement statement) throws AlbianDataServiceException {
        List<T> list = doLoadObjects(sessionId,cls, cmdType, statement);
        if (Validate.isNullOrEmpty(list))
            return null;
        return list;
    }

    @Deprecated
    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         int start, int step, LinkedList<IFilterCondition> wheres)
            throws AlbianDataServiceException {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, null);
    }

    @Deprecated
    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         int start, int step, LinkedList<IFilterCondition> wheres, LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, orderbys);
    }

    @Deprecated
    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, String rountingName,
                                                         int start, int step, LinkedList<IFilterCondition> wheres, LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException {
        if (LoadType.exact == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, start, step, wheres, orderbys,null);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }
        if (LoadType.dirty == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,null);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }

//        List<T> objs = AlbianPersistenceCache.findObjects(cls, start, step, wheres, orderbys);
//        if (null != objs)
//            return objs;

        List<T> objs = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,null);
        if (null == objs)
            return null;
//        AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, objs);
        return objs;
    }

    @Deprecated
    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           LoadType loadType, LinkedList<IFilterCondition> wheres)
            throws AlbianDataServiceException {
        return this.loadObjectsCount(sessionId, cls, loadType, null, wheres);
    }

    @Deprecated
    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           LoadType loadType, String rountingName, LinkedList<IFilterCondition> wheres)
            throws AlbianDataServiceException {
        if (LoadType.exact == loadType) {
            long count = doLoadPageingCount(sessionId, cls, true, rountingName, wheres, null,null);
            return count;
        }
        if (LoadType.dirty == loadType) {
            long count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,null);
            return count;
        }

//        long count = AlbianPersistenceCache.findPagesize(cls, wheres, null);
//        if (0 <= count) {
//            return count;
//        }
        long count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,null);

//        AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
        return count;
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
                                                       String routingName, IChainExpression wheres,String idxName)
            throws AlbianDataServiceException {
        List<T> list = doLoadObjects(sessionId, cls, isExact, routingName, 0, 0, wheres, null,idxName);
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
        IReaderJob job = ad.buildReaderJob(sessionId, cls, isExact, null,null,routingName, start, step,
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


//----- add by 木木，强行指定索引名字

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls, LoadType loadType, IChainExpression wheres,String idxName)
            throws AlbianDataServiceException {
        return this.loadObject(sessionId, cls, loadType, null, wheres,idxName);
    }

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls,
                                                  LoadType loadType, String rountingName, IChainExpression wheres,String idxName)
            throws AlbianDataServiceException {
        if (LoadType.exact == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, 0, 0, wheres, null,idxName);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

        if (LoadType.dirty == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, 0, 0, wheres, null,idxName);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

//        T obj = AlbianPersistenceCache.findObject(cls, wheres, null);
//        if (null != obj)
//            return obj;

        T newObj = doLoadObject(sessionId, cls, false, rountingName, wheres,idxName);
        if (null == newObj)
            return null;
//        AlbianPersistenceCache.setObject(cls, wheres, null, newObj);
        return newObj;
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
        if (LoadType.exact == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, start, step, wheres, orderbys,idxName);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }
        if (LoadType.dirty == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,idxName);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }

//        List<T> objs = AlbianPersistenceCache.findObjects(cls, start, step, wheres, orderbys);
//        if (null != objs)
//            return objs;

        List<T> objs = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,idxName);
        if (null == objs)
            return null;
//        AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, objs);
        return objs;
    }

    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           LoadType loadType, IChainExpression wheres,String idxName)
            throws AlbianDataServiceException {
        return this.loadObjectsCount(sessionId, cls, loadType, null, wheres,idxName);
    }

    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           LoadType loadType, String rountingName, IChainExpression wheres,String idxName)
            throws AlbianDataServiceException {
        long count = 0;
        if (LoadType.exact == loadType) {
            count = doLoadPageingCount(sessionId, cls, true, rountingName, wheres, null,idxName);
//            AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
        } else {
            if (LoadType.dirty == loadType) {
                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,idxName);
//                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
            } else {
//                count = AlbianPersistenceCache.findPagesize(cls, wheres, null);
//                if (0 <= count) {
//                    return count;
//                }
                count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,idxName);
//                AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
            }

        }

        return count;
    }

    @Deprecated
    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls, LoadType loadType, LinkedList<IFilterCondition> wheres,String idxName)
            throws AlbianDataServiceException {
        return this.loadObject(sessionId, cls, loadType, null, wheres,idxName);
    }

    @Deprecated
    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls,
                                                  LoadType loadType, String rountingName, LinkedList<IFilterCondition> wheres,String idxName)
            throws AlbianDataServiceException {
        if (LoadType.exact == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, 0, 0, wheres, null,idxName);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

        if (LoadType.dirty == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, 0, 0, wheres, null,idxName);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
            return list.get(0);
        }

//        T obj = AlbianPersistenceCache.findObject(cls, wheres, null);
//        if (null != obj)
//            return obj;

        T newObj = doLoadObject(sessionId, cls, false, rountingName, wheres);
        if (null == newObj)
            return null;
//        AlbianPersistenceCache.setObject(cls, wheres, null, newObj);
        return newObj;
    }

    @Deprecated
    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, LinkedList<IFilterCondition> wheres,String idxName)
            throws AlbianDataServiceException {
        return loadObjects(sessionId, cls, loadType, 0,0, wheres, null,idxName);
    }

    @Deprecated
    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         LinkedList<IFilterCondition> wheres, LinkedList<IOrderByCondition> orderbys,String idxName)
            throws AlbianDataServiceException {
        return loadObjects(sessionId, cls, loadType, 0,0, wheres, orderbys,idxName);
    }

    @Deprecated
    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, String rountingName,
                                                         LinkedList<IFilterCondition> wheres, LinkedList<IOrderByCondition> orderbys,String idxName)
            throws AlbianDataServiceException {
        return loadObjects(sessionId, cls, loadType,rountingName, 0, 0, wheres, orderbys,idxName);
    }


    @Deprecated
    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         int start, int step, LinkedList<IFilterCondition> wheres,String idxName)
            throws AlbianDataServiceException {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, null,idxName);
    }

    @Deprecated
    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         int start, int step, LinkedList<IFilterCondition> wheres, LinkedList<IOrderByCondition> orderbys,String idxName)
            throws AlbianDataServiceException {
        return this.loadObjects(sessionId, cls, loadType, null, start, step, wheres, orderbys,idxName);
    }

    @Deprecated
    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, String rountingName,
                                                         int start, int step, LinkedList<IFilterCondition> wheres, LinkedList<IOrderByCondition> orderbys,String idxName)
            throws AlbianDataServiceException {
        if (LoadType.exact == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, true, rountingName, start, step, wheres, orderbys,idxName);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }
        if (LoadType.dirty == loadType) {
            List<T> list = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,idxName);
            if (Validate.isNullOrEmpty(list))
                return null;
//            AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, list);
            return list;
        }

//        List<T> objs = AlbianPersistenceCache.findObjects(cls, start, step, wheres, orderbys);
//        if (null != objs)
//            return objs;

        List<T> objs = doLoadObjects(sessionId, cls, false, rountingName, start, step, wheres, orderbys,idxName);
        if (null == objs)
            return null;
//        AlbianPersistenceCache.setObjects(cls, start, step, wheres, orderbys, objs);
        return objs;
    }

    @Deprecated
    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           LoadType loadType, LinkedList<IFilterCondition> wheres,String idxName)
            throws AlbianDataServiceException {
        return this.loadObjectsCount(sessionId, cls, loadType, null, wheres,idxName);
    }

    @Deprecated
    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           LoadType loadType, String rountingName, LinkedList<IFilterCondition> wheres,String idxName)
            throws AlbianDataServiceException {
        if (LoadType.exact == loadType) {
            long count = doLoadPageingCount(sessionId, cls, true, rountingName, wheres, null,idxName);
            return count;
        }
        if (LoadType.dirty == loadType) {
            long count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,idxName);
            return count;
        }

//        long count = AlbianPersistenceCache.findPagesize(cls, wheres, null);
//        if (0 <= count) {
//            return count;
//        }
        long count = doLoadPageingCount(sessionId, cls, false, rountingName, wheres, null,idxName);

//        AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
        return count;
    }
}
