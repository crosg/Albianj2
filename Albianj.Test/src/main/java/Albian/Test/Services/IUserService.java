package Albian.Test.Services;

import Albian.Test.Model.IMultiUser;
import org.albianj.service.IAlbianService;
import org.apache.bcel.verifier.exc.StaticCodeConstraintException;

import java.util.List;

public interface IUserService extends IAlbianService {
    final  String Name ="UserService";

    boolean login(String uname,String pwd);
    boolean addUser(String uname,String pwd);
    boolean modifyPwd(String uname,String orgPwd,String newPwd);
    boolean batchAddUser();
    void queryMulitUserById();
    boolean tranOptUser();
}
