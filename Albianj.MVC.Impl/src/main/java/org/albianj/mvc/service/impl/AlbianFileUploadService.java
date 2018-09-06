package org.albianj.mvc.service.impl;

import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.mvc.HttpContext;
import org.albianj.mvc.config.AlbianHttpConfigurtion;
import org.albianj.mvc.config.FileUploadConfigurtion;
import org.albianj.mvc.service.IAlbianFileUploadService;
import org.albianj.mvc.service.UploadFile;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.FreeAlbianService;
import org.albianj.verify.Validate;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class AlbianFileUploadService extends FreeAlbianService implements IAlbianFileUploadService {
    public String getServiceName(){
        return Name;
    }

    private ServletFileUpload upload = null;
    private AlbianHttpConfigurtion c = null;

    public void setHttpConfigurtion(AlbianHttpConfigurtion c) {
        this.c = c;
    }

    public void loading() {
        FileUploadConfigurtion fc = c.getFileUploadConfigurtion();
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setRepository(factory.getRepository());
        upload = new ServletFileUpload(factory);
        this.upload.setSizeMax(fc.getMaxRequestSize());
        this.upload.setHeaderEncoding(c.getCharset());
        this.upload.setFileSizeMax(fc.getMaxFileSize());
        upload.setHeaderEncoding(c.getCharset());

    }

    @Override
    public ServletFileUpload getUploadService() {
        // TODO Auto-generated method stub
        return this.upload;
    }

    @Override
    public void parseRequest(HttpContext  ctx) throws IOException, FileUploadException {
        HttpServletRequest request = ctx.getCurrentRequest();
        Map<String, UploadFile> files = new LinkedHashMap<>();
        Map<String,String> attributes = new LinkedHashMap<>();
        FileUploadConfigurtion fc = c.getFileUploadConfigurtion();
        List list = upload.parseRequest(request);
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            FileItem formitem = (FileItem) iterator.next();
            if (formitem.isFormField()) {
                String name = formitem.getFieldName();
                String value = formitem.getString(Validate.isNullOrEmptyOrAllSpace(c.getCharset()) ? "utf-8" : c.getCharset());
                attributes.put(name,value);
            } else {
                //这里是上传文件的表单域
                String name = formitem.getName();
                String fieldName = formitem.getFieldName();
                if (formitem.getSize() > fc.getMaxFileSize()) {
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                            ctx.getHttpSessionId(), AlbianLoggerLevel.Warn,
                            "the file -> %s size -> %d is overflow the max-file-size -> %d.then skip it",
                            name,formitem.getSize(),fc.getMaxFileSize());
                    continue;
                }
                byte[] bytes = formitem.get();
                if(0 != bytes.length) {
                    UploadFile uf = new UploadFile();
                    uf.setClientFileName(name);
                    uf.setFieldName(fieldName);
                    uf.setData(bytes);
                    files.put(fieldName, uf);
                }
            }

        }

        if(!Validate.isNullOrEmpty(files)) {
            ctx.setFileItems(files);
        }
        if(!Validate.isNullOrEmpty(attributes)){
            ctx.setAttributes(attributes);
        }

    }

}
