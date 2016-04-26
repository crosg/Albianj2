package org.albianj.remote.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.albianj.net.IMsgHeader;
import org.albianj.net.MemoryToIOStream;



public class RemoteTest {

	private static BigInteger createID(String host,int port, int type) {
		SocketChannel  client = null;
		boolean isPooling = false;
				client = SocketChannel.open();
				client.connect(null);

	
		try {
			
			IMsgHeader outHeader = null;
//			outHeader = new MsgHeader(AlbianRemoteUNIDProtocol.Version,
//					AlbianRemoteUNIDProtocol.MakeId, 4, 0, false, 0);

			ByteBuffer bb = ByteBuffer.allocate(100);
			
			byte[] outHeaderBuffer = outHeader.pack();
			byte[] outBodyBuffer = MemoryToIOStream.intToNetStream(type);
			bb.put(outHeaderBuffer);
			bb.put(outBodyBuffer);
			bb.flip();

			while(bb.hasRemaining()) {
			    client.write(bb);
			}

			byte[] inHeaderBuffer = new byte[IMsgHeader.MsgHeaderLength];
			client.re
			is.read(inHeaderBuffer, 0, IMsgHeader.MsgHeaderLength);
			IMsgHeader inHeader = new MsgHeader().unpack(inHeaderBuffer);
			long inBodylen = inHeader.getBodyLength();
			byte[] inBodyBuffer = new byte[(int) inBodylen];
			is.read(inBodyBuffer, 0, (int) inBodylen);
			BigInteger unid = new BigInteger(inBodyBuffer);
			return unid;

		} catch (Exception e) {
			AlbianLoggerService.error(IAlbianLoggerService.AlbianRunningLoggerName,e, "get remote:%s:%d UNID is fail.",
					attr.getHost(),attr.getPort());
		} finally {
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					AlbianLoggerService.error(IAlbianLoggerService.AlbianRunningLoggerName,
							e, "close remote:%s:%d output stream is fail.",
							attr.getHost(),attr.getPort());
				}
			}
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					AlbianLoggerService.error(IAlbianLoggerService.AlbianRunningLoggerName,e, "close remote:%s:%d input stream is fail.",
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
				AlbianLoggerService.error(IAlbianLoggerService.AlbianRunningLoggerName,e, "close remote:%s:%d output stream is fail.",
						attr.getHost(),attr.getPort());
			}
		}
		}
		return null;
	}

	
}
