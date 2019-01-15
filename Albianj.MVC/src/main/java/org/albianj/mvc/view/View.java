package org.albianj.mvc.view;

import org.albianj.io.Path;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.mvc.HttpContext;
import org.albianj.mvc.NotHttpActionAttribute;
import org.albianj.mvc.NotHttpFieldAttribute;
import org.albianj.mvc.config.ViewConfigurtion;
import org.albianj.mvc.config.ViewFieldConfigurtion;
import org.albianj.mvc.lang.HttpHelper;
import org.albianj.mvc.service.IAlbianFileUploadService;
import org.albianj.mvc.service.IAlbianTemplateService;
import org.albianj.mvc.service.TemplateException;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.text.StringHelper;
import org.albianj.verify.Validate;
import org.apache.commons.fileupload.FileUploadException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public abstract class View extends FreeView {

    @NotHttpFieldAttribute()
    protected static final String ContentPlaceHolder = "$ALBIAN_SCREEN_CONTENT";

    @Override
    @NotHttpActionAttribute()
    public void kinit(HttpContext ctx) {
        super.kinit(ctx);
    }

    @Override
    @NotHttpActionAttribute()
    public void kBeforeAction(ViewConfigurtion pc) {
        if (ctx.isMultipartRequest()) {
            IAlbianFileUploadService fus = AlbianServiceRouter.getSingletonService(
                    IAlbianFileUploadService.class,
                    IAlbianFileUploadService.Name);
            try {
                fus.parseRequest(ctx);
                this.setFileItems(ctx.getFileItems());// for cleanup
            } catch (FileUploadException | IOException e) {
                // TODO Auto-generated catch block
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                        ctx.getHttpSessionId(), AlbianLoggerLevel.Error, e,
                        "new instance by the mapping class:%s is fail.template -> %s.",
                        pc.getFullClassName(), ctx.getTemplateFullName());
            }
        }

        fields = pc.getFields();
        try {
            if (pc.isAutoBinding() && null != fields) {
                for (Map.Entry<String, ViewFieldConfigurtion> kv : fields.entrySet()) {
                    ViewFieldConfigurtion f = kv.getValue();
                    String value = getAttributeValue(f.getBindingName());
                    if (StringHelper.isNotBlank(value)) {
                        Class<?> type = f.getType();
                        Object realValue = null;

                        realValue = toBoxValue(type, value);
                        if (realValue instanceof String) {
                            realValue = HttpHelper.escapeHtmlString(realValue.toString());
                        }

                        // 这里可能需要类型转换
                        f.getField().set(this, realValue);
                    }
                }
            }
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    ctx.getHttpSessionId(), AlbianLoggerLevel.Error, e,
                    "box value from request to Object and then for class:%s is fail.template -> %s.",
                    pc.getFullClassName(), ctx.getTemplateFullName());

        }
    }

    @Override
    @NotHttpActionAttribute()
    public void kAfterAction(ViewConfigurtion pc) {
        try {
            if (pc.isAutoBinding() && null != fields) {
                for (Map.Entry<String, ViewFieldConfigurtion> kv : fields.entrySet()) {
                    ViewFieldConfigurtion f = kv.getValue();
                    Object value = null;
                    value = f.getField().get(this);
                    if (null != value) {
                        this.bindingAttribute(f.getBindingName(), value);
                    }
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    ctx.getHttpSessionId(), AlbianLoggerLevel.Error, e,
                    "auto mapping fields to response by class  -> %s is fail.from template -> %s.",
                    pc.getFullClassName(), ctx.getTemplateFullName());
        }
    }

    @Override
    @NotHttpActionAttribute()
    public void kBeforeRender() {
        //normal view need render header
        String title = getViewTitle();
        if (!Validate.isNullOrEmptyOrAllSpace(title)) {
            this.model.put("Albian_View_Title", title);
        }

        this.model.put("Albian_View_Style_Content", styleBlocksToHtml());
        this.model.put("Albian_View_Style_Link", styleLinksToHtml());
        this.model.put("Albian_View_Style_Header", headersToHtml());
    }

    @Override
    @NotHttpActionAttribute()
    public StringBuffer render() {

        IAlbianTemplateService ats = AlbianServiceRouter.getSingletonService(IAlbianTemplateService.class,
                IAlbianTemplateService.Name);
        Map<String, Object> model = getModel();
        this.fbinding("HttpHelper", HttpHelper.class);
        model.put("CurrentContext", ctx);
        model.put("Header", headers);
        StringWriter sw = new StringWriter();
        try {
            String templateFullname = Path.joinWithFilename(ctx.getTemplateFullName(), ctx.getHttpConfigurtion().getRootPath());
            if (!Path.isExist(templateFullname)) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                        ctx.getHttpSessionId(), AlbianLoggerLevel.Error,
                        "not found the template -> %s.",
                        ctx.getTemplateFullName());

            }
            ats.renderTemplate(ctx.getTemplateFullName(), model, this.getFunctions(), sw);
        } catch (IOException | TemplateException e) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    ctx.getHttpSessionId(), AlbianLoggerLevel.Error,
                    "render the template -> %s is fail.",
                    ctx.getTemplateFullName());

        }

        StringBuffer sb = new StringBuffer(sw.toString());
        return sb;
    }

    @Override
    @NotHttpActionAttribute()
    public StringBuffer kAfterRender(StringBuffer contextBuffer) {
        StringBuffer sb = new StringBuffer();
        if (0 != this.jsLinks.size()) {
            for (String s : this.jsLinks.values()) {
                sb.append(s);
            }
        }

        if (0 != jsBlocks.size()) {
            sb.append("<script language=\"JavaScript\" type=\"text/javascript\">");
            for (String s : this.jsBlocks.values()) {
                sb.append(s);
            }
            sb.append("</script>");
        }

        if (0 != sb.length()) {
            int offset = contextBuffer.lastIndexOf("</body>");
            contextBuffer.insert(offset, sb);
        }
        return contextBuffer;
    }


}
