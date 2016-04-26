package org.albianj.persistence.impl.db;

import java.sql.Statement;
import java.util.List;

import org.albianj.persistence.context.IReaderJob;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.PersistenceCommandType;
import org.albianj.persistence.object.IAlbianObject;

public interface IPersistenceQueryScope {
	public <T extends IAlbianObject> List<T> execute(Class<T> cls,
			IReaderJob job) throws AlbianDataServiceException;

	public <T extends IAlbianObject> List<T> execute(Class<T> cls,
			PersistenceCommandType cmdType, Statement statement) throws AlbianDataServiceException;
	
	public Object execute(
			IReaderJob job) throws AlbianDataServiceException ;
}
