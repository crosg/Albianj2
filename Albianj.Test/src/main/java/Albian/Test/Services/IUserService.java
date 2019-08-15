package Albian.Test.Services;

import org.albianj.argument.RefArg;
import org.albianj.service.IAlbianService;

public interface IUserService extends IAlbianService {
    final String Name = "UserService";

    boolean login(String uname, String pwd);

    boolean addUser(String uname, String pwd);

    boolean modifyPwd(String uname, String orgPwd, String newPwd);

    boolean batchAddUser(RefArg<String> id1,RefArg<String> id2);

    void queryMulitUserById(String idl, String idr);

    boolean tranOptUser();
}
