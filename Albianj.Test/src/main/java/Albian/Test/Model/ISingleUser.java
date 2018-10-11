package Albian.Test.Model;

import org.albianj.io.BinaryStream;
import org.albianj.persistence.object.IAlbianObject;

import java.math.BigInteger;

/*
 该接口对应的类不分库分表，直接指定storage进行保存操作
 */
public interface ISingleUser extends IAlbianObject {
    BigInteger getId();
    void setId(BigInteger id);

    String getUserName();
    void setUserName(String userName);

    String getPassword();
    void setPassword(String password);
}
