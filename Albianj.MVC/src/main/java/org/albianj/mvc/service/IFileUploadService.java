package org.albianj.mvc.service;

import org.albianj.mvc.HttpContext;
import org.albianj.service.BuiltinNames;
import org.albianj.service.IService;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.IOException;

public interface IFileUploadService extends IService {

    String Name = BuiltinNames.AlbianFileUploadServiceName;

    public ServletFileUpload getUploadService();

    public void parseRequest(HttpContext ctx) throws IOException, FileUploadException;


}
