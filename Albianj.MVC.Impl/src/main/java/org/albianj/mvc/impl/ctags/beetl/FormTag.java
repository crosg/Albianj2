package org.albianj.mvc.impl.ctags.beetl;

import org.albianj.mvc.HttpContext;
import org.beetl.core.Context;
import org.beetl.core.GeneralVarTagBinding;
import org.beetl.core.statement.Statement;

/**
 * Created by xuhaifeng on 17/1/19.
 */
public class FormTag extends GeneralVarTagBinding {

    private HttpContext currentContext = null;
    private String mvName = null;

    public static String getName() {
        return "HForm";
    }

    public HttpContext getCurrentContext() {
        return this.currentContext;
    }

    public void setCurrentContext(HttpContext ctx) {
        this.currentContext = ctx;
    }

    @Override
    public void init(Context ctx, Object[] args, Statement st) {
        super.init(ctx, args, st);
        Object o = getAttributeValue("ctx");
        currentContext = (HttpContext) o;
        Object s = getAttributeValue("name");
        mvName = (String) s;

    }

    @Override
    public void render() {

    }
}
