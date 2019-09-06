package Albianj.ChildBundle.Model.Impl;

import Albianj.ChildBundle.Model.ISingleUser;
import org.albianj.persistence.object.FreeAlbianObject;
import org.albianj.persistence.object.rants.AlbianObjectDataFieldRant;
import org.albianj.persistence.object.rants.AlbianObjectRant;

import java.math.BigInteger;

//如果使用特性模式，必须使用此标注，否则albianj不会对其进行解析
@AlbianObjectRant(Interface = ISingleUser.class)
public class SingleUser extends FreeAlbianObject implements ISingleUser {

    @AlbianObjectDataFieldRant(IsPrimaryKey = true)
    private BigInteger id;
    private String userName;
    @AlbianObjectDataFieldRant(FieldName = "Pwd")
    private String password;

    @Override
    public BigInteger getId() {
        return this.id;
    }

    @Override
    public void setId(BigInteger id) {
        this.id = id;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }
}
