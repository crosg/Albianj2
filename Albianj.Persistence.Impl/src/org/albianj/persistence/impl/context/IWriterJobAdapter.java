package org.albianj.persistence.impl.context;

import java.util.List;

import org.albianj.persistence.context.IWriterJob;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.object.IAlbianObject;

public interface IWriterJobAdapter {
	public IWriterJob buildCreation(String sessionId,IAlbianObject object)
			throws AlbianDataServiceException;

	public IWriterJob buildCreation(String sessionId,List<? extends IAlbianObject> objects)
			throws AlbianDataServiceException;

	public IWriterJob buildModification(String sessionId,IAlbianObject object)
			throws AlbianDataServiceException;
	
	public IWriterJob buildModification(String sessionId,IAlbianObject object,String[] members)
			throws AlbianDataServiceException;

	public IWriterJob buildModification(String sessionId,List<? extends IAlbianObject> objects)
			throws AlbianDataServiceException;

	public IWriterJob buildRemoved(String sessionId,IAlbianObject object)
			throws AlbianDataServiceException;

	public IWriterJob buildRemoved(String sessionId,List<? extends IAlbianObject> objects)
			throws AlbianDataServiceException;

	public IWriterJob buildSaving(String sessionId,IAlbianObject object)
			throws AlbianDataServiceException;

	public IWriterJob buildSaving(String sessionId,List<? extends IAlbianObject> objects)
			throws AlbianDataServiceException;

}
