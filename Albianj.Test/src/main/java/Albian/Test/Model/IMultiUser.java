package Albian.Test.Model;

import org.albianj.persistence.object.IAlbianObject;

import java.math.BigInteger;

/*
 分库分表使用
 */
public interface IMultiUser  extends IAlbianObject {
    String getId();
    void setId(String id);

    String getUserName();
    void setUserName(String userName);

    String getPassword();
    void setPassword(String password);
}
