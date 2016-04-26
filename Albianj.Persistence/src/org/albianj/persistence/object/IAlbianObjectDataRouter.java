package org.albianj.persistence.object;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Seapeak
 *
 */
public interface IAlbianObjectDataRouter {

	/**
	 * 
	 * @param routings
	 * @param obj
	 * @return
	 */
	public List<IDataRouterAttribute> mappingWriterRouting(
			Map<String, IDataRouterAttribute> routings, IAlbianObject obj);

	/**
	 * 
	 * @param routing
	 * @param obj
	 * @return
	 */
	public String mappingWriterRoutingStorage(IDataRouterAttribute routing,
			IAlbianObject obj);
	
	public String mappingWriterRoutingDatabase(IStorageAttribute storage,
			IAlbianObject obj);

	/**
	 * 
	 * @param routing
	 * @param obj
	 * @return
	 */
	public String mappingWriterTable(IDataRouterAttribute routing,
			IAlbianObject obj);

	/**
	 * 
	 * @param routings
	 * @param wheres
	 * @param orderbys
	 * @return
	 */
	public IDataRouterAttribute mappingReaderRouting(
			Map<String, IDataRouterAttribute> routings,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys);

	/**
	 * 
	 * @param routing
	 * @param wheres
	 * @param orderbys
	 * @return
	 */
	public String mappingReaderRoutingStorage(IDataRouterAttribute routing,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys);
	
	public String mappingReaderRoutingDatabase(IStorageAttribute storage,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys);


	/**
	 * 
	 * @param routing
	 * @param wheres
	 * @param orderbys
	 * @return
	 */
	public String mappingReaderTable(IDataRouterAttribute routing,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys);
	
	
	/**
	 * 
	 * @param routings
	 * @param wheres
	 * @param orderbys
	 * @return
	 */
	public IDataRouterAttribute mappingExactReaderRouting(
			Map<String, IDataRouterAttribute> routings,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys);

	/**
	 * 
	 * @param routing
	 * @param wheres
	 * @param orderbys
	 * @return
	 */
	public String mappingExactReaderRoutingStorage(IDataRouterAttribute routing,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys);
	
	public String mappingExactReaderRoutingDatabase(IStorageAttribute storage,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys);

	/**
	 * 
	 * @param routing
	 * @param wheres
	 * @param orderbys
	 * @return
	 */
	public String mappingExactReaderTable(IDataRouterAttribute routing,
			Map<String, IFilterCondition> wheres,
			Map<String, IOrderByCondition> orderbys);
}
