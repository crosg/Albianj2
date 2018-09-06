package org.albianj.persistence.impl.storage;

import org.albianj.kernel.AlbianState;
import org.albianj.kernel.IAlbianTransmitterService;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.persistence.db.IDataBasePool;
import org.albianj.persistence.object.DatabasePoolStyle;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.object.RunningStorageAttribute;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.service.AlbianServiceRouter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * project : com.yuewen.nrzx.albianj
 *
 * @ccversion 新建 - liyuqi 2018-07-23 11:38</br>
 */
public class AlbianStorageParserServiceTest {

    @BeforeClass
    public static void bootstrap() throws Exception {
        URL storageUrl = AlbianStorageParserServiceTest.class.getResource("/storage_test");
        String path = storageUrl.toExternalForm().substring("file:/".length());

        Class<?> clss = AlbianClassLoader.getInstance().loadClass("org.albianj.kernel.impl.AlbianTransmitterService");
        IAlbianTransmitterService abs = (IAlbianTransmitterService)clss.newInstance();
        abs.start(path, path);

        if (abs.getLifeState() != AlbianState.Running) {
            throw new IllegalStateException("Albianj 启动失败");
        }

    }

    @Test
    public void getDatabasePool() throws Exception {

        final IAlbianStorageParserService albianStorageParserService = AlbianServiceRouter
            .getSingletonService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name, true);
        final String sessionId = "AlbianStorageParserServiceTest";

        Assert.assertNotNull(albianStorageParserService);

        final IStorageAttribute storageAttribute =
            albianStorageParserService.getStorageAttribute("ccTestDB");

        final IRunningStorageAttribute runningStorageAttribute =
            new RunningStorageAttribute(storageAttribute, storageAttribute.getDatabase());

        IDataBasePool dataBasePool =
            albianStorageParserService.getDatabasePool(sessionId, runningStorageAttribute);

        Assert.assertNotNull(dataBasePool);

        Connection connection = dataBasePool.getConnection(sessionId, runningStorageAttribute);
        Assert.assertNotNull(connection);
        albianStorageParserService.returnConnection(sessionId, runningStorageAttribute, connection);


    }





}