package org.albianj.mvc.service.impl;

import org.albianj.loader.AlbianClassLoader;
import org.albianj.mvc.View;
import org.albianj.mvc.config.AlbianHttpConfigurtion;
import org.albianj.mvc.config.CustomTagConfigurtion;
import org.albianj.mvc.config.ViewConfigurtion;
import org.albianj.mvc.service.IAlbianTemplateService;
import org.albianj.mvc.service.TemplateException;
import org.albianj.service.*;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.verify.Validate;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Tag;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;
import org.beetl.core.resource.StringTemplateResourceLoader;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuhaifeng on 16/12/15.
 */

@AlbianServiceRant(Id = IAlbianTemplateService.Name, Interface = IAlbianTemplateService.class)
public class AlbianBeetlTemplateService extends FreeAlbianService implements IAlbianTemplateService {

    @AlbianServiceFieldRant(Type = AlbianServiceFieldType.Ref, Value = "AlbianMvcConfigurtionService.HttpConfigurtion",SetterLifetime = AlbianServiceFieldSetterLifetime.AfterNew)
    private AlbianHttpConfigurtion c;
    private GroupTemplate gt;
    private GroupTemplate sgt;
    private Map<String, Class<?>> functions = new HashMap<>();

    public String getServiceName() {
        return Name;
    }

    public void setHttpConfigurtion(AlbianHttpConfigurtion c) {
        this.c = c;
    }


    public void loading() throws AlbianParserException {
        try {
            FileResourceLoader resourceLoader = new FileResourceLoader(c.getRootPath(), c.getCharset());
            Configuration cfg = Configuration.defaultConfiguration();
            gt = new GroupTemplate(resourceLoader, cfg);
            StringTemplateResourceLoader rl = new StringTemplateResourceLoader();
            sgt = new GroupTemplate(rl, cfg);
            Map<String, CustomTagConfigurtion> ctcs = c.getCustomTags();
            for (Map.Entry<String, CustomTagConfigurtion> entry : ctcs.entrySet()) {
                CustomTagConfigurtion ctc = entry.getValue();
                Class<? extends Tag> cla = (Class<? extends Tag>) AlbianClassLoader.getInstance().loadClass(ctc.getFullClassname());
                if (null != cla) {
                    gt.registerTag(ctc.getName(), cla);
                    sgt.registerTag(ctc.getName(), cla);
                }
            }

        } catch (Exception e) {
            throw new AlbianParserException(e);
        }

    }

    public void renderTemplate(View page, Map<String, ?> model, Map<String, Class<?>> funcs, Writer writer)
            throws IOException, TemplateException {

        ViewConfigurtion pc = c.getPages().get(page.getClass().getName());
        String templatePath = pc.getTemplate();

        internalRenderTemplate(templatePath, page, model, funcs, writer);
    }

    public void renderTemplate(String templatePath, Map<String, ?> model, Map<String, Class<?>> funcs, Writer writer)
            throws IOException, TemplateException {

        internalRenderTemplate(templatePath, null, model, funcs, writer);
    }

    protected void internalRenderTemplate(String templatePath,
                                          View page,
                                          Map<String, ?> model,
                                          Map<String, Class<?>> funcs,
                                          Writer writer)
            throws IOException, TemplateException {
        if (!Validate.isNullOrEmpty(funcs)) {
            for (Map.Entry<String, Class<?>> e : funcs.entrySet()) {
                if (!functions.containsKey(e.getKey())) {
                    gt.registerFunctionPackage(e.getKey(), e.getValue());
                    sgt.registerFunctionPackage(e.getKey(), e.getValue());
                    functions.put(e.getKey(), e.getValue());
                }
            }
        }
        Template t = gt.getTemplate(templatePath);
        t.binding(model);
        t.renderTo(writer);
    }

    public StringBuffer renderTemplate(Map params, Map<String, Class<?>> funcs, String vmContext) {
        StringWriter writer = new StringWriter();
        try {
            if (!Validate.isNullOrEmpty(funcs)) {
                for (Map.Entry<String, Class<?>> e : funcs.entrySet()) {
                    if (!functions.containsKey(e.getKey())) {
                        gt.registerFunctionPackage(e.getKey(), e.getValue());
                        sgt.registerFunctionPackage(e.getKey(), e.getValue());
                        functions.put(e.getKey(), e.getValue());
                    }
                }
            }
            Template t = sgt.getTemplate(vmContext);
            t.binding(params);
            t.renderTo(writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return writer.getBuffer();
    }
}
