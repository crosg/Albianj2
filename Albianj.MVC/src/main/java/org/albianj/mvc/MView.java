package org.albianj.mvc;

import org.albianj.verify.Validate;

import java.util.Map;

/**
 * Created by xuhaifeng on 16/12/14.
 */
public class MView extends View {
//
//    @NotHttpActionAttribute()
//    public int getType(){
//        return Master;
//    }

    @NotHttpActionAttribute()
    public void kBeforeRender() {
        CView vc = (CView) this.ctx.getView();
        String title = Validate.isNullOrEmptyOrAllSpace(vc.getViewTitle()) ? getViewTitle() : vc.getViewTitle();
        if (!Validate.isNullOrEmptyOrAllSpace(title)) {
            this.model.put("Albian_View_Title", title);
        }

        Map<String, String> cheaders = vc.getHeaders();
        Map<String, String> cStyleBlocks = vc.getStyleBlocks();
        Map<String, String> cStyleLinks = vc.getStyleLinks();

        this.headers.putAll(cheaders);
        this.styleBlocks.putAll(cStyleBlocks);
        this.styleLinks.putAll(cStyleLinks);


        this.model.put("Albian_SubTitle", vc.getSubTitle());
        this.model.put("Albian_View_Style_Content", styleBlocksToHtml());
        this.model.put("Albian_View_Style_Link", styleLinksToHtml());
        this.model.put("Albian_View_Style_Header", headersToHtml());
    }


    @NotHttpActionAttribute()
    public StringBuffer kAfterRender(StringBuffer contextBuffer) {
        return null;
    }

    @NotHttpActionAttribute()
    public boolean isMasterView() {
        return true;
    }
}
