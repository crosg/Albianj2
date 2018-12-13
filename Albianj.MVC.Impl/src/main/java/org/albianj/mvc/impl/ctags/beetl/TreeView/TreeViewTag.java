package org.albianj.mvc.impl.ctags.beetl.TreeView;

import org.albianj.mvc.HttpContext;
import org.beetl.core.Context;
import org.beetl.core.GeneralVarTagBinding;
import org.beetl.core.statement.Statement;

/**
 * Created by xuhaifeng on 16/12/23.
 */
public class TreeViewTag extends GeneralVarTagBinding {

    private HttpContext currentContext = null;

    public static String getName() {
        return "TreeView";
    }

    @Override
    public void init(Context ctx, Object[] args, Statement st) {
        super.init(ctx, args, st);
        Object o = getAttributeValue("ctx");
        currentContext = (HttpContext) o;
    }

    public void render() {

    }


}
