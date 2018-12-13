package org.albianj.mvc.service;

import org.albianj.mvc.config.AlbianHttpConfigurtion;
import org.albianj.service.AlbianBuiltinServiceNamePair;
import org.albianj.service.IAlbianService;

/**
 * Created by xuhaifeng on 16/12/6.
 */
public interface IAlbianMVCConfigurtionService extends IAlbianService {
    String Name = AlbianBuiltinServiceNamePair.AlbianMvcConfigurtionServiceName;

    AlbianHttpConfigurtion getHttpConfigurtion();
}
