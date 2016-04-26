package org.albianj.persistence.impl.context;

import java.util.LinkedList;

import org.albianj.persistence.context.IReaderJob;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.object.IFilterCondition;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.object.filter.IChainExpression;

public interface IReaderJobAdapter {
	
	@Deprecated
	public IReaderJob buildReaderJob(String sessionId,Class<?> cls, boolean isExact,String routingName,
			int start, int step, LinkedList<IFilterCondition> wheres,
			LinkedList<IOrderByCondition> orderbys) throws AlbianDataServiceException;
	
	@Deprecated
	public IReaderJob buildReaderJob(String sessionId,Class<?> cls,boolean isExact,String routingName,
			 LinkedList<IFilterCondition> wheres,LinkedList<IOrderByCondition> orderbys) throws AlbianDataServiceException;
	
	
	public IReaderJob buildReaderJob(String sessionId,Class<?> cls,boolean isExact, String routingName,
			int start, int step, IChainExpression f,
			LinkedList<IOrderByCondition> orderbys) throws AlbianDataServiceException;
	
	public IReaderJob buildReaderJob(String sessionId,Class<?> cls,boolean isExact,String routingName,
			IChainExpression f,LinkedList<IOrderByCondition> orderbys) throws AlbianDataServiceException;
}
