package Albian.Test.Model.Impl;

import Albian.Test.Model.IOrgSingleUser;
import org.albianj.persistence.object.FreeAlbianObject;
import org.albianj.persistence.object.rants.AlbianObjectDataFieldRant;

import java.math.BigInteger;

public class OrgSingleUser extends FreeAlbianObject implements IOrgSingleUser {
    private BigInteger id;
    private String userName;
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
