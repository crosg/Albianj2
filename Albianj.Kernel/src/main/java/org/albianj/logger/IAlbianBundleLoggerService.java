package org.albianj.logger;

import org.albianj.service.IAlbianService;

public interface IAlbianBundleLoggerService extends IAlbianBundleLogger, IAlbianService {
    String Name = "AlbianRootLoggerService";
    String LogName4Runtime = "Runtime";
    String LogName4State = "State";
    String LogName4Monitor = "Monitor";
}
