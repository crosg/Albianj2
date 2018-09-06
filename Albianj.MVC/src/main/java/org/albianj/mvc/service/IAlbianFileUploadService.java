package org.albianj.mvc.service;

import org.albianj.mvc.HttpContext;
import org.albianj.service.IAlbianService;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.IOException;

public interface IAlbianFileUploadService  extends IAlbianService{
	
	String Name = "AlbianFileUploadService";
	
	public ServletFileUpload getUploadService();

	public void parseRequest(HttpContext ctx) throws IOException, FileUploadException;


}
