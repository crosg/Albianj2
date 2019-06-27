package org.albianj.persistence.impl.context.dactx;

import org.albianj.boot.BundleContext;
import org.albianj.loader.AlbianBootContext;
import org.albianj.persistence.context.IPersistenceCompensateNotify;
import org.albianj.persistence.context.IPersistenceNotify;
import org.albianj.persistence.context.IWriterJob;
import org.albianj.persistence.context.dactx.AlbianObjectWarp;
import org.albianj.persistence.context.dactx.IAlbianObjectWarp;
import org.albianj.persistence.context.dactx.IDataAccessContext;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.impl.context.IWriterJobAdapter;
import org.albianj.persistence.impl.context.WriterJobAdapter;
import org.albianj.persistence.impl.db.IPersistenceTransactionClusterScope;
import org.albianj.persistence.impl.db.PersistenceTransactionClusterScope;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.verify.Validate;

import java.util.ArrayList;
import java.util.List;

public class DataAccessContext implements IDataAccessContext {
    private List<IAlbianObjectWarp> entitis = null;
    private boolean isSetQueryIdentity = false;
    private IPersistenceNotify notifyCallback;
    private Object notifyCallbackObject;
    private IPersistenceCompensateNotify compensateCallback;
    private Object compensateCallbackObject;
    private boolean rbkOnErr = false;
    private BundleContext bundleContext = null;

    public DataAccessContext() {
        this(AlbianBootContext.Instance.getCurrentBundleContext());
    }
    public DataAccessContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        entitis = new ArrayList<>();
    }

    @Override
    public IDataAccessContext add(int opt, IAlbianObject entity) {
        IAlbianObjectWarp warp = new AlbianObjectWarp();
        warp.setEntry(entity);
        warp.setPersistenceOpt(opt);
        entitis.add(warp);
        return this;
    }

    @Override
    public IDataAccessContext add(int opt, IAlbianObject entity, String storageAlias) {
        IAlbianObjectWarp warp = new AlbianObjectWarp();
        warp.setEntry(entity);
        warp.setPersistenceOpt(opt);
        warp.setStorageAliasName(storageAlias);
        entitis.add(warp);
        return this;

    }

    @Override
    public IDataAccessContext add(int opt, IAlbianObject entity, String storageAlias, String tableAlias) {
        IAlbianObjectWarp warp = new AlbianObjectWarp();
        warp.setEntry(entity);
        warp.setPersistenceOpt(opt);
        warp.setStorageAliasName(storageAlias);
        warp.setTableAliasName(tableAlias);
        entitis.add(warp);
        return this;

    }

    public IDataAccessContext withQueryGenKey() {
        if (this.isSetQueryIdentity) {
            throw new AlbianDataServiceException("da-ctx exist query auto genkey");
        }
        entitis.get(entitis.size() - 1).setQueryIdentitry(true);
        this.isSetQueryIdentity = true;
        return this;
    }

    public IDataAccessContext setFinishNotify(IPersistenceNotify notifyCallback, Object notifyCallbackObject) {
        this.notifyCallback = notifyCallback;
        this.notifyCallbackObject = notifyCallbackObject;
        return this;
    }


    public IDataAccessContext setMakeupFor(IPersistenceCompensateNotify compensateCallback, Object compensateCallbackObject) {
        this.compensateCallback = compensateCallback;
        this.compensateCallbackObject = compensateCallbackObject;
        return this;
    }

    public IDataAccessContext setRollBackOnError() {
        this.rbkOnErr = true;
        return this;
    }

    @Override
    public boolean commit(String sessionId) {
        IWriterJobAdapter jobAdp = new WriterJobAdapter();
        IWriterJob job = jobAdp.buildWriterJob(sessionId,this.bundleContext, this.entitis, this.rbkOnErr);
        IPersistenceTransactionClusterScope tcs = new PersistenceTransactionClusterScope();
        return tcs.execute(job);
    }


    public long commitAndGenId(String sessionid) {
        return 0;
    }

    public void reset() {
        isSetQueryIdentity = false;
        this.rbkOnErr = false;
        this.notifyCallback = null;
        this.notifyCallbackObject = null;
        this.notifyCallback = null;
        this.notifyCallbackObject = null;
        if (!Validate.isNullOrEmpty(entitis)) {
            entitis.clear();
        }
    }
}
