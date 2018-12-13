package org.albianj.pooling;

public interface IPoolingObjectFactory {
   <T extends AutoCloseable> IPoolingObject<T> newPoolingObject(boolean isPooling,IPoolingObjectConfig poolingObjectConfig) ;
}
