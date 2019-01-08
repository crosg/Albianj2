package org.albianj.mvc.server;

import org.albianj.mvc.config.AlbianHttpConfigurtion;

public interface IServerLifeCycle {
    void ServerStartup(AlbianHttpConfigurtion cf);
}
