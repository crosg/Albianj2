package org.albianj.mvc.web;

import Albian.Core.Service.AlbianServiceHub;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.albianj.datetime.AlbianDateTime;
import org.albianj.except.AlbianDisplayableException;
import org.albianj.except.ExceptionUtil;
import org.albianj.io.Path;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.mvc.ActionResult;
import org.albianj.mvc.HttpActionMethod;
import org.albianj.mvc.HttpContext;
import org.albianj.mvc.config.AlbianHttpConfigurtion;
import org.albianj.mvc.config.ViewActionConfigurtion;
import org.albianj.mvc.config.ViewConfigurtion;
import org.albianj.mvc.server.IServerLifeCycle;
import org.albianj.mvc.service.IAlbianBrushingService;
import org.albianj.mvc.service.IAlbianMVCConfigurtionService;
import org.albianj.mvc.service.IAlbianResourceService;
import org.albianj.mvc.view.View;
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

public class AlbianMVCServlet  extends HttpServlet {

    @Override
    public void init() throws ServletException {
        try {
            IAlbianMVCConfigurtionService albianMVCConfigurtionService = AlbianServiceRouter.getSingletonService(IAlbianMVCConfigurtionService.class,
                    IAlbianMVCConfigurtionService.Name);
            IServerLifeCycle server = albianMVCConfigurtionService.getHttpConfigurtion().getServerLifeCycle();
            if(null != server){
                server.ServerStartup(albianMVCConfigurtionService.getHttpConfigurtion());
            }
        }catch (Exception e){
            AlbianServiceRouter.throwException("WebStartup", IAlbianLoggerService2.AlbianRunningLoggerName,
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

    protected void handleRequest(HttpServletRequest req,HttpServletResponse resp,boolean isPost){
        boolean isAjaxRequest = false;
        String sessionId = null;
        AlbianHttpConfigurtion c = null;
        HttpContext ctx = null;
        try {
            IAlbianMVCConfigurtionService albianMVCConfigurtionService = AlbianServiceRouter.getSingletonService(IAlbianMVCConfigurtionService.class,
                    IAlbianMVCConfigurtionService.Name);

            c = albianMVCConfigurtionService.getHttpConfigurtion();
            resp.setCharacterEncoding(c.getCharset());
            req.setCharacterEncoding(c.getCharset());
            ctx = new HttpContext(isPost, req, resp,
                    this.getServletContext(),
                    req.getSession(),
                    c);
            sessionId = ctx.getHttpSessionId();

//            IAlbianBrushingService abs = AlbianServiceRouter.getSingletonService(IAlbianBrushingService.class, IAlbianBrushingService.Name);
//            if (null != abs && !abs.consume(req)) {
//                throw new AlbianDisplayableException(ExceptionUtil.ExceptForWarn,
//                        "Brushing Fail.",
//                        String.format("this req with session -> %s brushing fail. url -> %s",
//                                sessionId,ctx.getCurrentUrl()));
//            }

            IAlbianResourceService ars = AlbianServiceRouter.getSingletonService(IAlbianResourceService.class, IAlbianResourceService.Name);
            if (ars.isResourceRequest(ctx)) {
                try {
                    ars.renderResource(ctx);
                    return;
                } catch (IOException e) {
                    throw new AlbianDisplayableException(ExceptionUtil.ExceptForWarn,
                            "Resource render Fail.",
                            String.format("this req with session -> %s url -> %s render fail.",
                                    sessionId,ctx.getCurrentUrl()),e);
                }
            }

            AlbianServiceRouter.addLog(sessionId,
                    IAlbianLoggerService2.AlbianRunningLoggerName,
                    AlbianLoggerLevel.Info,
                    String.format("%s req -> %s | IP -> %s | time -> %s | paras -> %s | url -> %s",
                    isPost ? "POST" : "GET",
                    req.getRequestURI().toString(), req.getRemoteAddr(),
                    AlbianDateTime.getDateTimeString(), req.getQueryString(),
                            ctx.getCurrentUrl()));

            HttpActionMethod ham = isPost ? HttpActionMethod.Post : HttpActionMethod.Get;

            parserRequestUrl(req, ctx, isPost);

            isAjaxRequest = isAjaxRequest(req);
            ctx.setAjaxRequest(isAjaxRequest);

            boolean isMultipartRequest = isMultipartRequest(req);
            ctx.setMultipartRequest(isMultipartRequest);

            Map<String, ViewConfigurtion> templates = c.getTemplates();
            if (null == templates) {
                throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                        "Templates resource Fail.",
                        String.format("templates is null in the web,no config resource.session -> %s, req -> %s.",
                                sessionId,ctx.getCurrentUrl()));
            }

            ViewConfigurtion pc = null;
            if(ctx.isWelcomeViewRequest()) {
                pc = c.getWelcomePage();
            } else {
                pc = templates.get(ctx.getTemplateFullName());
            }

            if (null == pc) {
                throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                        "Template Not Found.",
                        String.format("template -> %s is not found, session -> %s req -> %s.",
                                ctx.getTemplateFullName(), sessionId,ctx.getCurrentUrl()));
            }

            ctx.setPageConfigurtion(pc);
            Class<?> cla = pc.getRealClass();
            if (!View.class.isAssignableFrom(cla)) {
                throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                        "Behind class inherited error.",
                        String.format("template -> %s with behind class -> %s is not extends from View , session -> %s req -> %s.",
                                 ctx.getTemplateFullName(),pc.getFullClassName(), sessionId,ctx.getCurrentUrl()));
            }

            View page = null;
            try {
                page = pc.getRealClass().newInstance();
                page.kinit(ctx);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                        "Init View Object fail.",
                        String.format("new or init template -> %s with behind class -> %s is fail. session -> %s req -> %s.",
                                ctx.getTemplateFullName(), pc.getFullClassName(), sessionId,ctx.getCurrentUrl()));
            }

            if (page.isMultiActions()) {
                String actionName = page.getCurrentActionName();
                if (Validate.isNullOrEmptyOrAllSpace(actionName)) {
                    throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                            "Action Not Exist.",
                            String.format("action in template -> %s with behind class -> %s  is null or empty. session -> %s req -> %s.",
                                    ctx.getTemplateFullName(), pc.getFullClassName(), sessionId,ctx.getCurrentUrl()));
                } else {
                    ctx.setActionName(actionName);
                }
            }

            ActionResult ar = page.interceptor(ctx);

            if(!execResult(ar,ctx,pc)) {
                throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                        "Interceptor action error.",
                        String.format("exec interceptor action in template -> %s with behind class -> %s  is fail. session -> %s req -> %s.",
                                ctx.getTemplateFullName(), pc.getFullClassName(), sessionId,ctx.getCurrentUrl()));
            }

            page.kBeforeAction(pc);

            Map<String, ViewActionConfigurtion> actions = pc.getActions();
            if (null == actions) {
                throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                        "Not found view's actions.",
                        String.format("template -> %s with behind class -> %s  actions is null. session -> %s req -> %s.",
                                ctx.getTemplateFullName(), pc.getFullClassName(), sessionId,ctx.getCurrentUrl()));
            }
            ViewActionConfigurtion pac = actions.get(ctx.getActionName());
            if (null == pac) {
                throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                        "Not found action.",
                        String.format("template -> %s with behind class -> %s not found action -> %s. session -> %s req -> %s.",
                                ctx.getTemplateFullName(), pc.getFullClassName(), ctx.getActionName(),
                                sessionId,ctx.getCurrentUrl()));
            }
            if (pac.getHttpActionMethod() != ham) {
                throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                        "Action method  error.",
                        String.format("template -> %s with behind class -> %s,session -> %s req -> %s." +
                                        "action -> %s method is error.query action method -> %s,action method -> %s",
                                ctx.getTemplateFullName(), pc.getFullClassName(), sessionId,ctx.getCurrentUrl(),
                                pac.getName(),isPost ? "POST" : "Get",HttpActionMethod.Post == pac.getHttpActionMethod() ? "POST" : "GET"));
            }
            Method m = pac.getMethod();
            if (null == m) {
                throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                        "action function error.",
                        String.format("template -> %s with behind class -> %s  is fail,session -> %s req -> %s." +
                                        "action -> %s function is null.",
                                ctx.getTemplateFullName(), pc.getFullClassName(), sessionId,ctx.getCurrentUrl(),
                                pac.getName()));
            }

            ActionResult result = null;
            try {
                result = (ActionResult) m.invoke(page, ctx);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                        "Interceptor action error.",
                        String.format("template -> %s with behind class -> %s  is fail,session -> %s req -> %s." +
                                        "exec action -> %s function is fail.",
                                ctx.getTemplateFullName(), pc.getFullClassName(), sessionId,ctx.getCurrentUrl(),
                                pac.getName()),e);
            }

            if(!execResult(result,ctx,pc)){
                throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                        "exec action error.",
                        String.format("exec action ->%s in template -> %s with behind class -> %s  is fail. session -> %s req -> %s.",
                                pac.getName(), ctx.getTemplateFullName(), pc.getFullClassName(), sessionId,ctx.getCurrentUrl()));
            }
        }catch (Exception e){
            try {
                resp.setContentType("application/text");
                resp.getOutputStream().write(e.getMessage().getBytes(ctx.getConfig().getCharset()));
            }catch (Exception ie){

            }
        }
    }

    /**
     * 根据request请求full url分析该url对应的template的路径,参数,action等等信息
     * 此函数兼容welcome page的访问
     * url的格式定义 /context-path/template-path/actionaname$template.shtm?k=v&k=v
     * @param req
     * @param ctx
     * @param isPost
     */
    private void parserRequestUrl(HttpServletRequest req, HttpContext ctx, boolean isPost) {
        String fullUrl = req.getRequestURI();
        AlbianHttpConfigurtion c = ctx.getConfig();
        String templatePath = null;
        String contextPath = c.getContextPath();

        //input welcome page with simple by expl:www.expl.com or www.expl.com/
        if (Validate.isNullOrEmptyOrAllSpace(fullUrl) ||  fullUrl.equals(contextPath)) {
            fullUrl = c.getWelcomePage().getTemplate();
        }

        // url = /context-path/template-path/actionaname$template.shtm?k=v&k=v
        // 1. deal query string
        String url  = null;
        String queryString = null;
        if(fullUrl.contains("?")) {
            String strs[] = StringHelper.split(fullUrl,"?");
            url = strs[0];
            if(2 == strs.length) {
                queryString = strs[1];
            }
        } else {
            url = fullUrl;
        }

        // url = /context-path/template-path/actionaname$template.shtm
        // 2. deal context-path
        if(!c.getContextPath().endsWith("/") && url.startsWith(contextPath)) {
            url = url.substring(contextPath.length() - 1);
        }

        if(!url.startsWith("/")) {
            url += "/";
        }

        // url = /template-path/actionaname$template.shtm
        // 3. deal action and view

        String actionName = isPost ? "execute" : "load";
        int pos = url.lastIndexOf("/");
        String pathSimpleName = url.substring(pos + 1); //actionaname$template.shtm
        templatePath = url.substring(0,pos); // /template-path
        String fileSimpleName = pathSimpleName; // if no action,fileSimpleName is template.shtm

        if(pathSimpleName.contains("$")) {
            String strs[] = pathSimpleName.split("$");
            actionName = strs[0];
            fileSimpleName = strs[1];
        }

        // 4. deal query string to map
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


        ctx.setActionName(actionName);
        ctx.setTemplateName(fileSimpleName);
        ctx.setTemplatePath(templatePath);
        ctx.setTemplateFullName(Path.joinWithFilename(fileSimpleName, templatePath));
        ctx.setParas(pMap);

//        if (StringHelper.isNotBlank(c.getContextPath()) && !StringHelper.equals("/", c.getContextPath())) {
//            int pos = 0;
//            if(0 > (pos = path.indexOf('/'))) {
//                throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
//                        "Request Url Fail.",
//                        String.format("Request url -> %s have no contextPath"),
//                        )
//            }
//            path = path.substring(path.indexOf('/'), 2);
//        }
//
//        // url:/context-path/template-path/actionaname$template.shtm?k=v&k=v
//        String templatePath = path.substring(0, path.lastIndexOf('/') + 1);
//        String templateName = path.substring(path.lastIndexOf('/') + 1);
//        String actionName = isPost ? "execute" : "load";
//
//        if (templateName.contains("$")) {
//            String[] strs = templateName.split("\\$");
//            actionName = strs[0];
//            templateName = strs[1];
//        }
//
//        ctx.setActionName(actionName);
//        ctx.setTemplateName(templateName);
//        ctx.setTemplatePath(templatePath);
//        ctx.setTemplateFullName(Path.joinWithFilename(templateName, templatePath));
//
//        String queryString = req.getQueryString();
//        if (StringHelper.isBlank(queryString)) {
//            return;
//        }
//
//        String[] paras = null;
//        if (StringHelper.contains(queryString, "&")) {
//            paras = queryString.split("&");
//        } else {
//            paras = new String[1];
//            paras[0] = queryString;
//        }
//
//        Map<String, String> pMap = new HashMap<>();
//        for (String para : paras) {
//            if (!StringHelper.contains(para, "=")) {
//                break;
//            }
//            String[] kv = para.split("=");
//            if (0 == kv.length) continue;
//            if (1 == kv.length) {
//                pMap.put(kv[0], null);
//            } else {
//                pMap.put(kv[0], kv[1]);
//            }
//        }
//
//        ctx.setParas(pMap);
    }

    private boolean isAjaxRequest(HttpServletRequest req) {
        return req.getHeader("X-Requested-With") != null || req.getParameter("X-Requested-With") != null;
    }

    private boolean isMultipartRequest(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent(request);
    }

    /**
     * 根据interceptor或者action被执行后返回的返回值进行执行相应的操作
     * @param ar
     * @param hc
     * @param pc
     * @return 如果返回true,则调用此函数的上一层函数继续执行,
     *          如果返回false,则调用此函数的上一层函数终止执行
     */
    private boolean execResult(ActionResult ar,HttpContext hc,ViewConfigurtion pc) throws AlbianDisplayableException{
        switch (ar.getResultType()) {
            case ActionResult.Redirect: {
                Object rc = ar.getResult();
                if (null == rc && !(rc instanceof String)) {
                    throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                            "Redirect url format error.",
                            String.format("Redirect url in template -> %s with behind class -> %s  is format error. session -> %s req -> %s.",
                                    hc.getTemplateFullName(), pc.getFullClassName(), hc.getHttpSessionId(),hc.getCurrentUrl()));
                }
                try {
                    hc.getCurrentResponse().sendRedirect(rc.toString());
                    return true;
                }catch (Exception e){
                    throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                            "Redirect error.",
                            String.format("Redirect url -> %s error in template -> %s with behind class -> %s. session -> %s req -> %s.",
                                    rc.toString(),hc.getTemplateFullName(),
                                    pc.getFullClassName(), hc.getHttpSessionId(),hc.getCurrentUrl()),e);
                }
            }
            case ActionResult.OutputStream: {
                return true;
            }
            case ActionResult.InnerError: {
                Object rc = ar.getResult();
                throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                        "Inner Error.",
                        String.format("template -> %s with behind class -> %s  is inner error -> %s. session -> %s req -> %s.",
                                hc.getTemplateFullName(), pc.getFullClassName(), rc,
                                hc.getHttpSessionId(),hc.getCurrentUrl()));

            }
            case ActionResult.Json: {
                if (!hc.isAjaxRequest()) {
                    throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                            "Result output Error.",
                            String.format("template -> %s with behind class -> %s  is result output error:json just only for ajax. session -> %s req -> %s.",
                                    hc.getTemplateFullName(), pc.getFullClassName(),
                                    hc.getHttpSessionId(),hc.getCurrentUrl()));

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
                hc.getCurrentResponse().setContentType("application/json");
                try {
                    hc.getCurrentResponse().getOutputStream().write(body.getBytes());
                    return true;
                }catch (Exception e){
                    throw new AlbianDisplayableException(ExceptionUtil.ExceptForError,
                            "output json error.",
                            String.format("template -> %s with behind class -> %s output json -> %s fail. session -> %s req -> %s.",
                                    hc.getTemplateFullName(), pc.getFullClassName(),body,
                                    hc.getHttpSessionId(),hc.getCurrentUrl()),e);
                }
            }
            case ActionResult.Normal:
            default: {
                return true;
            }
        }
    }

}
