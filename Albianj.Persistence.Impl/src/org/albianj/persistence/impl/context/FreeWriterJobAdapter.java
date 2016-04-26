package org.albianj.persistence.impl.context;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;

import org.albianj.persistence.context.IWriterJob;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.impl.db.CreateCommandAdapter;
import org.albianj.persistence.impl.db.IPersistenceUpdateCommand;
import org.albianj.persistence.impl.db.ModifyCommandAdapter;
import org.albianj.persistence.impl.db.RemoveCommandAdapter;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IAlbianObjectDataRouter;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.persistence.object.IDataRoutersAttribute;

public abstract class FreeWriterJobAdapter implements IWriterJobAdapter {
	public IWriterJob buildCreation(String sessionId,IAlbianObject object) throws AlbianDataServiceException {
		IWriterJob job = new WriterJob(sessionId);
		IPersistenceUpdateCommand cca = new CreateCommandAdapter();
		buildWriterJob(object, job, cca);
		return job;
	}

	public IWriterJob buildCreation(String sessionId,List<? extends IAlbianObject> objects) throws AlbianDataServiceException {
		IWriterJob job = new WriterJob(sessionId);
		IPersistenceUpdateCommand cca = new CreateCommandAdapter();
		for (IAlbianObject object : objects) {
			buildWriterJob(object, job, cca);
		}
		return job;
	}

	public IWriterJob buildModification(String sessionId,IAlbianObject object) throws AlbianDataServiceException {
		IWriterJob job = new WriterJob(sessionId);
		IPersistenceUpdateCommand mca = new ModifyCommandAdapter();
		buildWriterJob(object, job, mca);
		return job;
	}
	
	public IWriterJob buildModification(String sessionId,IAlbianObject object,String[] members) throws AlbianDataServiceException {
		IWriterJob job = new WriterJob(sessionId);
		IPersistenceUpdateCommand mca = new ModifyCommandAdapter();
		buildWriterJob(object, job, mca,members);
		return job;
	}

	

	public IWriterJob buildModification(String sessionId,List<? extends IAlbianObject> objects) throws AlbianDataServiceException {
		IWriterJob job = new WriterJob(sessionId);
		IPersistenceUpdateCommand mca = new ModifyCommandAdapter();
		for (IAlbianObject object : objects) {
			buildWriterJob(object, job, mca);
		}
		return job;
	}

	public IWriterJob buildRemoved(String sessionId,IAlbianObject object) throws AlbianDataServiceException {
		IWriterJob job = new WriterJob(sessionId);
		IPersistenceUpdateCommand rca = new RemoveCommandAdapter();
		buildWriterJob(object, job, rca);
		return job;
	}

	public IWriterJob buildRemoved(String sessionId,List<? extends IAlbianObject> objects) throws AlbianDataServiceException {
		IWriterJob job = new WriterJob(sessionId);
		IPersistenceUpdateCommand rca = new RemoveCommandAdapter();
		for (IAlbianObject object : objects) {
			buildWriterJob(object, job, rca);
		}
		return job;
	}

	public IWriterJob buildSaving(String sessionId,IAlbianObject object) throws AlbianDataServiceException {
		IWriterJob job = new WriterJob(sessionId);
		IPersistenceUpdateCommand iuc;
		if (object.getIsAlbianNew()) {
			iuc = new CreateCommandAdapter();
		} else {
			iuc = new ModifyCommandAdapter();
		}

		buildWriterJob(object, job, iuc);
		return job;
	}

	public IWriterJob buildSaving(String sessionId,List<? extends IAlbianObject> objects) throws AlbianDataServiceException {
		IWriterJob job = new WriterJob(sessionId);
		IPersistenceUpdateCommand cca = new CreateCommandAdapter();
		IPersistenceUpdateCommand mca = new ModifyCommandAdapter();
		for (IAlbianObject object : objects) {
			if (object.getIsAlbianNew()) {
				buildWriterJob(object, job, cca);
			} else {
				buildWriterJob(object, job, mca);
			}
		}
		return job;
	}

	protected abstract void buildWriterJob(IAlbianObject object,
			IWriterJob writerJob, IPersistenceUpdateCommand update) throws AlbianDataServiceException;
	
	protected abstract void buildWriterJob(IAlbianObject object,
			IWriterJob writerJob, IPersistenceUpdateCommand update,String[] members) throws AlbianDataServiceException;

	protected abstract Map<String, Object> buildSqlParameter(String jobId,
			IAlbianObject object, IAlbianObjectAttribute albianObject,
			PropertyDescriptor[] propertyDesc) throws AlbianDataServiceException;

	protected abstract List<IDataRouterAttribute> parserRoutings(String jobId,
			IAlbianObject object, IDataRoutersAttribute routings,
			IAlbianObjectAttribute albianObject);

	protected abstract String parserRoutingStorage(String jobId,IAlbianObject obj,
			IDataRouterAttribute routing, IAlbianObjectDataRouter hashMapping,
			IAlbianObjectAttribute albianObject) throws AlbianDataServiceException;
}
