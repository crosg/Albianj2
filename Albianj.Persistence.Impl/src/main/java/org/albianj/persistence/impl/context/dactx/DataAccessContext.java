package org.albianj.persistence.impl.context.dactx;

import org.albianj.persistence.context.IWriterJob;
import org.albianj.persistence.context.dactx.AlbianDataAccessOpt;
import org.albianj.persistence.context.dactx.IDataAccessContext;
import org.albianj.persistence.context.IPersistenceCompensateNotify;
import org.albianj.persistence.context.IPersistenceNotify;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.context.dactx.AlbianObjectWarp;
import org.albianj.persistence.impl.context.IWriterJobAdapter;
import org.albianj.persistence.impl.context.WriterJobAdapter;
import org.albianj.persistence.impl.db.IPersistenceTransactionClusterScope;
import org.albianj.persistence.impl.db.PersistenceTransactionClusterScope;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.context.dactx.IAlbianObjectWarp;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.verify.Validate;

import java.util.ArrayList;
import java.util.List;

public class DataAccessContext implements IDataAccessContext {
    List<IAlbianObjectWarp> entitis = null;
    private boolean isSetQueryIdentity = false;
    private IPersistenceNotify notifyCallback;
    private Object notifyCallbackObject;
    private IPersistenceCompensateNotify compensateCallback;
    private Object compensateCallbackObject;

    public DataAccessContext(){
        entitis = new ArrayList<>();
    }


    @Override
    public IDataAccessContext add(int opt, IAlbianObject entity) {
        IAlbianObjectWarp  warp = new AlbianObjectWarp();
        warp.setEntry(entity);
        warp.setPersistenceOpt(opt);
        entitis.add(warp);
        return this;
    }

    @Override
    public IDataAccessContext add( int opt, IAlbianObject entity,String storageAlias) {
        IAlbianObjectWarp  warp = new AlbianObjectWarp();
        warp.setEntry(entity);
        warp.setPersistenceOpt(opt);
        warp.setStorageAliasName(storageAlias);
        entitis.add(warp);
        return this;

    }

    @Override
    public IDataAccessContext add(int opt, IAlbianObject entity,String storageAlias, String tableAlias) {
        IAlbianObjectWarp  warp = new AlbianObjectWarp();
        warp.setEntry(entity);
        warp.setPersistenceOpt(opt);
        warp.setStorageAliasName(storageAlias);
        warp.setTableAliasName(tableAlias);
        entitis.add(warp);
        return this;

    }

    public IDataAccessContext withQueryGenKey(){
        if(this.isSetQueryIdentity){
            throw new AlbianDataServiceException("da-ctx exist query auto genkey");
        }
        entitis.get(entitis.size() -1 ).setQueryIdentitry(true);
        this.isSetQueryIdentity = true;
        return this;
    }

    public IDataAccessContext setFinishNotify(IPersistenceNotify notifyCallback, Object notifyCallbackObject){
        this.notifyCallback = notifyCallback;
        this.notifyCallbackObject = notifyCallbackObject;
        return this;
    }


    public IDataAccessContext setFailCompensator(IPersistenceCompensateNotify compensateCallback, Object compensateCallbackObject){
        this.compensateCallback = compensateCallback;
        this.compensateCallbackObject = compensateCallbackObject;
        return this;
    }

    @Override
    public boolean commit(String sessionId) {
        IWriterJobAdapter jobAdp = new WriterJobAdapter();
        IWriterJob job = jobAdp.buildWriterJob(sessionId,this.entitis);
        IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
        return tcs.execute(job);
    }

    public long commitAndGetGenId(String sessionid){
//        IWriterJobAdapter jobAdp = new WriterJobAdapter();
//        jobAdp.buildWriterJob(sessionId,this.entitis);
        return 0;
    }

    public void reset(){
        isSetQueryIdentity = false;
        this.notifyCallback = null;
        this.notifyCallbackObject = null;
        this.notifyCallback = null;
        this.notifyCallbackObject = null;
        if(!Validate.isNullOrEmpty(entitis)) {
            entitis.clear();
        }
    }
}
