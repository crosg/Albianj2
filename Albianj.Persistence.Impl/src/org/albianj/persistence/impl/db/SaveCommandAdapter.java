package org.albianj.persistence.impl.db;

import java.util.Map;

import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.persistence.object.IDataRoutersAttribute;
import org.albianj.persistence.object.IStorageAttribute;

public class SaveCommandAdapter implements IPersistenceUpdateCommand {

	private IPersistenceUpdateCommand create;
	private IPersistenceUpdateCommand modify;

	public SaveCommandAdapter(IPersistenceUpdateCommand create, IPersistenceUpdateCommand modify) {
		this.create = create;
		this.modify = modify;
	}

	public SaveCommandAdapter() {
		if (null == this.create)
			this.create = new CreateCommandAdapter();
		if (null == this.modify)
			this.modify = new ModifyCommandAdapter();
	}

	public IPersistenceCommand builder(IAlbianObject object, IDataRoutersAttribute routings,
			IAlbianObjectAttribute albianObject, Map<String, Object> mapValue,
			IDataRouterAttribute routing, IStorageAttribute storage) {
		if (object.getIsAlbianNew()) {
			return create.builder(object, routings, albianObject, mapValue,
					routing, storage);
		} else {
			return modify.builder(object, routings, albianObject, mapValue,
					routing, storage);
		}
	}
	
	public IPersistenceCommand builder(IAlbianObject object, IDataRoutersAttribute routings, IAlbianObjectAttribute albianObject,
			Map<String, Object> mapValue, IDataRouterAttribute routing, IStorageAttribute storage, String[] members) throws NoSuchMethodException{
		throw new NoSuchMethodException();
	}


}
