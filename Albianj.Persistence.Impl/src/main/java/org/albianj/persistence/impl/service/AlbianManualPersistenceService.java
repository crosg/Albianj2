package org.albianj.persistence.impl.service;

import org.albianj.persistence.context.IManualCommand;
import org.albianj.persistence.context.IManualContext;
import org.albianj.persistence.impl.context.ManualContext;
import org.albianj.persistence.impl.db.IManualCommandAdapter;
import org.albianj.persistence.impl.db.IManualTransactionScope;
import org.albianj.persistence.impl.db.ManualCommandAdapter;
import org.albianj.persistence.impl.db.ManualTransactionScope;
import org.albianj.persistence.service.IAlbianManualPersistenceService;
import org.albianj.service.AlbianServiceRant;
import org.albianj.service.FreeAlbianService;

import java.util.List;
import java.util.Vector;


/**
 * Created by xuhaifeng on 17/8/17.
 */
@AlbianServiceRant(Id = IAlbianManualPersistenceService.Name,Interface = IAlbianManualPersistenceService.class)
public class AlbianManualPersistenceService extends FreeAlbianService implements IAlbianManualPersistenceService {

    public String getServiceName(){
        return Name;
    }


    public int execute(String sessionId, String storageName, String dbName, IManualCommand cmd) {
        List<IManualCommand> mcs = new Vector<>();
        mcs.add(cmd);
        List<Integer> rcs = execute(sessionId,storageName,dbName,mcs);
        return rcs.get(0);
    }

    public int execute(String sessionId, String storageName, IManualCommand cmd) {
        List<IManualCommand> mcs = new Vector<>();
        mcs.add(cmd);
        List<Integer> rcs = execute(sessionId,storageName,null,mcs);
        return rcs.get(0);
    }

    public List<Integer> execute(String sessionId, String storageName, String dbName, List<IManualCommand> cmds) {

        IManualContext mctx = new ManualContext();


        mctx.setSessionId(sessionId);
        mctx.setCommands(cmds);

        IManualCommandAdapter mcd = new ManualCommandAdapter();
        mctx = mcd.createManualCommands(mctx);

        IManualTransactionScope mts = new ManualTransactionScope();
        mts.execute(mctx);
        return mctx.getResults();

    }

    public List<Integer> execute(String sessionId, String storageName, List<IManualCommand> cmds) {

        return execute(sessionId,storageName,null,cmds);

    }

}
