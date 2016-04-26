package org.albianj.persistence.object;

import java.util.List;
import java.util.Map;

public abstract class FreeAlbianObjectDataRouter implements
		IAlbianObjectDataRouter {

	@Override
	public List<IDataRouterAttribute> mappingWriterRouting(
			Map<String, IDataRouterAttribute> routings, IAlbianObject obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataRouterAttribute mappingReaderRouting(
			Map<String, IDataRouterAttribute> routings,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		return null;
	}

	@Override
	public String mappingWriterRoutingStorage(IDataRouterAttribute routing,
			IAlbianObject obj) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String mappingWriterRoutingDatabase(IStorageAttribute storage,
			IAlbianObject obj) {
		return null;
	}

	@Override
	public String mappingWriterTable(IDataRouterAttribute routing,
			IAlbianObject obj) {
		// TODO Auto-generated method stub
		 return null;
	}

	@Override
	public String mappingReaderRoutingStorage(IDataRouterAttribute routing,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String mappingReaderRoutingDatabase(IStorageAttribute storage,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String mappingReaderTable(IDataRouterAttribute routing,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public IDataRouterAttribute mappingExactReaderRouting(
			Map<String, IDataRouterAttribute> routings,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		return mappingReaderRouting(routings, wheres, orderbys);
	}

	/**
	 * 
	 * @param routing
	 * @param wheres
	 * @param orderbys
	 * @return
	 */
	public String mappingExactReaderRoutingStorage(IDataRouterAttribute routing,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		return mappingReaderRoutingStorage(routing, wheres, orderbys);
	}
	
	public String mappingExactReaderRoutingDatabase(IStorageAttribute storage,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys) {
		return mappingReaderRoutingDatabase(storage, wheres, orderbys);
	}

	/**
	 * 
	 * @param routing
	 * @param wheres
	 * @param orderbys
	 * @return
	 */
	public String mappingExactReaderTable(IDataRouterAttribute routing,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys){
		return mappingReaderTable(routing, wheres, orderbys);
	}

}
