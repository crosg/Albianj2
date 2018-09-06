package org.albianj.persistence.impl.storage;

import org.albianj.persistence.db.IDataBasePool;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.object.RunningStorageAttribute;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.service.AlbianServiceRouter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

/**
 * project : com.yuewen.nrzx.albianj
 *
 * @ccversion 新建 - liyuqi 2018-07-23 16:01</br>
 */
public class DatabasePoolBenchmarkThread extends Thread {

    private final String storageName;
    private final int count;
    private final Coster coster;
    private CountDownLatch countDownLatch;

    public DatabasePoolBenchmarkThread(CountDownLatch countDownLatch, String storageName, int count) {
        this.countDownLatch = countDownLatch;
        this.storageName = storageName;
        this.count = count;
        this.coster = new Coster(count);
    }

    @Override
    public void run() {
        final IAlbianStorageParserService albianStorageParserService = AlbianServiceRouter
            .getSingletonService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name, true);
        final String sessionId = "AlbianStorageParserServiceTest-" + storageName;
        IStorageAttribute sa = albianStorageParserService.getStorageAttribute(storageName);
        int time = count;
        while (time-- > 0) {
            IRunningStorageAttribute rsa = new RunningStorageAttribute(sa, sa.getDatabase());
            IDataBasePool pool = albianStorageParserService.getDatabasePool(sessionId, rsa);
            long s = System.nanoTime();
            Connection con = pool.getConnection(sessionId, rsa);
            coster.doCost((System.nanoTime() - s));

            Statement cmd = null;
            ResultSet rs = null;
            try {
                cmd = con.createStatement();
                rs = cmd.executeQuery("select 1");
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                pool.returnConnection(sessionId, storageName, rsa.getDatabase(), con, cmd, rs);
            }


        }
        countDownLatch.countDown();
    }

    public Coster getCoster() {
        return coster;
    }
}
