package org.albianj.persistence.context.dactx;

import org.albianj.persistence.context.IPersistenceCompensateNotify;
import org.albianj.persistence.context.IPersistenceNotify;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.filter.IChainExpression;

/*
    对于单个数据库，单表，支持in
    PreparedStatement statement = connection.prepareStatement("Select * from test where field in (?)");
Array array = statement.getConnection().createArrayOf("VARCHAR", new Object[]{"A1", "B2","C3"});
statement.setArray(1, array);
    对于多数据库支持自增主键，但是每次ctx只有一个自增主键的获取
    支持一个事务中，CUD操作
 */

public interface IDataAccessContext {

    IDataAccessContext add(int opt, IAlbianObject entiry);

    IDataAccessContext add(int opt, IAlbianObject entiry,String storageAliasName);

    IDataAccessContext add(int opt, IAlbianObject entiry,String storageAliasName, String tableAliasName);

    IDataAccessContext withQueryGenKey();

    IDataAccessContext setFinishNotify(IPersistenceNotify notifyCallback, Object notifyCallbackObject);

    IDataAccessContext setFailCompensator(IPersistenceCompensateNotify compensateCallback, Object compensateCallbackObject);

    boolean commit(String sessionId);

    void reset();
}
