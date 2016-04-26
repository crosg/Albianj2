package org.albianj.restful.impl.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.albianj.kernel.IAlbianLogicIdService;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.restful.impl.object.AlbianRestfulActionContext;
import org.albianj.restful.object.IAlbianRestfulActionContext;
import org.albianj.service.AlbianServiceRouter;

/**
 * @author Sean
 *
 *         restful 参数获取
 */

public class AlbianRestfulUtils {
	public  IAlbianRestfulActionContext AlbianRestfulActionContext(HttpServletRequest req,
			HttpServletResponse resp) {
		Map<String, String> param_pairs = splitQuery(req.getQueryString());
		IAlbianLogicIdService lids = AlbianServiceRouter.getLogIdService();
		return new AlbianRestfulActionContext(req, resp,
				req.getServletContext(), param_pairs.get("service"),
				param_pairs.get("action"), lids.makeStringUNID("session"),//req.getSession(true).getId(),内存持续增长
				param_pairs.get("sp"), param_pairs, getData(req));
	}
	
	public  IAlbianRestfulActionContext AlbianRestfulActionContext_Safe(HttpServletRequest req,
			HttpServletResponse resp) {
		Map<String, String> param_pairs = splitQuery(req.getQueryString());
		IAlbianLogicIdService lids = AlbianServiceRouter.getLogIdService();
		return new AlbianRestfulActionContext(req, resp,
				req.getServletContext(), param_pairs.get("service"),
				param_pairs.get("action"), lids.makeStringUNID("session"),//req.getSession(true).getId(),内存持续增长
				param_pairs.get("sp"), param_pairs, null);
	}

	public  Map<String, String> splitQuery(String qstring) {
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		// String query = url.getQuery();
		String[] pairs = qstring.split("&");
		for (String pair : pairs) {
			int idx = pair.indexOf("=");
			try {
				query_pairs.put(
						URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
						URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				AlbianServiceRouter.getLogger().error(
						IAlbianLoggerService.AlbianRunningLoggerName, e,
						"splitQuery UnsupportedEncodingException");
			}
		}
		return query_pairs;
	}

	public String getData(HttpServletRequest req) {
		StringBuffer info = new java.lang.StringBuffer();
		InputStream in = null;
		try {
			in = req.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			AlbianServiceRouter.getLogger().error(
					IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
		}
		BufferedInputStream buf = new BufferedInputStream(in);
		byte[] buffer = new byte[1024];
		int iRead;
		try {
			while ((iRead = buf.read(buffer)) != -1) {
				info.append(new String(buffer, 0, iRead, "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			AlbianServiceRouter.getLogger().error(
					IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
		} catch (IOException e) {
			AlbianServiceRouter.getLogger().error(
					IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
		}

		return info.toString();
	}
	
	public static String getLimitedData(HttpServletRequest req, int len) throws Exception{
		StringBuffer info = new java.lang.StringBuffer();
		InputStream in = null;
		try {
			in = req.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			AlbianServiceRouter.getLogger().error(
					IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
		}
		BufferedInputStream buf = new BufferedInputStream(in);
		byte[] buffer = new byte[1024];
		int iRead;
		try {
			while ((iRead = buf.read(buffer)) != -1) {
				info.append(new String(buffer, 0, iRead, "UTF-8"));
				if(info.length() > len){
					throw new Exception("the length of inputStream is out of limit length:"+len);
				}
			}
		} catch (UnsupportedEncodingException e) {
			AlbianServiceRouter.getLogger().error(
					IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
		} catch (IOException e) {
			AlbianServiceRouter.getLogger().error(
					IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
		}finally{
			buf.close();
			in.close();
		}

		return info.toString();
	}
}
