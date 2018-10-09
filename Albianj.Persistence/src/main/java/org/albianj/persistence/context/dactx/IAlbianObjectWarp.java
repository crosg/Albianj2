package org.albianj.persistence.context.dactx;

import org.albianj.persistence.object.IAlbianObject;

public interface IAlbianObjectWarp {
    int getPersistenceOpt();

    void setPersistenceOpt(int opt);

    IAlbianObject getEntry();

    void setEntry(IAlbianObject entry);

    String getStorageAliasName();

    void setStorageAliasName(String storageAliasName);

    String getTableAliasName();

    void setTableAliasName(String tableAliasName);

    boolean isQueryIdentitry();

    void setQueryIdentitry(boolean queryIdentitry);

}
