package Albian.Test.Services;

import org.albianj.service.IService;

public interface IUserService extends IService {
    final String Name = "UserService";

    boolean login(String uname, String pwd);

    boolean addUser(String uname, String pwd);

    boolean modifyPwd(String uname, String orgPwd, String newPwd);

    boolean batchAddUser();

    void queryMulitUserById();

    boolean tranOptUser();
}
