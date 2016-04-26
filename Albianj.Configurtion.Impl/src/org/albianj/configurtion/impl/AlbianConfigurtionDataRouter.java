package org.albianj.configurtion.impl;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.albianj.configurtion.IConfigItem;
import org.albianj.persistence.object.FreeAlbianObjectDataRouter;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IFilterCondition;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.verify.Validate;

public class AlbianConfigurtionDataRouter extends FreeAlbianObjectDataRouter {

	public static final String AlbianConfigWriterRouterDefault = "AlbianConfigWriterRouter";
	public static final String AlbianConfigReaderRouterDefault = "AlbianConfigReaderRouter";

	@Override
	public List<IDataRouterAttribute> mappingWriterRouting(
			Map<String, IDataRouterAttribute> routings, IAlbianObject obj) {
		// TODO Auto-generated method stub
		if (Validate.isNullOrEmpty(routings) || null == obj)
			return super.mappingWriterRouting(routings, obj);
		List<IDataRouterAttribute> list = new LinkedList<IDataRouterAttribute>();
		list.add(routings.get(AlbianConfigWriterRouterDefault));
		return list;
	}

	@Override
	public String mappingWriterRoutingStorage(IDataRouterAttribute routing,
			IAlbianObject obj) {
		// TODO Auto-generated method stub
		return super.mappingWriterRoutingStorage(routing, obj);
	}

	@Override
	public String mappingWriterTable(IDataRouterAttribute routing,
			IAlbianObject obj) {
		// TODO Auto-generated method stub
		if (null == routing || null == obj)
			return super.mappingWriterTable(routing, obj);
		IConfigItem ci = (IConfigItem) obj;
		int rem = ci.getId().remainder(new BigInteger("10")).intValue();
		return String.format("%s%02d", routing.getTableName(), rem);
	}

	@Override
	public IDataRouterAttribute mappingReaderRouting(
			Map<String, IDataRouterAttribute> routings,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		// TODO Auto-generated method stub
		if (Validate.isNullOrEmpty(routings))
			return super.mappingReaderRouting(routings, wheres, orderbys);
		return routings.get(AlbianConfigReaderRouterDefault);
	}

	@Override
	public String mappingReaderRoutingStorage(IDataRouterAttribute routing,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		// TODO Auto-generated method stub
		return super.mappingReaderRoutingStorage(routing, wheres, orderbys);
	}

	@Override
	public String mappingReaderTable(IDataRouterAttribute routing,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		// TODO Auto-generated method stub
		if (null == routing)
			return super.mappingReaderTable(routing, wheres, orderbys);
		if (wheres.containsKey("Level")) {// for name and for fule cache
			IFilterCondition fc = wheres.get("Level");
			if (!fc.isAddition())
				return super.mappingReaderTable(routing, wheres, orderbys);
			return String.format("%s%02d", routing.getTableName(), fc.getValue());
		}
		if (wheres.containsKey("Id")) {// for get by id
			IFilterCondition fc = wheres.get("Id");
			BigInteger bi = (BigInteger) fc.getValue();
			int rem = bi.remainder(new BigInteger("10")).intValue();
			return String.format("%s%02d", routing.getTableName(), rem);
		}
		if (wheres.containsKey("ParentId")) { // for get by parentid
			IFilterCondition fc = wheres.get("ParentId");
			BigInteger bi = (BigInteger) fc.getValue();
			int rem = bi.remainder(new BigInteger("10")).intValue();
			return String.format("%s%02d", routing.getTableName(), rem + 1);
		}

		return super.mappingReaderTable(routing, wheres, orderbys);
	}

}
