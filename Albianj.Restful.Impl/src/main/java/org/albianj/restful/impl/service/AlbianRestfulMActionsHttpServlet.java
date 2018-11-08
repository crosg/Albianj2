/*
Copyright (c) 2016, Shanghai YUEWEN Information Technology Co., Ltd. 
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Shanghai YUEWEN Information Technology Co., Ltd. 
* nor the names of its contributors may be used to endorse or promote products derived from 
* this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY SHANGHAI YUEWEN INFORMATION TECHNOLOGY CO., LTD. 
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright (c) 2016 著作权由上海阅文信息技术有限公司所有。著作权人保留一切权利。

这份授权条款，在使用者符合以下三条件的情形下，授予使用者使用及再散播本软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：

* 对于本软件源代码的再散播，必须保留上述的版权宣告、此三条件表列，以及下述的免责声明。
* 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附于散播包装中的媒介方式，重制上述之版权宣告、此三条件表列，以及下述的免责声明。
* 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称，来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。

免责声明：本软件是由上海阅文信息技术有限公司及本软件之贡献者以现状提供，本软件包装不负任何明示或默示之担保责任，
包括但不限于就适售性以及特定目的的适用性为默示性担保。加州大学董事会及本软件之贡献者，无论任何条件、无论成因或任何责任主义、
无论此责任为因合约关系、无过失责任主义或因非违约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的任何直接性、间接性、
偶发性、特殊性、惩罚性或任何结果的损害（包括但不限于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等），
不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
*/
package org.albianj.restful.impl.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yuewen.pplogstat.impl.IYuewenPPLogStatService;
import org.albianj.datetime.AlbianDateTime;
import org.albianj.kernel.KernelSetting;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.net.AlbianHost;
import org.albianj.restful.impl.object.AlbianRestfulResult;
import org.albianj.restful.impl.util.AlbianRestfulUtils;
import org.albianj.restful.impl.util.HostHelper;
import org.albianj.restful.object.AlbianRestfulResultStyle;
import org.albianj.restful.object.IAlbianRestfulActionContext;
import org.albianj.restful.object.IAlbianRestfulResult;
import org.albianj.restful.service.IAlbianRestfulLogger;
import org.albianj.restful.service.IAlbianRestfulMActionsService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Calendar;

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
        AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                sessionId, AlbianLoggerLevel.Info,
                "request=%s|IP=%s|time=%s|paras=%s", req
                        .getRequestURI().toString(), req.getRemoteAddr(),
                queryTime, req.getQueryString());

        String serviceName = ctx.getCurrentServiceName();
        String sp = ctx.getCurrentSP();
        if (Validate.isNullOrEmpty(sp)) sp = "";
        String action = ctx.getCurrentActionName();
        if (null == serviceName) {
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,
                    "query paras is not pass.then send errno=412.");

            sendErrorResponse(resp,
                    "application/" + format + "; charset=UTF-8", 412, "验证未通过",
                    sessionId);
            return;
        }

        if (serviceName.startsWith("Albian")
                || serviceName.startsWith("albian")) {
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,
                    "albianj kernel service can not be called by client.called service name:%s.",
                    serviceName);
            sendErrorResponse(resp,
                    "application/" + format + "; charset=UTF-8", 412, "验证未通过",
                    sessionId);
            return;
        }


        AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                sessionId, AlbianLoggerLevel.Info,
                "SP=%s|request=%s|IP=%s|time=%s|service=%s|paras=%s",
                sp, req.getRequestURI().toString(),
                req.getRemoteAddr(), queryTime, serviceName,
                req.getQueryString());


        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/" + format + "; charset=UTF-8");

        IAlbianRestfulMActionsService service = AlbianServiceRouter.getSingletonService(
                IAlbianRestfulMActionsService.class, serviceName, false);
        if (null == service) {
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,
                    "service:%s is not exist.", serviceName);

            sendErrorResponse(resp,
                    "application/" + format + "; charset=UTF-8", 404, "服务没有找到",
                    sessionId);
            return;
        }

        if (!service.verify(ctx)) {
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,
                    "verify service:%s is not passing.",
                    serviceName);

            sendErrorResponse(resp,
                    "application/" + format + "; charset=UTF-8", 412, "验证未通过",
                    sessionId);
            return;
        }
        Object[] args = new Object[]{ctx};

        Method mv = service.getActionVerify(action);
        boolean isPass = true;
        if (null != mv) {
            try {
                isPass = (boolean) mv.invoke(service, args);
            } catch (Exception e) {
                AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                        sessionId, AlbianLoggerLevel.Error,e,
                        "exec action:%s of service:%s verify is error.",action, serviceName);

            }
        }

        if (!isPass) {
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,
                    "exec action:%s of service:%s verify is error.",action, serviceName);


            sendErrorResponse(resp,
                    "application/" + format + "; charset=UTF-8", 412, "验证未通过",
                    sessionId);
            return;
        }

        long begin = Calendar.getInstance().getTimeInMillis();

        IAlbianRestfulMActionsService rs = (IAlbianRestfulMActionsService) service.getRealService();
        Method m = rs.getAction(action);
        if (null == m) {
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,
                    "action:%s in the service:%s is not exist.",action, serviceName);


            sendErrorResponse(resp,
                    "application/" + format + "; charset=UTF-8", 404, "服务没有找到",
                    sessionId);
            return;
        }
        try {
            ctx = (IAlbianRestfulActionContext) m.invoke(service, args);
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,e,
                    "SP=%s|request=%s|IP=%s|time=%s|service=%s|paras=%s|invoke action:%s is fail.",
                    sp, req.getRequestURI().toString(),
                    req.getRemoteAddr(), queryTime,
                    serviceName, req.getQueryString(), action);

            sendErrorResponse(resp,
                    "application/" + format + "; charset=UTF-8", 501, "服务内部错误",
                    sessionId);

            long e1 = Calendar.getInstance().getTimeInMillis();
            IYuewenPPLogStatService pplog = AlbianServiceRouter.getSingletonService(IYuewenPPLogStatService.class, IYuewenPPLogStatService.Name);
            if (null != pplog) {
                pplog.log(KernelSetting.getAppName(),
                        queryTime, HostHelper.getRemoteHost(req), serviceName, AlbianHost.getLocalIP(), serviceName, ctx.getCurrentActionName(), ctx.getResult().getReturnCode(),
                        false, e1 - begin, false);
            }

            return;
        }finally {
            long e1 = Calendar.getInstance().getTimeInMillis();
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Info,
                    "timespan=%d", e1 - begin);

            IYuewenPPLogStatService pplog = AlbianServiceRouter.getSingletonService(IYuewenPPLogStatService.class, IYuewenPPLogStatService.Name);
            if (null != pplog) {
                pplog.log(KernelSetting.getAppName(),
                        queryTime, HostHelper.getRemoteHost(req), serviceName, AlbianHost.getLocalIP(), serviceName, ctx.getCurrentActionName(), ctx.getResult().getReturnCode(),
                        true, e1 - begin, false);
            }

        }

        resp.setStatus(HttpServletResponse.SC_OK);

    if(AlbianRestfulResultStyle.Stream == ctx.getResultStyle()) {
        OutputStream out = resp.getOutputStream();
        try {
            if (null != ctx.getResult()) {
                byte[] bytes = (byte[]) ctx.getResult().getResult();
                out.write(bytes);
                out.flush();
            }
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,e,
                    "service:%s print body to http stream is fail.",
                    serviceName);

        } finally {
            out.close();
            long end = Calendar.getInstance().getTimeInMillis();
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Info,
                    "SP=%s|request=%s|response=%s|IP=%s|time=%s|timespan=%dms|service=%s|paras=%s",
                    sp, req.getRequestURI().toString(), "get stream,so do not logger context ",
                    req.getRemoteAddr(), queryTime, end - begin,
                    serviceName, req.getQueryString());

        }
    } else {
        String body = null;
        if(0 == ctx.getVerison()) {
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
                if (ctx.getShowNull()) {
                    body = JSON.toJSONString(ctx.getResult(),
                            SerializerFeature.WriteDateUseDateFormat,
                            SerializerFeature.SkipTransientField,
                            SerializerFeature.WriteMapNullValue,
                            SerializerFeature.WriteNullBooleanAsFalse,
                            SerializerFeature.WriteNullListAsEmpty,
                            SerializerFeature.WriteNullNumberAsZero,
                            SerializerFeature.WriteNullStringAsEmpty,
                            SerializerFeature.DisableCircularReferenceDetect
                    );
                } else {
                    body = JSON.toJSONString(ctx.getResult(),
                            SerializerFeature.SkipTransientField,
                            SerializerFeature.WriteDateUseDateFormat,
                            SerializerFeature.DisableCircularReferenceDetect
                    );
                }
            }
        } else {
            if (AlbianRestfulResultStyle.Xml == ctx.getResultStyle()) {
                if (null != ctx.getResultV1()) {
                    if (ctx.getParserV1() != null)
                        body = ctx.getParserV1().parserToXml(ctx.getResultV1())
                                .toString();
                    else
                        body = new AlbianRestfulResultParserV1().parserToXml(ctx
                                .getResultV1());
                }
            } else {
                if (ctx.getShowNull()) {
                    body = JSON.toJSONString(ctx.getResultV1(),
                            SerializerFeature.WriteDateUseDateFormat,
                            SerializerFeature.SkipTransientField,
                            SerializerFeature.WriteMapNullValue,
                            SerializerFeature.WriteNullBooleanAsFalse,
                            SerializerFeature.WriteNullListAsEmpty,
                            SerializerFeature.WriteNullNumberAsZero,
                            SerializerFeature.WriteNullStringAsEmpty,
                            SerializerFeature.DisableCircularReferenceDetect
                    );
                } else {
                    body = JSON.toJSONString(ctx.getResultV1(),
                            SerializerFeature.SkipTransientField,
                            SerializerFeature.WriteDateUseDateFormat,
                            SerializerFeature.DisableCircularReferenceDetect
                    );
                }
            }
        }

        if (null != ctx.getBodyFilterHandler()) {
            body = ctx.getBodyFilterHandler().filter(body);
        }

        PrintWriter out = resp.getWriter();
        try {
            if (!Validate.isNullOrEmpty(body)) {
                out.print(body);
                out.flush();
            }
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,e,
                    "service:%s print body to http stream is fail.",
                    serviceName);

        } finally {
            out.close();
            long end = Calendar.getInstance().getTimeInMillis();
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Info,
                    "SP=%s|request=%s|response=%s|IP=%s|time=%s|timespan=%dms|service=%s|paras=%s",
                    sp, req.getRequestURI().toString(), body,
                    req.getRemoteAddr(), queryTime, end - begin,
                    serviceName, req.getQueryString());

        }
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

        AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                sessionId, AlbianLoggerLevel.Info,
                "request=%s|IP=%s|time=%s|paras=%s", req
                        .getRequestURI().toString(), req.getRemoteAddr(),
                queryTime, req.getQueryString());

        String serviceName = ctx.getCurrentServiceName();
        String sp = ctx.getCurrentSP();
        if (Validate.isNullOrEmpty(sp)) sp = "";
        String action = ctx.getCurrentActionName();
        if (null == serviceName) {
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,
                    "query paras is not pass.then send errno=412.");


            sendErrorResponse(resp,
                    "application/" + format + "; charset=UTF-8", 412, "验证未通过",
                    sessionId);
            return;
        }

        if (serviceName.startsWith("Albian")
                || serviceName.startsWith("albian")) {
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,
                    "albianj kernel service can not be called by client.called service name:%s.",
                    serviceName);

            sendErrorResponse(resp,
                    "application/" + format + "; charset=UTF-8", 412, "验证未通过",
                    sessionId);
            return;
        }


        AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                sessionId, AlbianLoggerLevel.Info,
                "SP=%s|request=%s|IP=%s|time=%s|service=%s|paras=%s",
                sp, req.getRequestURI().toString(),
                req.getRemoteAddr(), queryTime, serviceName,
                req.getQueryString());


        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/" + format + "; charset=UTF-8");

        IAlbianRestfulMActionsService service = AlbianServiceRouter.getSingletonService(
                IAlbianRestfulMActionsService.class, serviceName, false);
        if (null == service) {
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,
                    "service:%s is not exist.", serviceName);

            sendErrorResponse(resp,
                    "application/" + format + "; charset=UTF-8", 404, "服务没有找到",
                    sessionId);
            return;
        }

        if (!service.verify(ctx)) {
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,
                    "verify service:%s is not passing.",
                    serviceName);


            sendErrorResponse(resp,
                    "application/" + format + "; charset=UTF-8", 412, "验证未通过",
                    sessionId);
            return;
        }

        long begin = Calendar.getInstance().getTimeInMillis();

        Method m = service.getAction(action);
        if (null == m) {
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,
                    "action:%s in the service:%s is not exist.",action, serviceName);

            sendErrorResponse(resp,
                    "application/" + format + "; charset=UTF-8", 404, "服务没有找到",
                    sessionId);
            return;
        }

        Object[] args = new Object[]{ctx};
        try {
            ctx = (IAlbianRestfulActionContext) m.invoke(service, args);
        } catch (Exception e) {

            long e1 = Calendar.getInstance().getTimeInMillis();
            IYuewenPPLogStatService pplog = AlbianServiceRouter.getSingletonService(IYuewenPPLogStatService.class, IYuewenPPLogStatService.Name);
            if (null != pplog) {
                pplog.log(KernelSetting.getAppName(),
                        queryTime, HostHelper.getRemoteHost(req), serviceName, AlbianHost.getLocalIP(), serviceName, ctx.getCurrentActionName(), ctx.getResult().getReturnCode(),
                        false, e1 - begin, false);
            }


            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId, AlbianLoggerLevel.Error,e,
                    "SP=%s|request=%s|IP=%s|time=%s|service=%s|paras=%s|invoke action:%s is fail.",
                    sp, req.getRequestURI().toString(),
                    req.getRemoteAddr(), queryTime,
                    serviceName, req.getQueryString(), action);

            sendErrorResponse(resp,
                    "application/" + format + "; charset=UTF-8", 501, "服务内部错误",
                    sessionId);
            return;
        }

        long e1 = Calendar.getInstance().getTimeInMillis();
//        long e1 = Calendar.getInstance().getTimeInMillis();

        AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                sessionId, AlbianLoggerLevel.Info,
                "timespan=%d", e1 - begin);

        IYuewenPPLogStatService pplog = AlbianServiceRouter.getSingletonService(IYuewenPPLogStatService.class, IYuewenPPLogStatService.Name);
        if (null != pplog) {
            pplog.log(KernelSetting.getAppName(),
                    queryTime, HostHelper.getRemoteHost(req), serviceName, AlbianHost.getLocalIP(), serviceName, ctx.getCurrentActionName(), ctx.getResult().getReturnCode(),
                    true, e1 - begin, false);
        }


        resp.setStatus(HttpServletResponse.SC_OK);

        if(AlbianRestfulResultStyle.Stream == ctx.getResultStyle()) {
            OutputStream out = resp.getOutputStream();
            try {
                if (null != ctx.getResult()) {
                    byte[] bytes = (byte[]) ctx.getResult().getResult();
                    out.write(bytes);
                    out.flush();
                }
            } catch (Exception e) {
                AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                        sessionId, AlbianLoggerLevel.Error,e,
                        "service:%s print body to http stream is fail.",
                        serviceName);

            } finally {
                out.close();
                long end = Calendar.getInstance().getTimeInMillis();
                AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                        sessionId, AlbianLoggerLevel.Info,
                        "SP=%s|request=%s|response=%s|IP=%s|time=%s|timespan=%dms|service=%s|paras=%s",
                        sp, req.getRequestURI().toString(), "get stream,so do not logger context ",
                        req.getRemoteAddr(), queryTime, end - begin,
                        serviceName, req.getQueryString());


            }
        } else {
            String body = null;
            if(0 == ctx.getVerison()) {
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
                    if (ctx.getShowNull()) {
                        body = JSON.toJSONString(ctx.getResult(),
                                SerializerFeature.SkipTransientField,
                                SerializerFeature.WriteDateUseDateFormat,
                                SerializerFeature.WriteMapNullValue,
                                SerializerFeature.WriteNullBooleanAsFalse,
                                SerializerFeature.WriteNullListAsEmpty,
                                SerializerFeature.WriteNullNumberAsZero,
                                SerializerFeature.WriteNullStringAsEmpty,
                                SerializerFeature.DisableCircularReferenceDetect

                        );
                    } else {
                        body = JSON.toJSONString(ctx.getResult(),
                                SerializerFeature.SkipTransientField,
                                SerializerFeature.WriteDateUseDateFormat,
                                SerializerFeature.DisableCircularReferenceDetect
                        );
                    }
                }
            } else {
                if (AlbianRestfulResultStyle.Xml == ctx.getResultStyle()) {
                    if (null != ctx.getResultV1()) {
                        if (ctx.getParserV1() != null)
                            body = ctx.getParserV1().parserToXml(ctx.getResultV1())
                                    .toString();
                        else
                            body = new AlbianRestfulResultParserV1().parserToXml(ctx
                                    .getResultV1());
                    }
                } else {
                    if (ctx.getShowNull()) {
                        body = JSON.toJSONString(ctx.getResultV1(),
                                SerializerFeature.SkipTransientField,
                                SerializerFeature.WriteDateUseDateFormat,
                                SerializerFeature.WriteMapNullValue,
                                SerializerFeature.WriteNullBooleanAsFalse,
                                SerializerFeature.WriteNullListAsEmpty,
                                SerializerFeature.WriteNullNumberAsZero,
                                SerializerFeature.WriteNullStringAsEmpty,
                                SerializerFeature.DisableCircularReferenceDetect

                        );
                    } else {
                        body = JSON.toJSONString(ctx.getResultV1(),
                                SerializerFeature.SkipTransientField,
                                SerializerFeature.WriteDateUseDateFormat,
                                SerializerFeature.DisableCircularReferenceDetect
                        );
                    }
                }
            }

            if (null != ctx.getBodyFilterHandler()) {
                body = ctx.getBodyFilterHandler().filter(body);
            }


            PrintWriter out = resp.getWriter();
            try {
                if (!Validate.isNullOrEmpty(body)) {
                    out.print(body);
                    out.flush();
                }
            } catch (Exception e) {
                AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                        sessionId, AlbianLoggerLevel.Error,e,
                        "service:%s print body to http stream is fail.",
                        serviceName);


            } finally {
                out.close();
                long end = Calendar.getInstance().getTimeInMillis();
                AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                        sessionId, AlbianLoggerLevel.Info,
                        "SP=%s|request=%s|response=%s|IP=%s|time=%s|timespan=%dms|service=%s|paras=%s",
                        sp, req.getRequestURI().toString(), body,
                        req.getRemoteAddr(), queryTime, end - begin,
                        serviceName, req.getQueryString());

            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doUnimplements("Delete", req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doUnimplements("Option", req, resp);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doUnimplements("Trace", req, resp);
    }

    private void doUnimplements(String mode, HttpServletRequest req,
                                HttpServletResponse resp) throws ServletException, IOException {
        AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                req.getSession().getId(),AlbianLoggerLevel.Error,
                "client use %s,request=%s|IP=%s",
                mode, req.getRequestURI().toString(), req.getRemoteAddr());
//        AlbianServiceRouter.getLogger().error(IAlbianRestfulLogger.Name,
//                "client use %s,request=%s|IP=%s|time=%s", mode, req
//                        .getRequestURI().toString(), req.getRemoteAddr(),
//                AlbianDateTime.getDateTimeString());
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
            AlbianServiceRouter.getLogger2().log(IAlbianRestfulLogger.Name,
                    sessionId,AlbianLoggerLevel.Error,e,
                    "print body to http stream is fail.");

//            AlbianServiceRouter.getLogger().error(IAlbianRestfulLogger.Name, e,
//                    "print body to http stream is fail.sessionid:%s.",
//                    sessionId);
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
