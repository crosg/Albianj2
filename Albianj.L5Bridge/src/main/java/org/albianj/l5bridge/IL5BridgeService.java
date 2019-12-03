package org.albianj.l5bridge;

import org.albianj.argument.RefArg;
import org.albianj.service.IAlbianService;

public interface IL5BridgeService extends IAlbianService {
    String Name = "L5BridgeService";
    public void exchange(String l5tag, RefArg<String> ip, RefArg<Integer> port);
}
