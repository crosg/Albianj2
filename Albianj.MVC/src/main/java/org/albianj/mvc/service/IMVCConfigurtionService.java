package org.albianj.mvc.service;

import org.albianj.mvc.config.AlbianHttpConfigurtion;
import org.albianj.service.BuiltinNames;
import org.albianj.service.IService;

/**
 * Created by xuhaifeng on 16/12/6.
 */
public interface IMVCConfigurtionService extends IService {
    String Name = BuiltinNames.AlbianMvcConfigurtionServiceName;

    AlbianHttpConfigurtion getHttpConfigurtion();
}
