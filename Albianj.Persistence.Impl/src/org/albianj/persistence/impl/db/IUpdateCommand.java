package org.albianj.persistence.impl.db;

import java.util.Map;

import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.persistence.object.IDataRoutersAttribute;
import org.albianj.persistence.object.IStorageAttribute;

public interface IUpdateCommand {
	public ICommand builder(IAlbianObject object, IDataRoutersAttribute routings,
			IAlbianObjectAttribute albianObject, Map<String, Object> mapValue,
			IDataRouterAttribute routing, IStorageAttribute storage);
	
	public ICommand builder(IAlbianObject object, IDataRoutersAttribute routings, IAlbianObjectAttribute albianObject,
			Map<String, Object> mapValue, IDataRouterAttribute routing, IStorageAttribute storage, String[] members)
					 throws NoSuchMethodException;
}
