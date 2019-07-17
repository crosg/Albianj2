package Albian.Core.Service;

import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.service.AlbianObjectCreator;
import org.albianj.service.AlbianServiceRouter;

public final class AlbianServiceHub extends AlbianServiceRouter {



    public static IAlbianObject newInstance(String sessionId, String itf) {
        return AlbianObjectCreator.newInstance(sessionId, itf);
    }

    public static <T extends IAlbianObject> T newInstance(String sessionId, Class<T> clazz) {
        return (T) newInstance(sessionId, clazz.getName());
    }
}
