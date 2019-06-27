package org.albianj.mvc.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.albianj.datetime.AlbianDateTime;
import org.albianj.io.Path;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.ILoggerService2;
import org.albianj.mvc.ActionResult;
import org.albianj.mvc.HttpActionMethod;
import org.albianj.mvc.HttpContext;
import org.albianj.mvc.service.IMVCConfigurtionService;
import org.albianj.mvc.service.IResourceService;
import org.albianj.mvc.view.View;
import org.albianj.mvc.config.AlbianHttpConfigurtion;
import org.albianj.mvc.config.ViewActionConfigurtion;
import org.albianj.mvc.config.ViewConfigurtion;
import org.albianj.mvc.server.IServerLifeCycle;
import org.albianj.mvc.service.IBrushingService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.text.StringHelper;
import org.albianj.verify.Validate;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AlbianWebServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 7660798281505576175L;

    @Override
    public void init() throws ServletException {
        try {
            IMVCConfigurtionService albianMVCConfigurtionService = AlbianServiceRouter.getSingletonService(IMVCConfigurtionService.class,
                    IMVCConfigurtionService.Name);
            IServerLifeCycle server = albianMVCConfigurtionService.getHttpConfigurtion().getServerLifeCycle();
            if(null != server){
                server.ServerStartup(albianMVCConfigurtionService.getHttpConfigurtion());
            }
        }catch (Exception e){
            AlbianServiceRouter.throwException("WebStartup", ILoggerService2.AlbianRunningLoggerName,
                    "Startup web server fail.",e);
        }
        super.init();
    }


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        //super.doGet(req,resp);
        handleRequest(req, resp, false);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        // super.doPost(req,resp);
        handleRequest(req, resp, true);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp, boolean isPost) throws ServletException {
        boolean isNotFound = false;
        boolean isError = false;
        boolean isAjaxRequest = false;
        String sessionId = null;
        AlbianHttpConfigurtion c = null;
        do {
            try {
                IMVCConfigurtionService albianMVCConfigurtionService = AlbianServiceRouter.getSingletonService(IMVCConfigurtionService.class,
                        IMVCConfigurtionService.Name);

                c = albianMVCConfigurtionService.getHttpConfigurtion();
                resp.setCharacterEncoding(c.getCharset());
                req.setCharacterEncoding(c.getCharset());
                HttpContext ctx = new HttpContext(isPost, req, resp,
                        this.getServletContext(),
                        req.getSession(),
                        c);
                sessionId = ctx.getHttpSessionId();

                IBrushingService abs = AlbianServiceRouter.getSingletonService(IBrushingService.class, IBrushingService.Name);
                if (null != abs && !abs.consume(req)) {
                    AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                            ctx.getHttpSessionId(), AlbianLoggerLevel.Error,
                            "client is flushing the web service.so donot back data to client.");
                    isError = true;
                    break;
                }

                IResourceService ars = AlbianServiceRouter.getSingletonService(IResourceService.class, IResourceService.Name);
                if (ars.isResourceRequest(ctx)) {
                    try {
                        ars.renderResource(ctx);
                        return;
                    } catch (IOException e) {
                        AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                                ctx.getHttpSessionId(), AlbianLoggerLevel.Error, e,
                                "render resource is fail.");
                        isError = true;
                        break;
                    }
                }

                AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                        ctx.getHttpSessionId(), AlbianLoggerLevel.Info,
                        "%s request=%s|IP=%s|time=%s|paras=%s",
                        isPost ? "POST" : "GET",
                        req.getRequestURI().toString(), req.getRemoteAddr(),
                        AlbianDateTime.getDateTimeString(), req.getQueryString());

                HttpActionMethod ham = isPost ? HttpActionMethod.Post : HttpActionMethod.Get;

                parserRequestUrl(req, ctx, isPost);

                isAjaxRequest = isAjaxRequest(req);
                ctx.setAjaxRequest(isAjaxRequest);

                boolean isMultipartRequest = isMultipartRequest(req);
                ctx.setMultipartRequest(isMultipartRequest);

                Map<String, ViewConfigurtion> templates = c.getTemplates();
                if (null == templates) {
                    AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                            ctx.getHttpSessionId(), AlbianLoggerLevel.Error,
                            "there is not template in the webapps.");
                    isNotFound = true;
                    break;
                }

                ViewConfigurtion pc = null;
                if(ctx.isWelcomeViewRequest()) {
                  pc = c.getWelcomePage();
                } else {
                    pc = templates.get(ctx.getTemplateFullName());
                }
                if (null == pc) {
                    AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                            ctx.getHttpSessionId(), AlbianLoggerLevel.Error,
                            "not found the mapping class by template:%s.",
                            ctx.getTemplateFullName());
                    isNotFound = true;
                    break;
                }
                ctx.setPageConfigurtion(pc);
                Class<?> cla = pc.getRealClass();
                if (!View.class.isAssignableFrom(cla)) {
                    AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                            ctx.getHttpSessionId(), AlbianLoggerLevel.Error,
                            "the mapping class:%s is not base-on View,template -> %s.",
                            pc.getFullClassName(), ctx.getTemplateFullName());
                    isError = true;
                    break;
                }

                View page = null;
                try {
                    page = pc.getRealClass().newInstance();
                    page.kinit(ctx);
                } catch (InstantiationException | IllegalAccessException e) {
                    AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                            ctx.getHttpSessionId(), AlbianLoggerLevel.Error, e,
                            "new instance by the mapping class:%s is fail.template -> %s.",
                            pc.getFullClassName(), ctx.getTemplateFullName());
                    isError = true;
                    break;
                }

                if (page.isMultiActions()) {
                    String actionName = page.getCurrentActionName();
                    if (Validate.isNullOrEmptyOrAllSpace(actionName)) {
                        AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                                ctx.getHttpSessionId(), AlbianLoggerLevel.Error,
                                "not get the actionof mulit-actions of view class:%s is fail.template -> %s.then use default:execute.",
                                pc.getFullClassName(), ctx.getTemplateFullName());
                    } else {
                        ctx.setActionName(actionName);
                    }
                }

                ActionResult ar = page.interceptor(ctx);
                switch (ar.getResultType()) {
                    case ActionResult.Redirect: {
                        Object rc = ar.getResult();
                        if (null == rc && !(rc instanceof String)) {
                            isError = true;
                            break;
                        }
                        resp.sendRedirect(rc.toString());
                        return;
                    }
                    case ActionResult.OutputStream: {
                        return;
                    }
                    case ActionResult.InnerError: {
                        isError = true;
                        Object rc = ar.getResult();
                        AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                                ctx.getHttpSessionId(), AlbianLoggerLevel.Error,
                                "inner error:%s in the action:%s from instance of class:%s is fail.template -> %s.",
                                rc, ctx.getActionName(), pc.getFullClassName(), ctx.getTemplateFullName());
                        break;
                    }
                    case ActionResult.Json: {
                        if (!isAjaxRequest) {
                            isError = true;
                            AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                                    ctx.getHttpSessionId(), AlbianLoggerLevel.Error,
                                    "the result json is error not in ajax-action:%s from instance of class:%s.template -> %s.",
                                    ctx.getActionName(), pc.getFullClassName(), ctx.getTemplateFullName());
                            break;
                        }

                        String body = null;
                        Object rc = ar.getResult();
                        if (null == rc) {
                            body = "{}";
                        }

                        if (rc instanceof String) {
                            body = rc.toString();
                        } else {
                            body = JSON.toJSONString(rc, SerializerFeature.SkipTransientField,
                                    SerializerFeature.WriteDateUseDateFormat,
                                    SerializerFeature.DisableCircularReferenceDetect);

                        }
                        resp.setContentType("application/json");
                        resp.getOutputStream().write(body.getBytes());
                        return;
                    }
                    case ActionResult.Normal:
                    default: {
                        break;
                    }
                }

                page.kBeforeAction(pc);

                Map<String, ViewActionConfigurtion> actions = pc.getActions();
                if (null == actions) {
                    AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                            ctx.getHttpSessionId(), AlbianLoggerLevel.Error,
                            "there is not actions by class -> %s for template -> %s in the webapps.",
                            pc.getFullClassName(), ctx.getTemplateFullName());
                    isNotFound = true;
                    break;
                }
                ViewActionConfigurtion pac = actions.get(ctx.getActionName());
                if (null == pac) {
                    AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                            ctx.getHttpSessionId(), AlbianLoggerLevel.Error,
                            "there is not action attribute by class -> %s for template -> %s in the webapps.",
                            pc.getFullClassName(), ctx.getTemplateFullName());
                    isNotFound = true;
                    break;
                }
                if (pac.getHttpActionMethod() != ham) {
                    AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                            ctx.getHttpSessionId(), AlbianLoggerLevel.Error,

                            "http request action method is not require."
                                    + "template -> %s,class -> %s,action -> %s,function -> %s,request-method -> %s,preinstall method ->%s.",
                            ctx.getTemplateFullName(), pc.getFullClassName(), pac.getBindingName(), pac.getName(),
                            isPost ? "POST" : "Get", HttpActionMethod.Post == pac.getHttpActionMethod() ? "POST" : "GET");
                    isError = true;
                    break;
                }
                Method m = pac.getMethod();
                if (null == m) {
                    AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                            ctx.getHttpSessionId(), AlbianLoggerLevel.Error,
                            "there is not action by class -> %s for template -> %s in the webapps.",
                            pc.getFullClassName(), ctx.getTemplateFullName());
                    isNotFound = true;
                    break;
                }

                ActionResult result = null;
                try {
                    result = (ActionResult) m.invoke(page, ctx);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                            ctx.getHttpSessionId(), AlbianLoggerLevel.Error,

                            e, "call action:%s from instance of class:%s is fail.template -> %s.",
                            pac.getName(), pc.getFullClassName(), ctx.getTemplateFullName());
                    isError = true;
                    break;
                }
                switch (result.getResultType()) {
                    case ActionResult.Redirect: {
                        Object rc = result.getResult();
                        if (null == rc && !(rc instanceof String)) {
                            isError = true;
                            break;
                        }
                        resp.sendRedirect(rc.toString());
                        return;
                    }
                    case ActionResult.OutputStream: {
                        return;
                    }
                    case ActionResult.InnerError: {
                        isError = true;
                        Object rc = result.getResult();
                        AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                                ctx.getHttpSessionId(), AlbianLoggerLevel.Error,

                                "inner error:%s in the action:%s from instance of class:%s is fail.template -> %s.",
                                rc, pac.getName(), pc.getFullClassName(), ctx.getTemplateFullName());
                        break;
                    }
                    case ActionResult.Json: {
                        if (!isAjaxRequest) {
                            isError = true;
                            AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                                    ctx.getHttpSessionId(), AlbianLoggerLevel.Error,

                                    "the result json is error not in ajax-action:%s from instance of class:%s.template -> %s.",
                                    pac.getName(), pc.getFullClassName(), ctx.getTemplateFullName());
                            break;
                        }

                        String body = null;
                        Object rc = result.getResult();
                        if (null == rc) {
                            body = "{}";
                        }

                        if (rc instanceof String) {
                            body = rc.toString();
                        } else {
                            body = JSON.toJSONString(rc, SerializerFeature.SkipTransientField,
                                    SerializerFeature.WriteDateUseDateFormat,
                                    SerializerFeature.DisableCircularReferenceDetect);

                        }
                        resp.setContentType("application/json");
                        resp.getOutputStream().write(body.getBytes());
                        return;
                    }
                    case ActionResult.Normal:
                    default: {
                        break;
                    }
                }

                if (isError) {
                    break;
                }

                page.kAfterAction(pc);
                page.kBeforeRender();
                StringBuffer stringBuffer = page.render();
                StringBuffer html = page.kAfterRender(stringBuffer);

                resp.setContentType("text/html");
                resp.getOutputStream().write(html.toString().getBytes(c.getCharset()));

            } catch (Exception t) {
                String path = req.getRequestURI();
                AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                        sessionId, AlbianLoggerLevel.Error,
                        t, "request to url->%s with method:%s is fail.",
                        path, isPost ? "POST" : "GET");
                isError = true;
            }
        } while (false);
        if (isError) {
            if (isAjaxRequest) {
                resp.setContentType("application/json");
                try {
                    resp.getOutputStream().write("{\"Error\" : \"inner error\"}".getBytes());
                } catch (Exception e) {

                }
            } else {
                try {

                    resp.sendRedirect(c.getErrorViewConfigurtion().getTemplate());
                } catch (Exception e) {

                }
            }


            return;
        }
        if (isNotFound) {
            if (isAjaxRequest) {
                resp.setContentType("application/json");
                try {
                    resp.getOutputStream().write("{\"Error\" : \"not found\"}".getBytes());
                } catch (Exception e) {

                }
            } else {
                try {
                    resp.sendRedirect(c.getNotFoundViewConfigurtion().getTemplate());
                } catch (Exception e) {

                }
            }
        }
        return;
    }

    private void parserRequestUrl(HttpServletRequest req, HttpContext ctx, boolean isPost) {
        String path = req.getRequestURI();
        AlbianHttpConfigurtion c = ctx.getConfig();

        if (path.equals("/")) {
            path = c.getWelcomePage().getTemplate();
        }

        if (StringHelper.isNotBlank(c.getContextPath()) && !StringHelper.equals("/", c.getContextPath())) {
            path = path.substring(path.indexOf('/'), 2);
        }

        // url:/context-path/template-path/actionaname$template.shtm?k=v&k=v
        String templatePath = path.substring(0, path.lastIndexOf('/') + 1);
        String templateName = path.substring(path.lastIndexOf('/') + 1);
        String actionName = isPost ? "execute" : "load";

        if (templateName.contains("$")) {
            String[] strs = templateName.split("\\$");
            actionName = strs[0];
            templateName = strs[1];
        }

        ctx.setActionName(actionName);
        ctx.setTemplateName(templateName);
        ctx.setTemplatePath(templatePath);
        ctx.setTemplateFullName(Path.joinWithFilename(templateName, templatePath));

        String queryString = req.getQueryString();
        if (StringHelper.isBlank(queryString)) {
            return;
        }

        String[] paras = null;
        if (StringHelper.contains(queryString, "&")) {
            paras = queryString.split("&");
        } else {
            paras = new String[1];
            paras[0] = queryString;
        }

        Map<String, String> pMap = new HashMap<>();
        for (String para : paras) {
            if (!StringHelper.contains(para, "=")) {
                break;
            }
            String[] kv = para.split("=");
            if (0 == kv.length) continue;
            if (1 == kv.length) {
                pMap.put(kv[0], null);
            } else {
                pMap.put(kv[0], kv[1]);
            }
        }

        ctx.setParas(pMap);
    }

    private boolean isAjaxRequest(HttpServletRequest req) {
        return req.getHeader("X-Requested-With") != null || req.getParameter("X-Requested-With") != null;
    }

    private boolean isMultipartRequest(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent(request);
    }


}
