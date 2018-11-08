package Albian.Test.Services;

import org.albianj.service.IAlbianService;

public interface IUTF8M64Service extends IAlbianService {

    public static String ServiceId="UTF8M64Service";
    boolean saveUtf8M64(int id,String v);

    String getUtf8M64(int id);
}
