package org.albianj.mvc.ctags;

import org.albianj.io.Path;
import org.albianj.mvc.HttpContext;
import org.albianj.mvc.view.MView;
import org.albianj.mvc.config.ViewConfigurtion;
import org.albianj.mvc.service.IAlbianTemplateService;
import org.albianj.service.AlbianServiceRouter;
import org.beetl.core.Context;
import org.beetl.core.GeneralVarTagBinding;
import org.beetl.core.statement.Statement;

import java.util.Map;

/**
 * Created by xuhaifeng on 16/12/15.
 */
public class MasterViewTag extends GeneralVarTagBinding {

    private HttpContext currentContext = null;
    private String mvName = null;

    public static String getName() {
        return "MasterView";
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
        currentContext.setUseMasterView(true);
        Map<String, ViewConfigurtion> mvs = currentContext.getHttpConfigurtion().getMasterViews();
        ViewConfigurtion pc = mvs.get(mvName);

        StringBuffer sb = Path.readLineFile(pc.getTemplate());

        MView view = null;
        try {
            view = (MView) pc.getRealClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        view.kinit(currentContext);
        view.kBeforeAction(pc);
        view.load(currentContext);
        view.kAfterAction(pc);
        view.kBeforeRender();
        IAlbianTemplateService ats = AlbianServiceRouter.getSingletonService(IAlbianTemplateService.class, IAlbianTemplateService.Name);
        StringBuffer masterViewContext = ats.renderTemplate(view.getModel(), view.getFunctions(), sb.toString());
        currentContext.setMasterView(view);
        currentContext.setMasterViewHtml(masterViewContext);

    }
}
