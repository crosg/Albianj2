package org.albianj.mvc.view;

import org.albianj.datetime.AlbianDateTimeHelper;
import org.albianj.except.AlbianExterException;
import org.albianj.except.ExceptionUtil;
import org.albianj.io.Path;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.mvc.*;
import org.albianj.mvc.config.AlbianHttpConfigurtion;
import org.albianj.mvc.config.FileUploadConfigurtion;
import org.albianj.mvc.config.ViewConfigurtion;
import org.albianj.mvc.config.ViewFieldConfigurtion;
import org.albianj.mvc.lang.ServerHelper;
import org.albianj.mvc.service.UploadFile;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by xuhaifeng on 16/12/15.
 */
public abstract class FreeView implements IView {

    @NotHttpFieldAttribute()
    protected String __MultiActionName;

    @NotHttpFieldAttribute()
    protected String __ActionArgs;

    protected String __AlbianSubmitClientToken;

    @NotHttpFieldAttribute()
    protected Map<String, ViewFieldConfigurtion> fields = null;

    @NotHttpFieldAttribute()
    protected HttpContext ctx = null;
    @NotHttpFieldAttribute()
    protected Map<String, Object> model = new HashMap<String, Object>();
    @NotHttpFieldAttribute()
    protected Map<String, UploadFile> fileItems = null;
    @NotHttpFieldAttribute()
    protected Map<String, String> jsBlocks = new LinkedHashMap<>();
    @NotHttpFieldAttribute()
    protected Map<String, String> jsLinks = new LinkedHashMap<>();
    @NotHttpFieldAttribute()
    protected Map<String, String> styleBlocks = new LinkedHashMap<>();
    @NotHttpFieldAttribute()
    protected Map<String, String> styleLinks = new LinkedHashMap<>();
    @NotHttpFieldAttribute()
    protected Map<String, String> headers = new LinkedHashMap<>();
    @NotHttpFieldAttribute()
    protected Map<String, Class<?>> functions = new HashMap<>();

    @NotHttpActionAttribute()
    public void kinit(HttpContext ctx) {
        this.ctx = ctx;
        model.put("CurrentContext", ctx);
        __MultiActionName = getAttributeValue("__AlbianActionName");
        __ActionArgs = getAttributeValue("__AlbianActionArgs");
        if (isMultiActions()) {
            openMultiActions();
        }

        if (isFormSubmitOnlyOnce() && !isMasterView()) {
            if (ctx.isPost()) { //check token when post only
                __AlbianSubmitClientToken = getAttributeValue("__AlbianSumbitToken");
                Object oToken = getSession("__AlbianSumbitToken");
                if (Validate.isNullOrEmptyOrAllSpace(__AlbianSubmitClientToken) || null == oToken) {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                            ctx.getHttpSessionId(), AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianMvf,
                            AlbianModuleType.AlbianMvf.getThrowInfo(),
                            "the same form submit again.server token:%s,submit token:%s.",
                            oToken, __AlbianSubmitClientToken);
                }
                if (!__AlbianSubmitClientToken.equals(oToken.toString())) {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                            ctx.getHttpSessionId(), AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianMvf,
                            AlbianModuleType.AlbianMvf.getThrowInfo(),
                            "the same form submit again.server token:%s,submit token:%s.",
                            oToken, __AlbianSubmitClientToken);
                }
            }

            String token = ServerHelper.generateToken(ctx.getHttpSessionId(), ctx.getCurrentRequest());
            addSession("__AlbianSumbitToken", token);
            __AlbianSubmitClientToken = token;
            openSubmitOnlyOnce(token);
        }
    }

    @Override
    @NotHttpActionAttribute()
    public String getViewTitle() {
        return null;
    }

    @NotHttpActionAttribute()
    public String getCurrentActionName() {
        return this.__MultiActionName;
    }

    @NotHttpActionAttribute()
    public String getCurrentActionArgs() {
        return this.__ActionArgs;
    }

    @Override
    @NotHttpActionAttribute()
    public ActionResult interceptor(HttpContext ctx) {
        return ActionResult.Default;
    }


    @Override
    @HttpActionAttribute(Method = HttpActionMethod.Get)
    public ActionResult load(HttpContext ctx) {
        return ActionResult.Default;
    }

    @HttpActionAttribute(Method = HttpActionMethod.Post)
    public ActionResult execute(HttpContext ctx) {
        return ActionResult.Default;
    }

    @NotHttpActionAttribute()
    public Map<String, UploadFile> getFileItems() {
        return this.fileItems;
    }

    @NotHttpActionAttribute()
    public void setFileItems(Map<String, UploadFile> fileItems) {
        this.fileItems = fileItems;
    }

    @NotHttpActionAttribute()
    public UploadFile getUploadFile(String fieldName) {
        if (null == fileItems) return null;
        if (Validate.isNullOrEmptyOrAllSpace(fieldName)) return null;
        return this.fileItems.get(fieldName);
    }

    @NotHttpActionAttribute()
    public Map<String, String> writeUploadFile() throws IOException {
        AlbianHttpConfigurtion c = ctx.getHttpConfigurtion();
        Map<String, UploadFile> map = this.fileItems;
        if (Validate.isNullOrEmpty(map)) return null;
        Map<String, String> files = new HashMap<>();
        FileUploadConfigurtion fuc = c.getFileUploadConfigurtion();
        String folder = fuc.getFolder();
        for (Map.Entry<String, UploadFile> e : map.entrySet()) {
            UploadFile uf = e.getValue();
            String clientFilename = uf.getClientFileName();
            byte[] data = uf.getData();
            String ext = FilenameUtils.getExtension(clientFilename);
            String webFilename = Path.joinWithFilename(AlbianServiceRouter.getLogIdService().generate32UUID() + "." + ext, folder);
            String filename = Path.joinWithFilename(webFilename, c.getRootPath());
            File f = new File(filename);
            FileUtils.writeByteArrayToFile(f, data);
            files.put(uf.getFieldName(), webFilename);
        }
        return files;
    }

    @Override
    @NotHttpActionAttribute()
    public void bindingAttribute(String name, Object value) {
        this.model.put(name, value);
    }

    @NotHttpActionAttribute()
    public Map<String, Object> getModel() {
        return this.model;
    }

    @NotHttpActionAttribute()
    public Map<String, Class<?>> getFunctions() {
        return this.functions;
    }

    @NotHttpActionAttribute()
    protected void sendRedirect(String url) throws IOException {
        ctx.getCurrentResponse().sendRedirect(url);
    }

    @NotHttpActionAttribute()
    protected void sendRedirect(Class<? extends View> view, String... paras) throws IOException {
        AlbianHttpConfigurtion c = ctx.getHttpConfigurtion();
        Map<String, ViewConfigurtion> map = c.getPages();
        if (!Validate.isNullOrEmpty(map)) {
            throw new RuntimeException("not found the view.");
        }

        ViewConfigurtion vc = map.get(view.getName());
        if (null == vc) {
            throw new RuntimeException("not found the view.");
        }

        String template = vc.getTemplate();
        StringBuffer sb = new StringBuffer();
        if (null != paras) {
            for (String s : paras) {
                sb.append(s).append("&");
            }
        }
        if (0 != sb.length()) {
            sb.insert(0, '?');
            sb.insert(0, template);
        } else {
            sb.append(template);
        }

        sendRedirect(sb.toString());
    }

    @NotHttpActionAttribute()
    public String getAttributeValue(String name) {
        Map<String, String> map = ctx.getAttributes();
        if (null != map) {
            String v = map.get(name);
            if (null != v) return v;
        }
        String v = (String) ctx.getCurrentRequest().getAttribute(name);
        if (null != v) return v;
        v = ctx.getCurrentRequest().getParameter(name);
        return v;
    }

    @NotHttpActionAttribute()
    public Object toBoxValue(Class<?> cls, Object o) throws Exception {
        if(cls.equals(String.class)) {
            return o.toString();
        } else if(cls.equals(BigDecimal.class)) {
            BigDecimal bd = new BigDecimal(o.toString());
            return bd;
        } else if(cls.equals(boolean.class) || cls.equals(Boolean.class)) {
            if(isNumeric(o.toString())){
                return 0 != new BigDecimal(o.toString()).longValue();
            }
            return Boolean.parseBoolean(o.toString());
        } else if(cls.equals(int.class) || cls.equals(Integer.class)) {
            return Integer.parseInt(o.toString());
        } else if(cls.equals(long.class) || cls.equals(Long.class)) {
            return Long.parseLong(o.toString());
        } else if(cls.equals(BigInteger.class)) {
            BigInteger bi = new BigInteger(o.toString());
            return bi;
        } else if(cls.equals(float.class) || cls.equals(Float.class)) {
            return Float.parseFloat(o.toString());
        } else if (cls.equals(double.class) || cls.equals(Double.class)) {
            return Double.parseDouble(o.toString());
        } else if(cls.equals(java.util.Date.class)) {
            Date d = null;
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                d = dateFormat.parse(o.toString());
            } catch (Exception e) {
                d = null;
            }
            if (null == d) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    d = dateFormat.parse(o.toString());
                } catch (Exception e) {

                }
            }
            return d;
        } else if(cls.equals(java.sql.Date.class)) {
            return AlbianDateTimeHelper.valueOfSqlDate(o.toString());
        } else if(cls.equals(java.sql.Time.class)){
            return java.sql.Time.valueOf(o.toString());
        } else if(cls.equals(java.sql.Timestamp.class)) {
            long ts = AlbianDateTimeHelper.toTimeMillis(o.toString());
            if(0 == ts) {
                throw new AlbianExterException(ExceptionUtil.ExceptForError,
                        "Cast fail.",
                        "cast string -> ",o.toString()," to sql.Timestamp fail.");
            }
            return new java.sql.Timestamp(ts);
        }
        return o.toString();
    }

    public static boolean isNumeric(String str) {
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }
        return true;
    }

    @NotHttpActionAttribute()
    protected void addModel(String key, Object v) {
        this.model.put(key, v);
    }

    @NotHttpActionAttribute()
    protected void vbinding(String key, Object v) {
        this.model.put(key, v);
    }

    @NotHttpActionAttribute()
    protected void fbinding(String namespace, Class<?> cla) {
        this.functions.put(namespace, cla);
    }

    @NotHttpActionAttribute()
    public void addJavaScriptBlockContent(String id, String script) {
        jsBlocks.put(id, script);
    }

    @NotHttpActionAttribute()
    public void removeJavaScriptBlock(String id) {
        this.jsBlocks.remove(id);
    }

    @NotHttpActionAttribute()
    public void addJavaScriptLink(String id, String link) {
        this.jsLinks.put(id, new StringBuffer("<script language=\"JavaScript\" type=\"text/javascript\" src=\"")
                .append(link).append("\" />").toString());
    }

    @NotHttpActionAttribute()
    public void removeJavaScriptLink(String id) {
        this.jsLinks.remove(id);
    }

    @NotHttpActionAttribute()
    public void addStyleBlock(String id, String content) {
        this.styleBlocks.put(id, content);
    }

    @NotHttpActionAttribute()
    public void removeStyleBlock(String id) {
        this.styleBlocks.remove(id);
    }

    @NotHttpActionAttribute()
    public void addStyleLink(String id, String link) {
        this.styleLinks.put(id, new StringBuffer("<link rel=\"stylesheet\" href=\"").append(link).append("\" />").toString());
    }

    @NotHttpActionAttribute()
    public void removeStyleLink(String id) {
        this.styleLinks.remove(id);
    }

    @NotHttpActionAttribute()
    public void addHeader(String id, String header) {
        this.headers.put(id, header);
    }

    @NotHttpActionAttribute()
    public void removeHeader(String id) {
        this.headers.remove(id);
    }

    @NotHttpActionAttribute()
    public Map<String, String> getJavaScriptBlocks() {
        return this.jsBlocks;
    }

    @NotHttpActionAttribute()
    public Map<String, String> getJavaScriptLinks() {
        return this.jsLinks;
    }

    @NotHttpActionAttribute()
    public Map<String, String> getStyleBlocks() {
        return this.styleBlocks;
    }

    @NotHttpActionAttribute()
    public Map<String, String> getStyleLinks() {
        return this.styleLinks;
    }

    @NotHttpActionAttribute()
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @NotHttpActionAttribute()
    public StringBuffer headersToHtml() {
        StringBuffer sb = new StringBuffer();
        for (String h : this.headers.values()) {
            sb.append(h);
        }
        return sb;
    }

    @NotHttpActionAttribute()
    public StringBuffer styleBlocksToHtml() {
        StringBuffer sb = new StringBuffer("<style type=\"text/css\">");
        for (String h : this.styleBlocks.values()) {
            sb.append(h);
        }
        sb.append("</style>");
        return sb;
    }

    @NotHttpActionAttribute()
    public StringBuffer styleLinksToHtml() {
        StringBuffer sb = new StringBuffer();
        for (String h : this.styleLinks.values()) {
            sb.append(h);
        }
        return sb;
    }

    @NotHttpActionAttribute()
    public void addSession(String key, Object v) {
        HttpServletRequest req = this.ctx.getCurrentRequest();
        req.getSession().setAttribute(key, v);
    }

    @NotHttpActionAttribute()
    public Object getSession(String key) {
        HttpServletRequest req = this.ctx.getCurrentRequest();
        return req.getSession().getAttribute(key);
    }

    @NotHttpActionAttribute()
    public void removeSession(String key) {
        HttpServletRequest req = this.ctx.getCurrentRequest();
        req.getSession().removeAttribute(key);
    }

    @NotHttpActionAttribute()
    public boolean isMultiActions() {
        return false;
    }

    @NotHttpActionAttribute()
    public boolean isFormSubmitOnlyOnce() {
        return false;
    }

    @NotHttpActionAttribute()
    public boolean isMasterView() {
        return false;
    }

    @NotHttpActionAttribute()
    public void openMultiActions() {
        String header = "<meta name=\"multi-action\" content=\"true\" />";
        addHeader("MultiActions", header);
        StringBuffer sb = new StringBuffer();
        sb.append("function __doPost(formId,actionName,actionArgs) {\n")
                .append("    var theform = document.getElementById(formId);\n")
                .append("    var input =document.createElement(\"input\");\n")
                .append("    input.setAttribute(\"type\",\"hidden\");\n")
                .append("    input.setAttribute(\"id\",\"__AlbianActionName\");\n")
                .append("    input.setAttribute(\"name\",\"__AlbianActionName\");\n")
                .append("    input.setAttribute(\"value\",actionName);\n")
                .append("    theform.appendChild(input);\n")
                .append("    if(actionArgs) {\n")
                .append("       var args =document.createElement(\"input\");\n")
                .append("       args.setAttribute(\"type\",\"hidden\");\n")
                .append("       args.setAttribute(\"id\",\"__AlbianActionArgs\");\n")
                .append("       args.setAttribute(\"name\",\"__AlbianActionArgs\");\n")
                .append("       args.setAttribute(\"value\",actionArgs);\n")
                .append("       theform.appendChild(args);\n")
                .append("    }\n")
                .append("    theform.submit();\n")
                .append("}");

        addJavaScriptBlockContent("__doPost", sb.toString());
    }

    @NotHttpActionAttribute()
    public void openSubmitOnlyOnce(String token) {
        StringBuffer sb = new StringBuffer();
        sb.append("var forms = document.forms; \n")
                .append("if(0 == forms.length) {")
                .append("    var input =document.createElement(\"input\");\n")
                .append("    input.setAttribute(\"type\",\"hidden\");\n")
                .append("    input.setAttribute(\"id\",\"__AlbianSumbitToken\");\n")
                .append("    input.setAttribute(\"name\",\"__AlbianSumbitToken\");\n")
                .append("    input.setAttribute(\"value\",'").append(token).append("');\n")
                .append("    document.body.appendChild(input);\n")
                .append("    } else { \n")
                .append("for(var i = 0; i < forms.length; i++) { \n")
                .append("   var f = forms[i];\n")
                .append("    var input =document.createElement(\"input\");\n")
                .append("    input.setAttribute(\"type\",\"hidden\");\n")
                .append("    input.setAttribute(\"id\",\"__AlbianSumbitToken\");\n")
                .append("    input.setAttribute(\"name\",\"__AlbianSumbitToken\");\n")
                .append("    input.setAttribute(\"value\",'").append(token).append("');\n")
                .append("    f.appendChild(input);\n")
                .append("} }\n");

        addJavaScriptBlockContent("submit_only_once", sb.toString());
    }
}
