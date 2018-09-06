package org.albianj.mvc.service;

import org.albianj.service.parser.IAlbianParserService;

/**
 * Created by xuhaifeng on 16/12/13.
 */
public interface IAlbianMimeTypeService extends IAlbianParserService {
    String Name = "AlbianMimeTypeService";
    String getMimeType(String key);
}
