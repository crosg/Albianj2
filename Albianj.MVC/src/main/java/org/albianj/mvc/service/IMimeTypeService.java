package org.albianj.mvc.service;

import org.albianj.service.parser.IParserService;

/**
 * Created by xuhaifeng on 16/12/13.
 */
public interface IMimeTypeService extends IParserService {
    String Name = "AlbianMimeTypeService";

    String getMimeType(String key);
}
