package org.albianj.persistence.impl.service;

import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

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
import org.albianj.persistence.impl.dbcached.AlbianPersistenceCache;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IFilterCondition;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.persistence.service.IAlbianPersistenceService;
import org.albianj.persistence.service.LoadType;
import org.albianj.service.FreeAlbianService;
import org.albianj.verify.Validate;

public class AlbianPersistenceService extends FreeAlbianService implements IAlbianPersistenceService {	
	
	public boolean create(String sessionId, IAlbianObject object) throws AlbianDataServiceException{
		return create(sessionId,object,null,null,null,null);
	}

	public boolean create(String sessionId, IAlbianObject object, IPersistenceNotify notifyCallback,
			Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
			Object compensateCallbackObject) throws AlbianDataServiceException{
		IWriterJobAdapter ja = new WriterJobAdapter();
		IWriterJob job = ja.buildCreation(sessionId,object);
		if(null != notifyCallback)
			job.setNotifyCallback(notifyCallback);
		if(null != notifyCallbackObject)
			job.setNotifyCallbackObject(notifyCallbackObject);
		if(null != compensateCallback)
			job.setCompensateNotify(compensateCallback);
		if(null != compensateCallbackObject)
			job.setCompensateCallbackObject(compensateCallbackObject);
		IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
		return tcs.execute(job);
	}

	public boolean create(String sessionId, List<? extends IAlbianObject> objects) throws AlbianDataServiceException{
		return this.create(sessionId, objects,null,null,null,null);
	}
	
	public boolean create(String sessionId, List<? extends IAlbianObject> objects, IPersistenceNotify notifyCallback,
			Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
			Object compensateCallbackObject) throws AlbianDataServiceException{
		IWriterJobAdapter ja = new WriterJobAdapter();
		IWriterJob job = ja.buildCreation(sessionId,objects);
		if(null != notifyCallback)
			job.setNotifyCallback(notifyCallback);
		if(null != notifyCallbackObject)
			job.setNotifyCallbackObject(notifyCallbackObject);
		if(null != compensateCallback)
			job.setCompensateNotify(compensateCallback);
		if(null != compensateCallbackObject)
			job.setCompensateCallbackObject(compensateCallbackObject);
		IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
		return tcs.execute(job);
	}

	public boolean modify(String sessionId, IAlbianObject object) throws AlbianDataServiceException{
		return this.modify(sessionId, object, null, null, null, null);
	}
	public boolean modify(String sessionId, IAlbianObject object, IPersistenceNotify notifyCallback,
			Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
			Object compensateCallbackObject) throws AlbianDataServiceException{
		IWriterJobAdapter ja = new WriterJobAdapter();
		IWriterJob job = ja.buildModification(sessionId,object);
		if(null != notifyCallback)
			job.setNotifyCallback(notifyCallback);
		if(null != notifyCallbackObject)
			job.setNotifyCallbackObject(notifyCallbackObject);
		if(null != compensateCallback)
			job.setCompensateNotify(compensateCallback);
		if(null != compensateCallbackObject)
			job.setCompensateCallbackObject(compensateCallbackObject);
		IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
		return tcs.execute(job);
	}

	public boolean modify(String sessionId, List<? extends IAlbianObject> objects) throws AlbianDataServiceException{
		return this.modify(sessionId, objects, null, null, null, null);
	}
	public boolean modify(String sessionId, List<? extends IAlbianObject> objects,
			IPersistenceNotify notifyCallback,
			Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
			Object compensateCallbackObject) throws AlbianDataServiceException{
		IWriterJobAdapter ja = new WriterJobAdapter();
		IWriterJob job = ja.buildModification(sessionId,objects);
		if(null != notifyCallback)
			job.setNotifyCallback(notifyCallback);
		if(null != notifyCallbackObject)
			job.setNotifyCallbackObject(notifyCallbackObject);
		if(null != compensateCallback)
			job.setCompensateNotify(compensateCallback);
		if(null != compensateCallbackObject)
			job.setCompensateCallbackObject(compensateCallbackObject);
		IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
		return tcs.execute(job);
	}
	
	public boolean remove(String sessionId, IAlbianObject object) throws AlbianDataServiceException{
		return this.remove(sessionId, object, null, null, null, null);
	}
	public boolean remove(String sessionId, IAlbianObject object, IPersistenceNotify notifyCallback,
			Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
			Object compensateCallbackObject) throws AlbianDataServiceException{
		IWriterJobAdapter ja = new WriterJobAdapter();
		IWriterJob job = ja.buildRemoved(sessionId,object);
		if(null != notifyCallback)
			job.setNotifyCallback(notifyCallback);
		if(null != notifyCallbackObject)
			job.setNotifyCallbackObject(notifyCallbackObject);
		if(null != compensateCallback)
			job.setCompensateNotify(compensateCallback);
		if(null != compensateCallbackObject)
			job.setCompensateCallbackObject(compensateCallbackObject);
		IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
		return tcs.execute(job);
	}
	public boolean remove(String sessionId, List<? extends IAlbianObject> objects) throws AlbianDataServiceException{
		return this.remove(sessionId, objects, null, null, null, null);
	}

	public boolean remove(String sessionId, List<? extends IAlbianObject> objects, IPersistenceNotify notifyCallback,
			Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
			Object compensateCallbackObject) throws AlbianDataServiceException{
		IWriterJobAdapter ja = new WriterJobAdapter();
		IWriterJob job = ja.buildRemoved(sessionId,objects);
		if(null != notifyCallback)
			job.setNotifyCallback(notifyCallback);
		if(null != notifyCallbackObject)
			job.setNotifyCallbackObject(notifyCallbackObject);
		if(null != compensateCallback)
			job.setCompensateNotify(compensateCallback);
		if(null != compensateCallbackObject)
			job.setCompensateCallbackObject(compensateCallbackObject);
		IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
		return tcs.execute(job);
	}

	
	public boolean save(String sessionId, IAlbianObject object) throws AlbianDataServiceException{
		return this.save(sessionId, object, null, null, null, null);
	}
	public boolean save(String sessionId, IAlbianObject object, 
			IPersistenceNotify notifyCallback, Object notifyCallbackObject,
			IPersistenceCompensateNotify compensateCallback, Object compensateCallbackObject)
					throws AlbianDataServiceException{
		IWriterJobAdapter ja = new WriterJobAdapter();
		IWriterJob job = ja.buildSaving(sessionId,object);
		if(null != notifyCallback)
			job.setNotifyCallback(notifyCallback);
		if(null != notifyCallbackObject)
			job.setNotifyCallbackObject(notifyCallbackObject);
		if(null != compensateCallback)
			job.setCompensateNotify(compensateCallback);
		if(null != compensateCallbackObject)
			job.setCompensateCallbackObject(compensateCallbackObject);
		IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
		return tcs.execute(job);
	}

	public boolean save(String sessionId, List<? extends IAlbianObject> objects) throws AlbianDataServiceException{
		return this.save(sessionId, objects, null, null, null, null);
	}
	public boolean save(String sessionId, List<? extends IAlbianObject> objects, IPersistenceNotify notifyCallback,
			Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
			Object compensateCallbackObject) throws AlbianDataServiceException{
		IWriterJobAdapter ja = new WriterJobAdapter();
		IWriterJob job = ja.buildSaving(sessionId,objects);
		if(null != notifyCallback)
			job.setNotifyCallback(notifyCallback);
		if(null != notifyCallbackObject)
			job.setNotifyCallbackObject(notifyCallbackObject);
		if(null != compensateCallback)
			job.setCompensateNotify(compensateCallback);
		if(null != compensateCallbackObject)
			job.setCompensateCallbackObject(compensateCallbackObject);
		IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
		return tcs.execute(job);
	}

	
	
	public <T extends IAlbianObject> T loadObject(String sessionId,Class<T> cls,LoadType loadType, IChainExpression wheres)
			throws AlbianDataServiceException{
		return this.loadObject(sessionId, cls, loadType, null,wheres);
	}
	
	public <T extends IAlbianObject> T loadObject(String sessionId,Class<T> cls,
			LoadType loadType,String rountingName, IChainExpression wheres)
			throws AlbianDataServiceException{
		if(LoadType.exact == loadType){
			List<T> list = doLoadObjects(sessionId,cls, true, rountingName, 0, 0, wheres, null);
			if (Validate.isNullOrEmpty(list))
				return null;
			AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
			return list.get(0);
		}
		
		if(LoadType.dirty == loadType ){
			List<T> list = doLoadObjects(sessionId,cls, false, rountingName, 0, 0, wheres, null);
			if (Validate.isNullOrEmpty(list))
				return null;
			AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
			return list.get(0);
		}
		
		T obj = AlbianPersistenceCache.findObject(cls, wheres, null);
		if (null != obj)
			return obj;

		T newObj = doLoadObject(sessionId,cls,false, rountingName, wheres);
		if (null == newObj)
			return null;
		AlbianPersistenceCache.setObject(cls, wheres, null, newObj);
		return newObj;
	}
	
	
	public <T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls, LoadType loadType,IChainExpression wheres)
			throws AlbianDataServiceException {
		return loadObjects(sessionId,cls,loadType,null,wheres,null);
	}
	
	public <T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,
			IChainExpression wheres,LinkedList<IOrderByCondition> orderbys)
			throws AlbianDataServiceException{
		return loadObjects(sessionId,cls,loadType,null,wheres,orderbys);
	}
	
	public <T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,String rountingName, 
			IChainExpression wheres,LinkedList<IOrderByCondition> orderbys)
			throws AlbianDataServiceException {
		return loadObjects(sessionId,cls,loadType,0,0,wheres,orderbys);
	}
	
	public <T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,
			int start,int step, IChainExpression wheres)
			throws AlbianDataServiceException{
		return this.loadObjects(sessionId, cls, loadType,null, start, step, wheres,null);
	}
	
	public <T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,
			int start,int step, IChainExpression wheres,LinkedList<IOrderByCondition> orderbys)
			throws AlbianDataServiceException{
		return this.loadObjects(sessionId, cls, loadType,null, start, step, wheres,orderbys);
	}
	
	public <T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,String rountingName, 
			int start,int step,IChainExpression wheres,LinkedList<IOrderByCondition> orderbys)
			throws AlbianDataServiceException{
		if(LoadType.exact == loadType){
			List<T> list = doLoadObjects(sessionId,cls, true, rountingName, start, step, wheres, orderbys);
			if (Validate.isNullOrEmpty(list))
				return null;
			AlbianPersistenceCache.setObjects(cls,start,step, wheres, orderbys, list);
			return list;
		}
		if(LoadType.dirty == loadType ){
			List<T> list = doLoadObjects(sessionId,cls, false, rountingName, start, step, wheres, orderbys);
			if (Validate.isNullOrEmpty(list))
				return null;
			AlbianPersistenceCache.setObjects(cls,start,step, wheres, orderbys, list);
			return list;
		}
		
		List<T> objs = AlbianPersistenceCache.findObjects(cls, start,step,wheres, orderbys);
		if (null != objs)
			return objs;

		objs = doLoadObjects(sessionId,cls,false, rountingName,start,step, wheres,orderbys);
		if (null == objs)
			return null;
		AlbianPersistenceCache.setObjects(cls,start,step, wheres, orderbys, objs);
		return objs;
	}
	
	public <T extends IAlbianObject> long loadObjectsCount(String sessionId,Class<T> cls,
			LoadType loadType, IChainExpression wheres)
			throws AlbianDataServiceException{
		return this.loadObjectsCount(sessionId, cls, loadType,null, wheres);
	}
	
	public <T extends IAlbianObject> long loadObjectsCount(String sessionId,Class<T> cls,
			LoadType loadType, String rountingName, IChainExpression wheres)
			throws AlbianDataServiceException{
		if(LoadType.exact == loadType){
			long count = doLoadPageingCount(sessionId,cls, true, rountingName, wheres, null);
			return count;
		}
		if(LoadType.dirty == loadType){
			long count = doLoadPageingCount(sessionId,cls, false, rountingName, wheres, null);
			return count;
		}
		
		long count = AlbianPersistenceCache.findPagesize(cls, wheres, null);
		if(0 <= count){
			return count;
		}
		count = doLoadPageingCount(sessionId,cls, true, rountingName, wheres, null);
		
		AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
		return count;
	}
	
	
	
	
	@Deprecated
	public <T extends IAlbianObject> T loadObject(String sessionId,Class<T> cls,LoadType loadType, LinkedList<IFilterCondition> wheres)
			throws AlbianDataServiceException{
		return this.loadObject(sessionId, cls, loadType, null,wheres);
	}
	
	@Deprecated
	public <T extends IAlbianObject> T loadObject(String sessionId,Class<T> cls,
			LoadType loadType,String rountingName, LinkedList<IFilterCondition> wheres)
			throws AlbianDataServiceException{
		if(LoadType.exact == loadType){
			List<T> list = doLoadObjects(sessionId,cls, true, rountingName, 0, 0, wheres, null);
			if (Validate.isNullOrEmpty(list))
				return null;
			AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
			return list.get(0);
		}
		
		if(LoadType.dirty == loadType ){
			List<T> list = doLoadObjects(sessionId,cls, false, rountingName, 0, 0, wheres, null);
			if (Validate.isNullOrEmpty(list))
				return null;
			AlbianPersistenceCache.setObject(cls, wheres, null, list.get(0));
			return list.get(0);
		}
		
		T obj = AlbianPersistenceCache.findObject(cls, wheres, null);
		if (null != obj)
			return obj;

		T newObj = doLoadObject(sessionId,cls,false, rountingName, wheres);
		if (null == newObj)
			return null;
		AlbianPersistenceCache.setObject(cls, wheres, null, newObj);
		return newObj;
	}
	
	public <T extends IAlbianObject> T loadObject(String sessionId,Class<T> cls,PersistenceCommandType cmdType,
			Statement statement) throws AlbianDataServiceException{
		List<T> list = doLoadObjects(cls, cmdType, statement);
		if (Validate.isNullOrEmpty(list))
			return null;
		return list.get(0);
	}
	
	@Deprecated
	public <T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls, LoadType loadType,LinkedList<IFilterCondition> wheres)
			throws AlbianDataServiceException {
		return loadObjects(sessionId,cls,loadType,null,wheres,null);
	}
	
	@Deprecated
	public <T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,
			LinkedList<IFilterCondition> wheres,LinkedList<IOrderByCondition> orderbys)
			throws AlbianDataServiceException{
		return loadObjects(sessionId,cls,loadType,null,wheres,orderbys);
	}
	
	@Deprecated
	public <T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,String rountingName, 
			LinkedList<IFilterCondition> wheres,LinkedList<IOrderByCondition> orderbys)
			throws AlbianDataServiceException {
		return loadObjects(sessionId,cls,loadType,0,0,wheres,orderbys);
	}
	
	public <T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,  PersistenceCommandType cmdType,
			Statement statement) throws AlbianDataServiceException{
		List<T> list = doLoadObjects(cls, cmdType, statement);
		if (Validate.isNullOrEmpty(list))
			return null;
		return list;
	}
	
	@Deprecated
	public <T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,
			int start,int step, LinkedList<IFilterCondition> wheres)
			throws AlbianDataServiceException{
		return this.loadObjects(sessionId, cls, loadType,null, start, step, wheres,null);
	}
	
	@Deprecated
	public <T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,
			int start,int step, LinkedList<IFilterCondition> wheres,LinkedList<IOrderByCondition> orderbys)
			throws AlbianDataServiceException{
		return this.loadObjects(sessionId, cls, loadType,null, start, step, wheres,orderbys);
	}
	
	@Deprecated
	public <T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,String rountingName, 
			int start,int step,LinkedList<IFilterCondition> wheres,LinkedList<IOrderByCondition> orderbys)
			throws AlbianDataServiceException{
		if(LoadType.exact == loadType){
			List<T> list = doLoadObjects(sessionId,cls, true, rountingName, start, step, wheres, orderbys);
			if (Validate.isNullOrEmpty(list))
				return null;
			AlbianPersistenceCache.setObjects(cls,start,step, wheres, orderbys, list);
			return list;
		}
		if(LoadType.dirty == loadType ){
			List<T> list = doLoadObjects(sessionId,cls, false, rountingName, start, step, wheres, orderbys);
			if (Validate.isNullOrEmpty(list))
				return null;
			AlbianPersistenceCache.setObjects(cls,start,step, wheres, orderbys, list);
			return list;
		}
		
		List<T> objs = AlbianPersistenceCache.findObjects(cls, start,step,wheres, orderbys);
		if (null != objs)
			return objs;

		objs = doLoadObjects(sessionId,cls,false, rountingName,start,step, wheres,orderbys);
		if (null == objs)
			return null;
		AlbianPersistenceCache.setObjects(cls,start,step, wheres, orderbys, objs);
		return objs;
	}
	
	@Deprecated
	public <T extends IAlbianObject> long loadObjectsCount(String sessionId,Class<T> cls,
			LoadType loadType, LinkedList<IFilterCondition> wheres)
			throws AlbianDataServiceException{
		return this.loadObjectsCount(sessionId, cls, loadType,null, wheres);
	}
	
	@Deprecated
	public <T extends IAlbianObject> long loadObjectsCount(String sessionId,Class<T> cls,
			LoadType loadType, String rountingName, LinkedList<IFilterCondition> wheres)
			throws AlbianDataServiceException{
		if(LoadType.exact == loadType){
			long count = doLoadPageingCount(sessionId,cls, true, rountingName, wheres, null);
			return count;
		}
		if(LoadType.dirty == loadType){
			long count = doLoadPageingCount(sessionId,cls, false, rountingName, wheres, null);
			return count;
		}
		
		long count = AlbianPersistenceCache.findPagesize(cls, wheres, null);
		if(0 <= count){
			return count;
		}
		count = doLoadPageingCount(sessionId,cls, true, rountingName, wheres, null);
		
		AlbianPersistenceCache.setPagesize(cls, wheres, null, count);
		return count;
	}
	
	@Deprecated
	protected static <T extends IAlbianObject> T doLoadObject(String sessionId,
			Class<T> cls,boolean isExact,
			String routingName, LinkedList<IFilterCondition> wheres)
			throws AlbianDataServiceException {
		List<T> list = doLoadObjects(sessionId,cls, isExact, routingName, 0, 0, wheres, null);
		if (Validate.isNullOrEmpty(list))
			return null;
		return list.get(0);
	}
	
	@Deprecated
	protected static <T extends IAlbianObject> List<T> doLoadObjects(String sessionId,
			Class<T> cls,boolean isExact, String routingName, int start, int step,
			LinkedList<IFilterCondition> wheres,
			LinkedList<IOrderByCondition> orderbys)
			throws AlbianDataServiceException {
		IReaderJobAdapter ad = new ReaderJobAdapter();
		List<T> list = null;
		IReaderJob job = ad.buildReaderJob(sessionId,cls, isExact,routingName, start, step,
				wheres, orderbys);
		IPersistenceQueryScope scope = new PersistenceQueryScope();
		list = scope.execute(cls, job);
		return list;
	}
	
	@Deprecated
	protected static  <T extends IAlbianObject> long doLoadPageingCount(String sessionId,
			Class<T> cls,boolean isExact, String routingName,
			LinkedList<IFilterCondition> wheres,
			LinkedList<IOrderByCondition> orderbys)
			throws AlbianDataServiceException {
		IReaderJobAdapter ad = new ReaderJobAdapter();
		IReaderJob job = ad.buildReaderJob(sessionId,cls, isExact,routingName,
				wheres, orderbys);
		IPersistenceQueryScope scope = new PersistenceQueryScope();
		Object o = scope.execute(job);
		return (long) o;
	}
	
	
	protected <T extends IAlbianObject> List<T> doLoadObjects(
			Class<T> cls, PersistenceCommandType cmdType, Statement statement)
			throws AlbianDataServiceException {
		IPersistenceQueryScope scope = new PersistenceQueryScope();
		List<T> list = null;
		list = scope.execute(cls, cmdType, statement);
		return list;
	}
	
	protected <T extends IAlbianObject> T doLoadObject(Class<T> cls,
			PersistenceCommandType cmdType, Statement statement)
			throws AlbianDataServiceException {
		List<T> list = doLoadObjects(cls, cmdType, statement);
		if (Validate.isNullOrEmpty(list))
			return null;
		return list.get(0);
	}
	
	protected <T extends IAlbianObject> T doLoadObject(String sessionId,
			Class<T> cls,boolean isExact,
			String routingName, IChainExpression wheres)
			throws AlbianDataServiceException {
		List<T> list = doLoadObjects(sessionId,cls, isExact, routingName, 0, 0, wheres, null);
		if (Validate.isNullOrEmpty(list))
			return null;
		return list.get(0);
	}

	protected <T extends IAlbianObject> List<T> doLoadObjects(String sessionId,
			Class<T> cls,boolean isExact, String routingName, int start, int step,
			IChainExpression wheres,
			LinkedList<IOrderByCondition> orderbys)
			throws AlbianDataServiceException {
		IReaderJobAdapter ad = new ReaderJobAdapter();
		List<T> list = null;
		IReaderJob job = ad.buildReaderJob(sessionId,cls, isExact,routingName, start, step,
				wheres, orderbys);
		IPersistenceQueryScope scope = new PersistenceQueryScope();
		list = scope.execute(cls, job);
		return list;
	}
	
	protected  <T extends IAlbianObject> long doLoadPageingCount(String sessionId,
			Class<T> cls,boolean isExact, String routingName,
			IChainExpression wheres,
			LinkedList<IOrderByCondition> orderbys)
			throws AlbianDataServiceException {
		IReaderJobAdapter ad = new ReaderJobAdapter();
		IReaderJob job = ad.buildReaderJob(sessionId,cls, isExact,routingName,
				wheres, orderbys);
		IPersistenceQueryScope scope = new PersistenceQueryScope();
		Object o = scope.execute(job);
		return (long) o;
	}



}
