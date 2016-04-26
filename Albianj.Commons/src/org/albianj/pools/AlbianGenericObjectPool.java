package org.albianj.pools;

import java.util.Stack;

public class AlbianGenericObjectPool<T extends IAlbianPoolingObject> {
	
	private Stack<IAlbianPoolingObject> stack = null;
	private AlbianGenericObjectPoolConfig config = null;
	private IAlbianGenericObjectFactory factory = null;
	private Object arg = null;
	private long batchNumber = 0;
	private long currentSize = 0;
	
	public AlbianGenericObjectPool(AlbianGenericObjectPoolConfig config,
			IAlbianGenericObjectFactory factory,Object arg){
		if(null == config || null == factory){
			throw new IllegalArgumentException("argment is null.");
		}
		this.stack = new Stack<IAlbianPoolingObject>();
		this.factory = factory;
		this.arg = arg;
		this.config = config;
		this.batchNumber = System.currentTimeMillis();
	}
	
	public AlbianGenericObjectPool(IAlbianGenericObjectFactory factory,Object arg){
		this(new AlbianGenericObjectPoolConfig(),factory,arg);
	}
	
	
	public synchronized IAlbianPoolingObject  borrowObject() throws AlbianObjectPoolsException{
		IAlbianPoolingObject obj = null;
		if(!stack.empty()){
			obj = stack.pop();
		} else {
			if(currentSize < config.maxActive){
				 obj = factory.create(arg);
				obj.setBatchNumber(batchNumber);
				currentSize++;
			}
			if(AlbianObjectPoolsWhenExhaustedActionStyle.ThrowException == config.whenExhaustedAction){
					throw new AlbianObjectPoolsException("no free object in the pools.");
				
			} else {
				 obj = factory.create(arg); // not push to pool
			}
		}
		if(null != obj){
			factory.borrowAction(obj);
		}
		return obj;
		
	}
	
	public synchronized void returnObject(IAlbianPoolingObject obj){
		factory.returnAction(obj);
		if(obj.getBatchNumber() == batchNumber){
			stack.push(obj);
		}
		return;
	}
	
	public synchronized void destory(){
		stack.clear();
		batchNumber = -1;
		stack = null;
	}
}
