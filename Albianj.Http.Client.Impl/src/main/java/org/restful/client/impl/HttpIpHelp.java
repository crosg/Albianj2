package org.restful.client.impl;//package org.restful.client.impl;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.net.HttpURLConnection;
//import java.net.InetAddress;
//import java.net.URL;
//import java.net.URLConnection;
//import java.net.UnknownHostException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//
//import model.comm.PropSetting;
//
//import org.albianj.logger.IAlbianLoggerService;
//import org.albianj.net.AlbianHost;
//import org.albianj.service.AlbianServiceRouter;
//import org.albianj.kernel.KernelSetting;
//
//import com.yuewen.pplogstat.IYuewenPPLogStatService;
//import comm.Constant;
//
//public class HttpIpHelp {
    //
//	IAlbianLoggerService logutil = AlbianServiceRouter.getLogger();
//
//	private static String local = System.getProperty("user.dir");
//	private static String serviceName = Constant.getConfigValue("serviceName",local);
//
//    public String getRemoteHost(HttpServletRequest request) {
//        String ipAddress = request.getHeader("x-client-ip");
//        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("x-forwarded-for");
//        }
//        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("Proxy-Client-IP");
//        }
//        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getRemoteAddr();
//            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
//                //根据网卡取本机配置的IP
//                InetAddress inet = null;
//                try {
//                    inet = InetAddress.getLocalHost();
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                }
//                ipAddress = inet.     getHostAddress();
//            }
//        }
//        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
//        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
//            if (ipAddress.indexOf(",") > 0) {
//                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
//            }
//        }
//        return ipAddress;
//    }
//}
//
//	public String sendRequest(String urlString, String params){
//    	HttpURLConnection httpConn = null;
//        OutputStream out = null;
//        String result = null;
//
//		try {
//			URL url = new URL(urlString);
//			httpConn = (HttpURLConnection) url.openConnection();
//			httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//			httpConn.setRequestMethod("GET");
//			httpConn.setConnectTimeout(5000);
//			httpConn.setReadTimeout(5000);
//			httpConn.setDoOutput(true);
//			httpConn.setDoInput(true);
//			out = httpConn.getOutputStream();
//			if(params!=null&&!"".equals(params)){
//				byte[] buf = params.getBytes("UTF-8");
//				httpConn.setRequestProperty("Content-Length", String.valueOf(buf.length));
//				out.write(buf);
//			}
//
//	        byte[] datas = readInputStream(httpConn.getInputStream());
//	        result = new String(datas, "UTF-8");
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if(out != null)
//					out.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return result;
//    }
//
//    private byte[] readInputStream(InputStream inStream) throws Exception{
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        byte[] buffer = new byte[1024];
//        int len = 0;
//        while( (len = inStream.read(buffer)) != -1){
//            outStream.write(buffer, 0, len);
//        }
//        byte[] data = outStream.toByteArray();//网页的二进制数据
//        outStream.close();
//        inStream.close();
//        return data;
//    }
//
//    /**
//     * 向指定URL发送GET方法的请求
//     *
//     * @param url
//     *            发送请求的URL
//     * @param param
//     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
//     * @return URL 所代表远程资源的响应结果
//     */
//    public String sendGet(String url, String param) {
//        String result = "";
//        BufferedReader in = null;
//
//        Long begain = System.currentTimeMillis();
//        try {
//            String urlNameString = url + "?" + param;
//            URL realUrl = new URL(urlNameString);
//            logutil
//			.info(IAlbianLoggerService.AlbianRunningLoggerName,"URL=%s",urlNameString);
////             打开和URL之间的连接
//            URLConnection connection = realUrl.openConnection();
//            // 设置通用的请求属性
//            connection.setRequestProperty("accept", "*/*");
//            connection.setRequestProperty("connection", "Keep-Alive");
//            connection.setRequestProperty("user-agent",
//                    "Mozilla/5.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//            connection.setRequestProperty("Accept-Charset", "UTF-8");
//            connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
//            // 建立实际的连接
//            connection.connect();
//            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
//            // 定义 BufferedReader输入流来读取URL的响应
//            in = new BufferedReader(new InputStreamReader(
//                    connection.getInputStream()));
//            String line;
//            while ((line = in.readLine()) != null) {
//                result += line;
//            }
//
//            //监控
//            monitorLog(begain, url, param, 1,true, false);
//        } catch (Exception e) {
//            System.out.println("发送GET请求出现异常！" + e);
//            //监控
//            monitorLog(begain, url, param, 0,false, false);
//            e.printStackTrace();
//            logutil
//			.info(IAlbianLoggerService.AlbianRunningLoggerName,
//					"发送GET请求出现异常！"+e.toString());
//        }
//        // 使用finally块来关闭输入流
//        finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (Exception e2) {
//                e2.printStackTrace();
//            }
//        }
//        return result;
//    }
//
//    /**
//     * 向指定 URL 发送POST方法的请求
//     *
//     * @param url
//     *            发送请求的 URL
//     * @param param
//     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
//     * @return 所代表远程资源的响应结果
//     */
//    public String sendPost(String url, String param) {
//        PrintWriter out = null;
//        BufferedReader in = null;
//        String result = "";
//
//        Long begain = System.currentTimeMillis();
//        try {
//            URL realUrl = new URL(url);
//            // 打开和URL之间的连接
//            URLConnection conn = realUrl.openConnection();
//            // 设置通用的请求属性
//            conn.setRequestProperty("accept", "*/*");
//            conn.setRequestProperty("connection", "Keep-Alive");
//            conn.setRequestProperty("user-agent",
//                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//            // 发送POST请求必须设置如下两行
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            // 获取URLConnection对象对应的输出流
//            out = new PrintWriter(conn.getOutputStream());
//            // 发送请求参数
//            out.print(param);
//            // flush输出流的缓冲
//            out.flush();
//            // 定义BufferedReader输入流来读取URL的响应
//            in = new BufferedReader(
//                    new InputStreamReader(conn.getInputStream()));
//            String line;
//            while ((line = in.readLine()) != null) {
//                result += line;
//            }
//          //监控
//            monitorLog(begain, url, param, 1,true, false);
//        } catch (Exception e) {
//        	logutil
//			.info(IAlbianLoggerService.AlbianRunningLoggerName,
//					"发送 POST 请求出现异常！"+e);
//          //监控
//            monitorLog(begain, url, param, 0,false, false);
//            e.printStackTrace();
//        }
//        //使用finally块来关闭输出流、输入流
//        finally{
//            try{
//                if(out!=null){
//                    out.close();
//                }
//                if(in!=null){
//                    in.close();
//                }
//            }
//            catch(IOException ex){
//                ex.printStackTrace();
//            }
//        }
//        return result;
//    }
//
//
//
//
//    /**
//     * 上传图片
//     * @param urlStr
//     * @param textMap
//     * @param fileMap
//     * @return
//     */
//    public static String formUpload(String urlStr, Map<String, String> textMap, Map<String, String> fileMap) {
//        String res = "";
//        HttpURLConnection conn = null;
//        String BOUNDARY = "---------------------------123821742118716"; //boundary就是request头和上传文件内容的分隔符
//        try {
//            URL url = new URL(urlStr);
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setConnectTimeout(5000);
//            conn.setReadTimeout(30000);
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setUseCaches(false);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Connection", "Keep-Alive");
//            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
//            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
//
//            OutputStream out = new DataOutputStream(conn.getOutputStream());
//            // text
//            if (textMap != null) {
//                StringBuffer strBuf = new StringBuffer();
//                Iterator<Map.Entry<String, String>> iter = textMap.entrySet().iterator();
//                while (iter.hasNext()) {
//                    Map.Entry<String, String> entry = iter.next();
//                    String inputName = (String) entry.getKey();
//                    String inputValue = (String) entry.getValue();
//                    if (inputValue == null) {
//                        continue;
//                    }
//                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
//                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
//                    strBuf.append(inputValue);
//                }
//                out.write(strBuf.toString().getBytes());
//            }
//
//            // file
//            if (fileMap != null) {
//                Iterator<Map.Entry<String, String>> iter = fileMap.entrySet().iterator();
//                while (iter.hasNext()) {
//                    Map.Entry<String, String> entry = iter.next();
//                    String inputName = (String) entry.getKey();
//                    String inputValue = (String) entry.getValue();
//                    if (inputValue == null) {
//                        continue;
//                    }
//                    File file = new File(inputValue);
//                    String filename = file.getName();
//                    MagicMatch match = Magic.getMagicMatch(file, false, true);
//                    String contentType = match.getMimeType();
//
//                    StringBuffer strBuf = new StringBuffer();
//                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
//                    strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename + "\"\r\n");
//                    strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
//
//                    out.write(strBuf.toString().getBytes());
//
//                    DataInputStream in = new DataInputStream(new FileInputStream(file));
//                    int bytes = 0;
//                    byte[] bufferOut = new byte[1024];
//                    while ((bytes = in.read(bufferOut)) != -1) {
//                        out.write(bufferOut, 0, bytes);
//                    }
//                    in.close();
//                }
//            }
//
//            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
//            out.write(endData);
//            out.flush();
//            out.close();
//
//            // 读取返回数据
//            StringBuffer strBuf = new StringBuffer();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                strBuf.append(line).append("\n");
//            }
//            res = strBuf.toString();
//            reader.close();
//            reader = null;
//        } catch (Exception e) {
//            System.out.println("发送POST请求出错。" + urlStr);
//            e.printStackTrace();
//        } finally {
//            if (conn != null) {
//                conn.disconnect();
//                conn = null;
//            }
//        }
//        return res;
//    }
//
//
//
//
//
//    @SuppressWarnings({ "deprecation", "static-access" })
//	private void monitorLog(Long begain, String url, String param,
//			int resultCode, Boolean result, Boolean isTimeOut) {
//		if("dev".equals(PropSetting.getIsUat())) return;
//		Long end = System.currentTimeMillis();
//		SimpleDateFormat  df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//		String serviceip = url;
//		String servicename = "";
//		String interfacename = "";
//		//int index = url.indexOf("/service");
//		int index = url.indexOf("?");
//		if (index > 0) {
//			serviceip = url.substring(0, index);
//			servicename = url.substring(index);
//			interfacename = param.substring(param.indexOf("=") + 1,
//					param.indexOf("&"));
//		}
//		if(null == servicename || ("").equals(servicename) ){
//			servicename = serviceip;
//		}
//
//		String serName = "api";
//		int in = serviceName.indexOf("/");
//		if(in > 0){
//			serName = serviceName.substring(0,serviceName.indexOf("/"));
//		}
//		AlbianHost host = new AlbianHost();
//		IYuewenPPLogStatService pplog = AlbianServiceRouter
//				.getSingletonService(IYuewenPPLogStatService.class,
//						IYuewenPPLogStatService.Name);
//
//		try {
//			pplog.log(KernelSetting.getAppName(), df.format(new Date(begain)),
//					host.getHostIp(host.getInetAddress()), serName, serviceip,
//					servicename, interfacename, resultCode, result, end
//							- begain, isTimeOut);
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}
