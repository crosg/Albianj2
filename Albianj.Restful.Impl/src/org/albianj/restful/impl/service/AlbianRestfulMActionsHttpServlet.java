package org.albianj.restful.impl.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.albianj.datetime.AlbianDateTime;
import org.albianj.kernel.KernelSetting;
import org.albianj.net.AlbianHost;
import org.albianj.restful.impl.object.AlbianRestfulResult;
import org.albianj.restful.impl.util.AlbianRestfulUtils;
import org.albianj.restful.object.AlbianRestfulResultStyle;
import org.albianj.restful.object.IAlbianRestfulActionContext;
import org.albianj.restful.object.IAlbianRestfulResult;
import org.albianj.restful.service.IAlbianRestfulLogger;
import org.albianj.restful.service.IAlbianRestfulMActionsService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yuewen.pplogstat.IYuewenPPLogStatService;

public class AlbianRestfulMActionsHttpServlet extends HttpServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7119953581251032385L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		AlbianRestfulUtils restfulUtils = new AlbianRestfulUtils();
		IAlbianRestfulActionContext ctx = restfulUtils.AlbianRestfulActionContext(req, resp);
		String format = ctx.getCurrentParameters().get("format");
		if (format == null)
			format = "json";
		String sessionId = ctx.getCurrentSessionId();
		String queryTime = AlbianDateTime.getDateTimeString();
		AlbianServiceRouter.getLogger().info(IAlbianRestfulLogger.Name,
				"request=%s|IP=%s|time=%s|paras=%s|sesseionid=%s", req
						.getRequestURI().toString(), req.getRemoteAddr(),
				queryTime, req.getQueryString(), sessionId);

		String serviceName = ctx.getCurrentServiceName();
		String sp = ctx.getCurrentSP();
		if(Validate.isNullOrEmpty(sp)) sp = "";
		String action = ctx.getCurrentActionName();
		if (null == serviceName) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianRestfulLogger.Name,
							"sesseionid=%s,query paras is not pass.then send errno=412.",
							sessionId);

			sendErrorResponse(resp,
					"application/" + format + "; charset=UTF-8", 412, "验证未通过",
					sessionId);
			return;
		}

		if (serviceName.startsWith("Albian")
				|| serviceName.startsWith("albian")) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianRestfulLogger.Name,
							"sesseionid=%s,albianj kernel service can not be called by client.called service name:%s.",
							sessionId, serviceName);
			sendErrorResponse(resp,
					"application/" + format + "; charset=UTF-8", 412, "验证未通过",
					sessionId);
			return;
		}



		AlbianServiceRouter.getLogger()
				.info(IAlbianRestfulLogger.Name,
						"SP=%s|request=%s|IP=%s|time=%s|service=%s|paras=%s|sesseionid=%s",
						sp, req.getRequestURI().toString(),
						req.getRemoteAddr(), queryTime, serviceName,
						req.getQueryString(), sessionId);

		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/" + format + "; charset=UTF-8");

		IAlbianRestfulMActionsService service = AlbianServiceRouter.getService(
				IAlbianRestfulMActionsService.class, serviceName, false);
		if (null == service) {
			AlbianServiceRouter.getLogger().error(IAlbianRestfulLogger.Name,
					"service:%s is not exist.sessionid:%s.", serviceName,
					sessionId);
			sendErrorResponse(resp,
					"application/" + format + "; charset=UTF-8", 404, "服务没有找到",
					sessionId);
			return;
		}

		if (!service.verify(ctx)) {
			AlbianServiceRouter.getLogger().error(IAlbianRestfulLogger.Name,
					"verify service:%s is not passing.sessionid:%s.",
					serviceName, sessionId);
			sendErrorResponse(resp,
					"application/" + format + "; charset=UTF-8", 412, "验证未通过",
					sessionId);
			return;
		}
		Object[] args = new Object[] { ctx };
		
		Method mv = service.getActionVerify(action);
		boolean isPass = true;
		if(null != mv){
			try {
				isPass = (boolean) mv.invoke(service,args);
			} catch (Exception e) {
				AlbianServiceRouter.getLogger().info(IAlbianRestfulLogger.Name,
						"sesseionid=%s exec action:%s verify is error.", sessionId,serviceName);
			}
		}
		
		if(!isPass){
			AlbianServiceRouter.getLogger().info(IAlbianRestfulLogger.Name,
					"sesseionid=%s exec action:%s verify is not pass.", sessionId,serviceName);
			sendErrorResponse(resp,
					"application/" + format + "; charset=UTF-8", 412, "验证未通过",
					sessionId);
			return;
		}
		
		long begin = Calendar.getInstance().getTimeInMillis();

		Method m = service.getAction(action);
		if(null == m){
			AlbianServiceRouter.getLogger().error(IAlbianRestfulLogger.Name,
					"service:%s is not exist.sessionid:%s.", serviceName,
					sessionId);
			sendErrorResponse(resp,
					"application/" + format + "; charset=UTF-8", 404, "服务没有找到",
					sessionId);
			return;
		}
		try {
			 ctx = (IAlbianRestfulActionContext) m.invoke(service, args);
		} catch (Exception e) {
			AlbianServiceRouter.getLogger()
			.error(IAlbianRestfulLogger.Name,
					"SP=%s|request=%s|IP=%s|time=%s|service=%s|paras=%s|sesseionid=%s|invoke action:%s is fail，error info:%s.",
					sp, req.getRequestURI().toString(), 
					req.getRemoteAddr(), queryTime, 
					serviceName, req.getQueryString(), sessionId,action,e.getMessage());


			sendErrorResponse(resp,
					"application/" + format + "; charset=UTF-8", 501, "服务内部错误",
					sessionId);
			
			long e1 = Calendar.getInstance().getTimeInMillis();
			IYuewenPPLogStatService pplog = AlbianServiceRouter.getSingletonService(IYuewenPPLogStatService.class, IYuewenPPLogStatService.Name);
			if(null != pplog){
				pplog.log(KernelSetting.getAppName(),
						queryTime, req.getRemoteAddr(),serviceName,AlbianHost.getLocalIP(),serviceName,ctx.getCurrentActionName(),ctx.getResult().getReturnCode(),
						false, e1 - begin,false);
			}
			
			return;
		}
		
		long e1 = Calendar.getInstance().getTimeInMillis();
		AlbianServiceRouter.getLogger().info(IAlbianRestfulLogger.Name,
				"sesseionid=%s|timespan=%d", sessionId, e1 - begin);
		
		IYuewenPPLogStatService pplog = AlbianServiceRouter.getSingletonService(IYuewenPPLogStatService.class, IYuewenPPLogStatService.Name);
		if(null != pplog){
			pplog.log(KernelSetting.getAppName(),
					queryTime, req.getRemoteAddr(),serviceName,AlbianHost.getLocalIP(),serviceName,ctx.getCurrentActionName(),ctx.getResult().getReturnCode(),
					true, e1 - begin,false);
		}

		resp.setStatus(HttpServletResponse.SC_OK);
		String body = null;
		if (AlbianRestfulResultStyle.Xml == ctx.getResultStyle()) {
			if (null != ctx.getResult()) {
				if (ctx.getParser() != null)
					body = ctx.getParser().parserToXml(ctx.getResult())
							.toString();
				else
					body = new AlbianRestfulResultParser().parserToXml(ctx
							.getResult());
			}
		} else {
			body = JSON.toJSONString(ctx.getResult(),
					SerializerFeature.WriteDateUseDateFormat);
		}
		
//		AlbianLoggerService
//		.info(IAlbianRestfulLogger.Name,
//				"SP=%s|request=%s|response=%s|IP=%s|service=%s|paras=%s|sesseionid=%s",
//				sp, req.getRequestURI().toString(), body,
//				req.getRemoteAddr(),
//				serviceName, req.getQueryString(), sessionId);
		
		PrintWriter out = resp.getWriter();
		try {
			if(!Validate.isNullOrEmpty(body)){
				out.print(body);
				out.flush();
			}
		} catch (Exception e) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianRestfulLogger.Name,
							e,
							"service:%s print body to http stream is fail.sessionid:%s.",
							serviceName, sessionId);
		} finally {
			out.close();
			long end = Calendar.getInstance().getTimeInMillis();
			AlbianServiceRouter.getLogger()
					.info(IAlbianRestfulLogger.Name,
							"SP=%s|request=%s|response=%s|IP=%s|time=%s|timespan=%dms|service=%s|paras=%s|sesseionid=%s",
							sp, req.getRequestURI().toString(), body,
							req.getRemoteAddr(), queryTime, end - begin,
							serviceName, req.getQueryString(), sessionId);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		AlbianRestfulUtils restfulUtils = new AlbianRestfulUtils();
		IAlbianRestfulActionContext ctx = restfulUtils.AlbianRestfulActionContext(req, resp);
		String sessionId = ctx.getCurrentSessionId();
		String format = ctx.getCurrentParameters().get("format");
		if (format == null)
			format = "json";
		String queryTime = AlbianDateTime.getDateTimeString();

		AlbianServiceRouter.getLogger().info(IAlbianRestfulLogger.Name,
				"request=%s|IP=%s|time=%s|paras=%s|sesseionid=%s", req
						.getRequestURI().toString(), req.getRemoteAddr(),
				queryTime, req.getQueryString(), sessionId);

		String serviceName = ctx.getCurrentServiceName();
		String sp = ctx.getCurrentSP();
		if(Validate.isNullOrEmpty(sp)) sp = "";
		String action = ctx.getCurrentActionName();
		if (null == serviceName) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianRestfulLogger.Name,
							"sesseionid=%s,query paras is not pass.then send errno=412.",
							sessionId);

			sendErrorResponse(resp,
					"application/" + format + "; charset=UTF-8", 412, "验证未通过",
					sessionId);
			return;
		}

		if (serviceName.startsWith("Albian")
				|| serviceName.startsWith("albian")) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianRestfulLogger.Name,
							"sesseionid=%s,albianj kernel service can not be called by client.called service name:%s.",
							sessionId, serviceName);
			sendErrorResponse(resp,
					"application/" + format + "; charset=UTF-8", 412, "验证未通过",
					sessionId);
			return;
		}



		AlbianServiceRouter.getLogger()
				.info(IAlbianRestfulLogger.Name,
						"SP=%s|request=%s|IP=%s|time=%s|service=%s|paras=%s|sesseionid=%s",
						sp, req.getRequestURI().toString(),
						req.getRemoteAddr(), queryTime, serviceName,
						req.getQueryString(), sessionId);

		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/" + format + "; charset=UTF-8");

		IAlbianRestfulMActionsService service = AlbianServiceRouter.getService(
				IAlbianRestfulMActionsService.class, serviceName, false);
		if (null == service) {
			AlbianServiceRouter.getLogger().error(IAlbianRestfulLogger.Name,
					"service:%s is not exist.sessionid:%s.", serviceName,
					sessionId);
			sendErrorResponse(resp,
					"application/" + format + "; charset=UTF-8", 404, "服务没有找到",
					sessionId);
			return;
		}

		if (!service.verify(ctx)) {
			AlbianServiceRouter.getLogger().error(IAlbianRestfulLogger.Name,
					"verify service:%s is not passing.sessionid:%s.",
					serviceName, sessionId);
			sendErrorResponse(resp,
					"application/" + format + "; charset=UTF-8", 412, "验证未通过",
					sessionId);
			return;
		}

		long begin = Calendar.getInstance().getTimeInMillis();

		Method m = service.getAction(action);
		if(null == m){
			AlbianServiceRouter.getLogger().error(IAlbianRestfulLogger.Name,
					"service:%s is not exist.sessionid:%s.", serviceName,
					sessionId);
			sendErrorResponse(resp,
					"application/" + format + "; charset=UTF-8", 404, "服务没有找到",
					sessionId);
			return;
		}
		
		Object[] args = new Object[] { ctx };
		try {
			ctx = (IAlbianRestfulActionContext) m.invoke(service, args);
		} catch (Exception e) {
			AlbianServiceRouter.getLogger()
			.error(IAlbianRestfulLogger.Name,
					"SP=%s|request=%s|IP=%s|time=%s|service=%s|paras=%s|sesseionid=%s|invoke action:%s is fail，error info:%s.",
					sp, req.getRequestURI().toString(), 
					req.getRemoteAddr(), queryTime, 
					serviceName, req.getQueryString(), sessionId,action,e.getMessage());

			sendErrorResponse(resp,
					"application/" + format + "; charset=UTF-8", 501, "服务内部错误",
					sessionId);
			return;
		}

		long e1 = Calendar.getInstance().getTimeInMillis();
		AlbianServiceRouter.getLogger().info(IAlbianRestfulLogger.Name,
				"sesseionid=%s|timespan=%d", sessionId, e1 - begin);

		resp.setStatus(HttpServletResponse.SC_OK);
		String body = null;
		if (AlbianRestfulResultStyle.Xml == ctx.getResultStyle()) {
			if (null != ctx.getResult()) {
				if (ctx.getParser() != null)
					body = ctx.getParser().parserToXml(ctx.getResult())
							.toString();
				else
					body = new AlbianRestfulResultParser().parserToXml(ctx
							.getResult());
			}
		} else {
			body = JSON.toJSONString(ctx.getResult(),
					SerializerFeature.WriteDateUseDateFormat);
		}
		PrintWriter out = resp.getWriter();
		try {
			if(!Validate.isNullOrEmpty(body)){
				out.print(body);
				out.flush();
			}
		} catch (Exception e) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianRestfulLogger.Name,
							e,
							"service:%s print body to http stream is fail.sessionid:%s.",
							serviceName, sessionId);
		} finally {
			out.close();
			long end = Calendar.getInstance().getTimeInMillis();
			AlbianServiceRouter.getLogger()
					.info(IAlbianRestfulLogger.Name,
							"SP=%s|request=%s|response=%s|IP=%s|time=%s|timespan=%dms|service=%s|paras=%s|sesseionid=%s",
							sp, req.getRequestURI().toString(), body,
							req.getRemoteAddr(), queryTime, end - begin,
							serviceName, req.getQueryString(), sessionId);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doUnimplements("Delete", req, resp);
	};

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doUnimplements("Option", req, resp);
	};

	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doUnimplements("Trace", req, resp);
	};

	private void doUnimplements(String mode, HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		AlbianServiceRouter.getLogger().error(IAlbianRestfulLogger.Name,
				"client use %s,request=%s|IP=%s|time=%s", mode, req
						.getRequestURI().toString(), req.getRemoteAddr(),
				AlbianDateTime.getDateTimeString());
		sendErrorResponse(resp, "application/json; charset=UTF-8", 501,
				"服务器不支持此功能", req.getSession().getId());
	}

	private void sendErrorResponse(HttpServletResponse resp,
			String contentType, int code, String msg, String sessionId)
			throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType(contentType);
		IAlbianRestfulResult arr = new AlbianRestfulResult(code, msg);
		String body = JSON.toJSONString(arr);
		resp.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = resp.getWriter();
		try {
			out.print(body);
			out.flush();
		} catch (Exception e) {
			AlbianServiceRouter.getLogger().error(IAlbianRestfulLogger.Name, e,
					"print body to http stream is fail.sessionid:%s.",
					sessionId);
		} finally {
			out.close();
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
	}

}
