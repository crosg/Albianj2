package org.albianj.mvc;

import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.service.AlbianServiceRouter;

/**
 * Created by xuhaifeng on 16/12/15.
 */
public class CView extends View {

    @NotHttpActionAttribute()
    public String getSubTitle() {
        return this.getClass().getSimpleName();
    }

    @Override
    @NotHttpActionAttribute()
    public void kinit(HttpContext ctx) {
        super.kinit(ctx);
        ctx.setView(this);

        model.put("Albian_SubTitle", getSubTitle());
    }


    @NotHttpActionAttribute()
    public void kBeforeRender() {

    }


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

        StringBuffer html = ctx.getMasterViewHtml();
        if (null == html) {
//            this.getSession()
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    ctx.getHttpSessionId(), AlbianLoggerLevel.Error,
                    "can not get the master-view html,maybe you need add MasterViewTag in the content-view."
            );
//            AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,
//                    "can not get the master-view html,maybe you need add MasterViewTag in the content-view."
//                    );
            return null;
        }
        int idx = html.indexOf(ContentPlaceHolder);
        html.delete(idx, idx + ContentPlaceHolder.length());
        html.insert(idx, contextBuffer);
        if (0 != sb.length()) {
            int offset = html.lastIndexOf("</body>");
            html.insert(offset, sb);
        }
        return html;
    }


}

