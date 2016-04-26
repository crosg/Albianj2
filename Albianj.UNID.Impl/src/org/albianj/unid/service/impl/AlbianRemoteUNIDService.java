package org.albianj.unid.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.albianj.argument.RefArg;
import org.albianj.datetime.AlbianDateTime;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.impl.AlbianLoggerService;
import org.albianj.net.IMsgHeader;
import org.albianj.net.MemoryToIOStream;
import org.albianj.net.MsgHeader;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.unid.impl.service.pooling.AlbianUNIDConnectPoolMap;
import org.albianj.unid.impl.service.pooling.AlbianUNIDSocketConnectPoolFactory;
import org.albianj.unid.service.AlbianRemoteUNIDAttributeException;
import org.albianj.unid.service.AlbianRemoteUNIDProtocol;
import org.albianj.unid.service.IAlbianRemoteUNIDAttribute;
import org.albianj.unid.service.IAlbianRemoteUNIDService;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.dom4j.Element;

/**
 * @author Seapeak
 *
 */
public class AlbianRemoteUNIDService extends FreeAlbianRemoteUNIDParser
		implements IAlbianRemoteUNIDService {
	private List<IAlbianRemoteUNIDAttribute> _list = null;
	private Object locker = new Object();
	private static int _idx = 0;

	@Override
	public void init() throws AlbianParserException {
		_list = new ArrayList<IAlbianRemoteUNIDAttribute>();
		super.init();
	}

	protected void parserServers(@SuppressWarnings("rawtypes") List nodes)
			throws AlbianRemoteUNIDAttributeException {
		for (Object node : nodes) {
			IAlbianRemoteUNIDAttribute attr = parserServer((Element) node);
			if (null == attr)
				continue;
			_list.add(attr);
			GenericObjectPool.Config config = new GenericObjectPool.Config();
			config.maxActive = attr.getPoolSize();
			config.maxWait = 300;
			GenericObjectPool<Socket> pool = new GenericObjectPool<Socket>(
					new AlbianUNIDSocketConnectPoolFactory(attr),
					config) {
			};
			AlbianUNIDConnectPoolMap.insert(
					String.format("%s:%d", attr.getHost(), attr.getPort()),
					pool);
		}

	}

	protected IAlbianRemoteUNIDAttribute parserServer(Element node) {
		String host = XmlParser.getAttributeValue(node, "Host");
		IAlbianRemoteUNIDAttribute attr = new AlbianRemoteUNIDAttribute();
		if (Validate.isNullOrEmptyOrAllSpace(host)) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,
					"remote server host in the unid.xml is null or empty.xml:%s.", node.asXML());
			return null;
		}
		attr.setHost(host);

		String sPort = XmlParser.getAttributeValue(node, "Port");
		if (Validate.isNullOrEmptyOrAllSpace(sPort)) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,
					"remote server port in the unid.xml is null or empty.xml:%s.", node.asXML());
			return null;		
			}
		attr.setPort(Integer.parseInt(sPort));

		String stto = XmlParser.getAttributeValue(node, "Timeout");
		if (!Validate.isNullOrEmptyOrAllSpace(stto)) {
			attr.setTimeout(Integer.parseInt(stto));
		}

		String ssize = XmlParser.getAttributeValue(node, "PoolSize");
		if (!Validate.isNullOrEmptyOrAllSpace(ssize)) {
			attr.setPoolSize(Integer.parseInt(ssize));
		}
		return attr;
	}

	public BigInteger createBookId() {
		int idx = 0;
		synchronized (locker) {
			idx = (_idx++) % _list.size();
			if (idx > _list.size()) {
				idx = 0;
				_idx = 0;
			}
		}
		IAlbianRemoteUNIDAttribute attr = _list.get(idx);
		return createID(attr, 0);
	}
	
	public BigInteger createAuthorId() {
		int idx = 0;
		synchronized (locker) {
			idx = (_idx++) % _list.size();
			if (idx > _list.size()) {
				idx = 0;
				_idx = 0;
			}
		}
		IAlbianRemoteUNIDAttribute attr = _list.get(idx);
		return createID(attr, 1);
	}
	
	public BigInteger createConfigItemId(){
		int idx = 0;
		synchronized (locker) {
			idx = (_idx++) % _list.size();
			if (idx > _list.size()) {
				idx = 0;
				_idx = 0;
			}
		}
		IAlbianRemoteUNIDAttribute attr = _list.get(idx);
		return createID(attr, 2);
	}

	int typeDefault = 3;
	public BigInteger createUNID() {
		int type = 0x3ff < typeDefault++ ? typeDefault = 3 : typeDefault;
		return createUNID(type);
	}

	public BigInteger createUNID(int type) {
		int idx = 0;
		synchronized (locker) {
			idx = (_idx++) % _list.size();
			if (idx > _list.size()) {
				idx = 0;
				_idx = 0;
			}
		}
		IAlbianRemoteUNIDAttribute attr = _list.get(idx);
		return createID(attr, type);
	}

	@SuppressWarnings("unchecked")
	private static BigInteger createID(IAlbianRemoteUNIDAttribute attr, int type) {
		Socket client = null;
		GenericObjectPool<Socket> pool = null;
		boolean isPooling = false;
		Object obj = AlbianUNIDConnectPoolMap.get(String.format("%s:%d",
				attr.getHost(), attr.getPort()));
		try {
			if (null == obj) {
				isPooling = false;
				client = new Socket(attr.getHost(), attr.getPort());
				client.setSoTimeout(attr.getTimeout());
			} else {
				isPooling = true;
				pool = (GenericObjectPool<Socket>) obj;
				client = pool.borrowObject();
			}
		} catch (Exception e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,
					e, "create UNID client connect:%s:%d is fail.",
					attr.getHost(),attr.getPort());
			return null;
		}
		
		if(null == client){
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianRunningLoggerName,
					 "get UNID client connect:%s:%d from pool is fail.create it.",
					attr.getHost(),attr.getPort());
			try {
					isPooling = false;
					client = new Socket(attr.getHost(), attr.getPort());
					client.setSoTimeout(attr.getTimeout());
			
			} catch (Exception e) {
				AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,
						e, "create UNID client connect:%s:%d is fail.",
						attr.getHost(),attr.getPort());
				return null;
			}			
		}

		OutputStream os = null;
		InputStream is = null;
		try {
//			client = new Socket(attr.getHost(), attr.getPort());
//			client.setSoTimeout(attr.getTimeout());
			os = client.getOutputStream();
			IMsgHeader outHeader = null;
			outHeader = new MsgHeader(AlbianRemoteUNIDProtocol.Version,
					AlbianRemoteUNIDProtocol.MakeId, 4, 0, false, 0);

			byte[] outHeaderBuffer = outHeader.pack();
			byte[] outBodyBuffer = MemoryToIOStream.intToNetStream(type);
			os.write(outHeaderBuffer);
			os.write(outBodyBuffer);
			os.flush();

			is = client.getInputStream();
			byte[] inHeaderBuffer = new byte[IMsgHeader.MsgHeaderLength];
			is.read(inHeaderBuffer, 0, IMsgHeader.MsgHeaderLength);
			IMsgHeader inHeader = new MsgHeader().unpack(inHeaderBuffer);
			long inBodylen = inHeader.getBodyLength();
			byte[] inBodyBuffer = new byte[(int) inBodylen];
			is.read(inBodyBuffer, 0, (int) inBodylen);
			BigInteger unid = new BigInteger(inBodyBuffer);
			return unid;

		} catch (Exception e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,e, "get remote:%s:%d UNID is fail.",
					attr.getHost(),attr.getPort());
		} finally {
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,
							e, "close remote:%s:%d output stream is fail.",
							attr.getHost(),attr.getPort());
				}
			}
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,e, "close remote:%s:%d input stream is fail.",
							attr.getHost(),attr.getPort());
				}
			}
		
		if (null != client) {
			try {
				if (isPooling) {
					pool.returnObject(client);
				} else {
					client.close();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,e, "close remote:%s:%d output stream is fail.",
						attr.getHost(),attr.getPort());
			}
		}
		}
		return null;
	}

	public void unpack(BigInteger bi, RefArg<Timestamp> time,
			RefArg<Integer> type) {
		if (null == time && null == type)
			return;
		long r32 = bi.shiftRight(32).longValue();
		long l32 = bi.intValue();
		if (null != type) {
			type.setValue((int) (l32 & 0x3ff));
		}
		if (null != time) {
			Date dt = AlbianDateTime.dateAddSeconds(2015, 1, 1, r32);
			@SuppressWarnings("deprecation")
			Timestamp tt = new Timestamp(dt.getYear(), dt.getMonth(),
					dt.getDate(), dt.getHours(), dt.getMinutes(),
					dt.getSeconds(), 0);
			time.setValue(tt);
		}

	}

	public void unpack(BigInteger bi, RefArg<Timestamp> time,
			RefArg<Integer> sed, RefArg<Integer> idx) {
		long r32 = bi.divide(new BigInteger("1000000000")).longValue();
		int next = bi.divide(new BigInteger("100")).intValue();
		int i = bi.modInverse(new BigInteger("100")).intValue();
		if (null != time) {
			Date dt = AlbianDateTime.dateAddSeconds(2015, 1, 1, r32);
			@SuppressWarnings("deprecation")
			Timestamp tt = new Timestamp(dt.getYear(), dt.getMonth(),
					dt.getDate(), dt.getHours(), dt.getMinutes(),
					dt.getSeconds(), 0);
			time.setValue(tt);
		}
		if (null != sed)
			sed.setValue(next);
		if (null != idx)
			idx.setValue(i);
	}
	
	/**
	 * 生成一个使用二进制算法的组合的id，改id对于人不是太友好，不能被很好的辨认
	 * 但是对算法友好，计算较快
	 * @return 二进制算法生成的一个十进制数，uint64类型
	 */
	public BigInteger createBinaryId() {
		int idx = 0;
		synchronized (locker) {
			idx = (_idx++) % _list.size();
			if (idx > _list.size()) {
				idx = 0;
				_idx = 0;
			}
		}
		IAlbianRemoteUNIDAttribute attr = _list.get(idx);
		return createID(attr, 3);
	}
	
	/**
	 * 生成一个十进制、完整的id。
	 * 这个id最后的4位将会从0-9999依次出现，这种id适合根据最后的4位做hash或者是轮询分库分表
	 * @return 十进制生成的id
	 */
	public BigInteger createCompleteDigital(){
		int idx = 0;
		synchronized (locker) {
			idx = (_idx++) % _list.size();
			if (idx > _list.size()) {
				idx = 0;
				_idx = 0;
			}
		}
		IAlbianRemoteUNIDAttribute attr = _list.get(idx);
		return createID(attr, 99);
	}
	/**
	 * 生成一个十进制，不完整的id
	 * 这个id的最后两位是00，永远是00。这种id比较适合根据自己的规则来指定分库分表，
	 * 如果要用这个id来做取模或者是轮询，必须排除最后的2位，排除最后的2位后，和createCompleteDigital生成的id一致
	 * @return
	 */
	public BigInteger createIncompleteDigital(){
		int idx = 0;
		synchronized (locker) {
			idx = (_idx++) % _list.size();
			if (idx > _list.size()) {
				idx = 0;
				_idx = 0;
			}
		}
		IAlbianRemoteUNIDAttribute attr = _list.get(idx);
		return createID(attr, 0);
	}
	
	/**
	 * 生成一个十进制，保证递增并且完整的十进制id
	 *  这个id最后的4位将会从0-9999依次出现，但是如果新的1秒开始，这个计数将会从0重新开始。
	 *  注意，这个id不是太适合取模或者是hash等分库分表，因为后面的四位数生成的不充分，可能会引起数据存储的数据量不平衡
	 * @return
	 */
	public BigInteger createIncrAndCompleteDigital(){
		int idx = 0;
		synchronized (locker) {
			idx = (_idx++) % _list.size();
			if (idx > _list.size()) {
				idx = 0;
				_idx = 0;
			}
		}
		IAlbianRemoteUNIDAttribute attr = _list.get(idx);
		return createID(attr, 98);
	}

	
}
