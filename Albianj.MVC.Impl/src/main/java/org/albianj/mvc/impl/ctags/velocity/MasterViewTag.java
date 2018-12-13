package org.albianj.mvc.impl.ctags.velocity;

import org.albianj.io.Path;
import org.albianj.mvc.HttpContext;
import org.albianj.mvc.MView;
import org.albianj.mvc.config.ViewConfigurtion;
import org.albianj.mvc.service.IAlbianTemplateService;
import org.albianj.service.AlbianServiceRouter;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Created by xuhaifeng on 16/12/14.
 */
public class MasterViewTag extends Directive {
    private static ViewConfigurtion pc = null;

    @Override
    public String getName() {
        return "MasterView";
    }

    @Override
    public int getType() {
        return LINE;
    }

    @Override
    public boolean render(InternalContextAdapter ctx, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        SimpleNode snCurrCtx = (SimpleNode) node.jjtGetChild(0);
        HttpContext currCtx = (HttpContext) snCurrCtx.value(ctx);
        currCtx.setUseMasterView(true);
        SimpleNode snMVName = (SimpleNode) node.jjtGetChild(1);
        String mvName = (String) snMVName.value(ctx);
        Map<String, ViewConfigurtion> mvs = currCtx.getHttpConfigurtion().getMasterViews();
        ViewConfigurtion pc = mvs.get(mvName);

        StringBuffer sb = Path.readLineFile(pc.getTemplate());

        MView mv = null;
        try {
            mv = (MView) pc.getRealClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        mv.kinit(currCtx);
        mv.kBeforeAction(pc);
        mv.load(currCtx);
        mv.kAfterAction(pc);
        mv.kBeforeRender();
        IAlbianTemplateService ats = AlbianServiceRouter.getSingletonService(IAlbianTemplateService.class, IAlbianTemplateService.Name);
        StringBuffer masterViewContext = ats.renderTemplate(mv.getModel(), mv.getFunctions(), sb.toString());
        currCtx.setMasterView(mv);
        currCtx.setMasterViewHtml(masterViewContext);
        return true;
    }
}
