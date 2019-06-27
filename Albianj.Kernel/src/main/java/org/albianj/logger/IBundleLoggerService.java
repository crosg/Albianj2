package org.albianj.logger;

import org.albianj.service.IService;

public interface IBundleLoggerService extends IAlbianBundleLogger, IService {
    String Name = "AlbianRootLoggerService";
    String LogName4Runtime = "Runtime";
    String LogName4State = "State";
    String LogName4Monitor = "Monitor";
}
