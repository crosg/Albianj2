package org.albianj.mvc.impl.ctags.beetl;

import org.albianj.mvc.HttpContext;
import org.beetl.core.Context;
import org.beetl.core.GeneralVarTagBinding;
import org.beetl.core.statement.Statement;

/**
 * Created by xuhaifeng on 17/1/19.
 */
public class FormTag extends GeneralVarTagBinding {

    public static String getName(){
        return "HForm";
    }

    private HttpContext currentContext = null;
    private String mvName = null;

    public void setCurrentContext(HttpContext ctx){
        this.currentContext = ctx;
    }

    public HttpContext getCurrentContext(){
        return this.currentContext;
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
//        currentContext.setUseMasterView(true);
//        Map<String, ViewConfigurtion> mvs = currentContext.getHttpConfigurtion().getMasterViews();
//        ViewConfigurtion pc = mvs.get(mvName);
//
//        StringBuffer sb = Path.readLineFile(pc.getTemplate());
//
//        MView view = null;
//        try {
//            view = (MView) pc.getRealClass().newInstance();
//        } catch (InstantiationException  | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        view.kinit(currentContext);
//        view.kBeforeAction(pc);
//        view.load(currentContext);
//        view.kAfterAction(pc);
//        view.kBeforeRender();
//        IAlbianTemplateService ats = AlbianServiceRouter.getSingletonService(IAlbianTemplateService.class,IAlbianTemplateService.Name);
//        StringBuffer masterViewContext = ats.renderTemplate(view.getModel(),view.getFunctions(),sb.toString());
//        currentContext.setMasterView(view);
//        currentContext.setMasterViewHtml(masterViewContext);

    }
}
