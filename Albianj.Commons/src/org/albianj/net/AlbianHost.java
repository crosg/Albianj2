package org.albianj.net;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class AlbianHost {

	@Deprecated
	public static InetAddress getInetAddress() throws UnknownHostException {
		return InetAddress.getLocalHost();
	}

	@Deprecated
	public static String getHostIp(InetAddress netAddress) {
		if (null == netAddress) {
			return null;
		}
		String ip = netAddress.getHostAddress(); // get the ip address
		return ip;
	}

	public static String getHostName(InetAddress netAddress) {
		if (null == netAddress) {
			return null;
		}
		String name = netAddress.getHostName(); // get the host address
		return name;
	}

	@Deprecated
	public static String getLocalIp() throws UnknownHostException {
		InetAddress netAddress = getInetAddress();
		if (null == netAddress) {
			return null;
		}
		String ip = netAddress.getHostAddress(); // get the ip address
		return ip;
	}

	@Deprecated
	public static String getLocalName() throws UnknownHostException {
		InetAddress netAddress = getInetAddress();
		if (null == netAddress) {
			return null;
		}
		String name = netAddress.getHostName(); // get the host address
		return name;
	}

	public static long ipToLong(String sIp) {
		long[] ip = new long[4];
		int position1 = sIp.indexOf(".");
		int position2 = sIp.indexOf(".", position1 + 1);
		int position3 = sIp.indexOf(".", position2 + 1);
		ip[0] = Long.parseLong(sIp.substring(0, position1));
		ip[1] = Long.parseLong(sIp.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(sIp.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(sIp.substring(position3 + 1));
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	public static String longToIp(int n) {
		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf((n >>> 24)));
		sb.append(".");
		sb.append(String.valueOf((n & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((n & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf((n & 0x000000FF)));
		return sb.toString();
	}

	/* 一个将字节转化为十六进制ASSIC码的函数 */
	public static String byteHEX(byte ib) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		char[] ob = new char[2];
		ob[0] = Digit[(ib >>> 4) & 0X0F];
		ob[1] = Digit[ib & 0X0F];
		String s = new String(ob);
		return s;
	}

	public static  String getMacAddr() {
		String MacAddr = "";
		String str = "";
		try {
			NetworkInterface NIC = NetworkInterface.getByName("eth0");
			byte[] buf = NIC.getHardwareAddress();
			for (int i = 0; i < buf.length; i++) {
				str = str + byteHEX(buf[i]);
			}
			MacAddr = str.toUpperCase();
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return MacAddr;
	}

	public static String getLocalIP() {
		String ip = "";
		try {
			Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				if (!ni.getName().equals("eth0")) {
					continue;
				} else {
					Enumeration<?> e2 = ni.getInetAddresses();
					while (e2.hasMoreElements()) {
						InetAddress ia = (InetAddress) e2.nextElement();
						if (ia instanceof Inet6Address)
							continue;
						ip = ia.getHostAddress();
					}
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return ip;
	}

}
