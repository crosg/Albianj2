package org.albianj.restful.service;

import org.albianj.restful.object.IAlbianRestfulResultV1;

/**
 * Created by xuhaifeng on 16/12/7.
 */
public interface IAlbianRestfulResultParserV1 {
    String parserToXml(IAlbianRestfulResultV1 result);
}
