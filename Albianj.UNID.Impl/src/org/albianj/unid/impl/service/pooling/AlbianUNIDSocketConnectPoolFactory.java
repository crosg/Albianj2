package org.albianj.unid.impl.service.pooling;

import java.net.InetSocketAddress;
import java.net.Socket;

import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.impl.AlbianLoggerService;
import org.albianj.service.AlbianServiceException;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.unid.service.IAlbianRemoteUNIDAttribute;
import org.apache.commons.pool.PoolableObjectFactory;

public class AlbianUNIDSocketConnectPoolFactory implements
		PoolableObjectFactory<Socket> {

	private IAlbianRemoteUNIDAttribute attr = null;

	public AlbianUNIDSocketConnectPoolFactory(IAlbianRemoteUNIDAttribute attr) {
		this.attr = attr;

	}

	public AlbianUNIDSocketConnectPoolFactory() {
	}

	@Override
	public Socket makeObject() throws Exception {
		// TODO Auto-generated method stub
		try {
			Socket client = null;
			client = new Socket(attr.getHost(), attr.getPort());
			client.setSoTimeout(attr.getTimeout());
			return client;
		} catch (Exception e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, AlbianServiceException.class,e,
					"IdSrvice is error.","create remote id client to server:%s:%d is error",
					attr.getHost(), attr.getPort());
		}
		return null;
	}

	@Override
	public void destroyObject(Socket obj) throws Exception {
		try {
			if (null == obj)
				return;
			if (null != obj.getInputStream())
				obj.getInputStream().close();
			if (null != obj.getOutputStream())
				obj.getOutputStream().close();
			if (obj.isConnected() || !obj.isClosed())
				obj.close();
		} catch (Exception e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, AlbianServiceException.class,e,
					"IdService is error.","destory remote id client to server:%s:%d is error",
					attr.getHost(), attr.getPort());

		}
		return;
	}

	@Override
	public boolean validateObject(Socket obj) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void activateObject(Socket obj) throws Exception {
		// TODO Auto-generated method stub
		try {
			if (obj.isClosed() || !obj.isConnected())
				obj.connect(
						new InetSocketAddress(attr.getHost(), attr.getPort()),
						attr.getTimeout());
		} catch (Exception e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, AlbianServiceException.class,e,
					"IdService is error.","activate remote id client to server:%s:%d is error",
					attr.getHost(), attr.getPort());
		}
	}

	@Override
	public void passivateObject(Socket obj) throws Exception {
		// TODO Auto-generated method stub
		try {
			if (null == obj)
				return;
			if (null != obj.getInputStream())
				obj.getInputStream().close();
			if (null != obj.getOutputStream())
				obj.getOutputStream().close();
			if (obj.isConnected() || !obj.isClosed())
				obj.close();
		} catch (Exception e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, AlbianServiceException.class,e,
					"IdService is error.","passivate remote id client to server:%s:%d is error",
					attr.getHost(), attr.getPort());		}
		return;
	}

}
