package Albian.Test.Model.Impl;

import Albian.Test.Model.ISingleUser;
import org.albianj.persistence.object.FreeAlbianObject;
import org.albianj.persistence.object.rants.AlbianObjectDataFieldRant;
import org.albianj.persistence.object.rants.AlbianObjectRant;

import java.math.BigInteger;

//如果使用特性模式，必须使用此标注，否则albianj不会对其进行解析
@AlbianObjectRant(Interface = ISingleUser.class)
public class SingleUser extends FreeAlbianObject implements ISingleUser {

    @AlbianObjectDataFieldRant(IsPrimaryKey = true)
    private BigInteger id;
    private String UserName;
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
        return this.UserName;
    }

    @Override
    public void setUserName(String userName) {
        this.UserName = userName;
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
