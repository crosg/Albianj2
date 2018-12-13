package org.albianj.mvc;

import org.albianj.mvc.config.ViewConfigurtion;

/**
 * Created by xuhaifeng on 16/12/15.
 */
public interface IView {
    @NotHttpActionAttribute()
    String getViewTitle();

    @NotHttpActionAttribute()
    public boolean isMasterView();

    @NotHttpActionAttribute()
    void kinit(HttpContext ctx);

    @NotHttpActionAttribute()
    void kBeforeAction(ViewConfigurtion pc);

    @NotHttpActionAttribute()
    void kAfterAction(ViewConfigurtion pc);

    @NotHttpActionAttribute()
    void kBeforeRender();

    @NotHttpActionAttribute()
    StringBuffer render();

    @NotHttpActionAttribute()
    StringBuffer kAfterRender(StringBuffer contextBuffer);

    @NotHttpActionAttribute()
    ActionResult interceptor(HttpContext ctx);

    @HttpActionAttribute(Method = HttpActionMethod.Get)
    ActionResult load(HttpContext ctx);

    @NotHttpActionAttribute()
    void bindingAttribute(String name, Object value);
}
