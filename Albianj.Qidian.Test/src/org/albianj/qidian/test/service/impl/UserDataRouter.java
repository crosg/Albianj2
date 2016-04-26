package org.albianj.qidian.test.service.impl;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.albianj.argument.RefArg;
import org.albianj.persistence.object.FreeAlbianObjectDataRouter;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.persistence.object.IFilterCondition;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.qidian.test.common.IdGenerator;
import org.albianj.qidian.test.object.IUser;

public class UserDataRouter extends FreeAlbianObjectDataRouter {

	static String WR = "UserDataRouterW";
	static String RR = "UserDataRouterR";
	
	@Override
	public List<IDataRouterAttribute> mappingWriterRouting(Map<String, IDataRouterAttribute> routings,
			IAlbianObject obj) {
		// TODO Auto-generated method stub
		IDataRouterAttribute dra = routings.get(WR);
		List<IDataRouterAttribute> drs = new LinkedList<>();
		drs.add(dra);
		return drs;
	}

	

	@Override
	public String mappingWriterRoutingStorage(IDataRouterAttribute routing, IAlbianObject obj) {
		// TODO Auto-generated method stub
		return routing.getStorageName();
	}

	@Override
	public String mappingWriterRoutingDatabase(IStorageAttribute storage, IAlbianObject obj) {
		// TODO Auto-generated method stub
		IUser u = (IUser) obj;
		RefArg<Long> ymd = new RefArg<>();
		IdGenerator.parser(u.getId(), ymd, null);
		return storage.getDatabase() + (ymd.getValue() / 10000);
	}

	@Override
	public String mappingWriterTable(IDataRouterAttribute routing, IAlbianObject obj) {
		// TODO Auto-generated method stub
		IUser u = (IUser) obj;
		RefArg<Long> ymd = new RefArg<>();
		RefArg<Long> idx = new RefArg<>();
		IdGenerator.parser(u.getId(), ymd, idx);
		return routing.getTableName() + (ymd.getValue() / 10000) + "_" + idx.getValue() % 2;
	}

	@Override
	public IDataRouterAttribute mappingReaderRouting(Map<String, IDataRouterAttribute> routings,
			Map<String, IFilterCondition> wheres, Map<String, IOrderByCondition> orderbys) {
		// TODO Auto-generated method stub
		IDataRouterAttribute dra = routings.get(RR);
		return dra;
	}
	
	@Override
	public String mappingReaderRoutingStorage(IDataRouterAttribute routing, Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		// TODO Auto-generated method stub
		return routing.getStorageName();
	}

	@Override
	public String mappingReaderRoutingDatabase(IStorageAttribute storage, Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		// TODO Auto-generated method stub
		if(wheres.containsKey("Id")){
			IFilterCondition fc = wheres.get("Id");
			RefArg<Long> ymd = new RefArg<>();
			IdGenerator.parser((BigInteger)fc.getValue(), ymd, null);
			return storage.getDatabase() + ymd.getValue() / 10000;
		}
		return null;
	}

	@Override
	public String mappingReaderTable(IDataRouterAttribute routing, Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		// TODO Auto-generated method stub
		if(wheres.containsKey("Id")){
			IFilterCondition fc = wheres.get("Id");
			RefArg<Long> ymd = new RefArg<>();
			RefArg<Long> idx = new RefArg<>();
			IdGenerator.parser((BigInteger)fc.getValue(), ymd, idx);
			return routing.getTableName() +  + ymd.getValue() / 10000 + "_" + idx.getValue() % 2;
		}
		return null;
	}

	@Override
	public IDataRouterAttribute mappingExactReaderRouting(Map<String, IDataRouterAttribute> routings,
			Map<String, IFilterCondition> wheres, Map<String, IOrderByCondition> orderbys) {
		// TODO Auto-generated method stub
		IDataRouterAttribute dra = routings.get(WR);
		return dra;
	}

	@Override
	public String mappingExactReaderRoutingStorage(IDataRouterAttribute routing, Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		return routing.getStorageName();
	}

	@Override
	public String mappingExactReaderRoutingDatabase(IStorageAttribute storage, Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		// TODO Auto-generated method stub
		if(wheres.containsKey("Id")){
			IFilterCondition fc = wheres.get("Id");
			RefArg<Long> ymd = new RefArg<>();
			IdGenerator.parser((BigInteger)fc.getValue(), ymd, null);
			return storage.getDatabase()  + ymd.getValue() / 10000;
		}
		return null;
	}

	@Override
	public String mappingExactReaderTable(IDataRouterAttribute routing, Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		// TODO Auto-generated method stub
		if(wheres.containsKey("Id")){
			IFilterCondition fc = wheres.get("Id");
			RefArg<Long> ymd = new RefArg<>();
			RefArg<Long> idx = new RefArg<>();
			IdGenerator.parser((BigInteger)fc.getValue(), ymd, idx);
			return routing.getTableName() +  + ymd.getValue() / 10000 + "_" + idx.getValue() % 2;
		}
		return null;
	}

}
