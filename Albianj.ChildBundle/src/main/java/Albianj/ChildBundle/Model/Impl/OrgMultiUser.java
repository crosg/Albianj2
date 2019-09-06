package Albianj.ChildBundle.Model.Impl;

import Albianj.ChildBundle.Model.IOrgMultiUser;
import org.albianj.persistence.object.FreeAlbianObject;

public class OrgMultiUser extends FreeAlbianObject implements IOrgMultiUser {

    private String id;
    private String userName;
    private String password;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
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
