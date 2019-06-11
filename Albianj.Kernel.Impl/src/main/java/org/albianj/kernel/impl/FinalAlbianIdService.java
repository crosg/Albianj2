package org.albianj.kernel.impl;

import org.albianj.kernel.IAlbianIdService;
import org.albianj.verify.Validate;

import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class FinalAlbianIdService implements IAlbianIdService {

    public static  IAlbianIdService Instance;

    static {
        Instance = new FinalAlbianIdService();
    }

    private static volatile long idSeed = 0;
    private static long ipSuff = 0;
    private static long tsSeek = 0;
    private Object locker = new Object();

    @Override
    public BigInteger genId(){
        return genId("eth1");
    }

    @Override
    public BigInteger genId(String networkName) {
        if (ipSuff == 0) {
            synchronized (this) {
                if (ipSuff == 0) {
                    String ip = getLocalIP(networkName);
                    if (Validate.isNullOrEmptyOrAllSpace(ip) || "0.0.0.0".equals(ip) || "127.0.0.1".equals(ip)) {
                        ip = getLocalFirstConfigIp();
                    }

                    long iip = ipToLong(ip);
                    ipSuff = (iip & 0xFFFF); //取最后的2段
                }
            }
        }

        long ts = System.currentTimeMillis() / 1000 - tsSeek; // seconds
        long idCnt = 0;
        synchronized (locker) {
            idSeed = (++idSeed) % 10000;
            idCnt = idSeed;
        }

        BigInteger id = new BigInteger(String.format("%d000000000", ts))
                .add(BigInteger.valueOf((long) (ipSuff * Math.pow(10, 4))))
                .add(BigInteger.valueOf(idCnt));
        return id;
    }

    private String getLocalIP(String networkName) {
        String ip = "";
        try {
            Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e1.nextElement();
                if (!ni.getName().equals(networkName)) {
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

    private long ipToLong(String ipAddress) {
        long result = 0;
        String[] ipAddressInArray = ipAddress.split("\\.");
        for (int i = 3; i >= 0; i--) {
            long ip = Long.parseLong(ipAddressInArray[3 - i]);
            result |= ip << (i * 8);
        }
        return result;
    }
    /**
     * 得到本地第一个非0.0.0.0和127.0.0.1的ip
     *
     * @return
     */
    private String getLocalFirstConfigIp() {
        String ip = "";
        try {
            Enumeration<?> e1 = NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e1.nextElement();
                Enumeration<?> e2 = ni.getInetAddresses();
                while (e2.hasMoreElements()) {
                    InetAddress ia = (InetAddress) e2.nextElement();
                    if (ia instanceof Inet6Address)
                        continue;
                    ip = ia.getHostAddress();
                    if ("0.0.0.0".equals(ip) || "127.0.0.1".equals(ip)) {
                        continue;
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
