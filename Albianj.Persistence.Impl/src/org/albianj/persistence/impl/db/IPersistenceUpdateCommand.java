package org.albianj.persistence.impl.db;

import java.util.Map;

import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.persistence.object.IDataRoutersAttribute;
import org.albianj.persistence.object.IStorageAttribute;

public interface IPersistenceUpdateCommand {
	public IPersistenceCommand builder(String sessionId,IAlbianObject object, IDataRoutersAttribute routings,
			IAlbianObjectAttribute albianObject, Map<String, Object> mapValue,
			IDataRouterAttribute routing, IStorageAttribute storage) throws AlbianDataServiceException;
	
	public IPersistenceCommand builder(String sessionId,IAlbianObject object, IDataRoutersAttribute routings, IAlbianObjectAttribute albianObject,
			Map<String, Object> mapValue, IDataRouterAttribute routing, IStorageAttribute storage, String[] members)
					 throws NoSuchMethodException, AlbianDataServiceException;
}
