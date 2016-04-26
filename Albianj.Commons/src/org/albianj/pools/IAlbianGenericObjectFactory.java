package org.albianj.pools;

public interface   IAlbianGenericObjectFactory{
	public IAlbianPoolingObject create(Object arg);
	public void borrowAction(IAlbianPoolingObject obj);
	public void returnAction(IAlbianPoolingObject obj);
}
