package org.albianj.mvc.service;

import org.albianj.mvc.view.View;
import org.albianj.service.BuiltinNames;
import org.albianj.service.IService;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Created by xuhaifeng on 16/12/1.
 */
public interface ITemplateService extends IService {
    String Name = BuiltinNames.AlbianTemplateServiceName;

    void renderTemplate(View page, Map<String, ?> model, Map<String, Class<?>> funcs, Writer writer)
            throws IOException, TemplateException;

    void renderTemplate(String templatePath, Map<String, ?> model, Map<String, Class<?>> funcs, Writer writer)
            throws IOException, TemplateException;


    StringBuffer renderTemplate(Map params, Map<String, Class<?>> funcs, String vmContext);
}
