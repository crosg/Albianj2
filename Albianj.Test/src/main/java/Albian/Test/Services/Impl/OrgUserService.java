package Albian.Test.Services.Impl;

import Albian.Core.Service.AlbianServiceHub;
import Albian.Test.Model.IOrgMultiUser;
import Albian.Test.Model.IOrgSingleUser;
import Albian.Test.Model.ISingleUser;
import Albian.Test.Services.IOrgUserService;
import Albian.Test.Services.Metadata.StorageInfo;
import org.albianj.persistence.context.dactx.AlbianDataAccessOpt;
import org.albianj.persistence.context.dactx.IDataAccessContext;
import org.albianj.persistence.context.dactx.IQueryContext;
import org.albianj.persistence.object.LogicalOperation;
import org.albianj.persistence.object.filter.FilterExpression;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.persistence.service.IDataAccessService;
import org.albianj.persistence.service.LoadType;
import org.albianj.service.FreeService;

import java.math.BigInteger;

public class OrgUserService extends FreeService implements IOrgUserService {
    int idx = 0;
    private IDataAccessService da;

    @Override
    public boolean login(String uname, String pwd) {
        // where条件推荐使用表达式这种写法
        IChainExpression wheres = new FilterExpression("UserName", LogicalOperation.Equal, uname);
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IQueryContext qctx = da.newQueryContext();
        IOrgSingleUser user = qctx.useStorage(StorageInfo.SingleUserStorageName).fromTable("SingleUser") //指定到storage
                .loadObject("sessionId", IOrgSingleUser.class, LoadType.quickly, wheres);
        if (user.getPassword().equals(pwd)) {
            return true;
        }
        // 如果还有查询，可以使用reset对其进行重置，再根据实际的需求进行组合使用
        qctx.reset();
        return false;
    }

    @Override
    public boolean addUser(String uname, String pwd) {
        //创建对象请使用此方法
        IOrgSingleUser user = AlbianServiceHub.newInstance("SessionId", IOrgSingleUser.class);
        user.setId(BigInteger.valueOf(System.currentTimeMillis()));
        user.setPassword(pwd);
        user.setUserName(uname);


        // 创建保存数据的上下文，不推荐使用save或者是create等诸如此类的原来的方法及其重载
        IDataAccessContext dctx = da.newDataAccessContext();
        return dctx.add(AlbianDataAccessOpt.Save, user, StorageInfo.SingleUserStorageName, "SingleUser").commit("Sessionid");
    }

    @Override
    public boolean modifyPwd(String uname, String orgPwd, String newPwd) {
        // 如果是更改数据库记录，必须先需要load一下数据库记录，
        IChainExpression wheres = new FilterExpression("UserName", LogicalOperation.Equal, uname);
        IQueryContext qctx = da.newQueryContext();
        IOrgSingleUser user = qctx.useStorage(StorageInfo.SingleUserStorageName).fromTable("SingleUser") //指定到storage
                // 如果需要及其精确，使用LoadType.exact，并且指定主数据库或根据DataRouter走WriteRouters配置
                .loadObject("sessionId", IOrgSingleUser.class, LoadType.quickly, wheres);
        if (user.getPassword().equals(orgPwd)) {
            user.setPassword(newPwd);
            IDataAccessContext dctx = da.newDataAccessContext();
            return dctx.add(AlbianDataAccessOpt.Save, user, StorageInfo.SingleUserStorageName, "SingleUser").commit("Sessionid");
        }
        return false;
    }

    @Override
    public boolean batchAddUser() {
        IDataAccessContext dctx = da.newDataAccessContext();
        IOrgMultiUser mu1 = AlbianServiceHub.newInstance("sessionId", IOrgMultiUser.class);
        String id1 = String.format("%d_%d_%d_%d", System.currentTimeMillis(), ++idx, 1, 1);
        mu1.setId(id1);
        mu1.setUserName("mu1_org");
        mu1.setPassword("mu1pwd_org");

        IOrgMultiUser mu2 = AlbianServiceHub.newInstance("sessionId", IOrgMultiUser.class);
        String id2 = String.format("%d_%d_%d_%d", System.currentTimeMillis(), ++idx, 2, 2);
        mu2.setId(id2);
        mu2.setUserName("mu2_org");
        mu2.setPassword("mu2pwd_org");

        ISingleUser user = AlbianServiceHub.newInstance("SessionId", ISingleUser.class);
        user.setId(BigInteger.valueOf(System.currentTimeMillis()));
        user.setPassword("batcher_by_org");
        user.setUserName("batcher_by_org");
        //同时使用数据路由与单数据库保存
        dctx.add(AlbianDataAccessOpt.Save, mu1)
                .add(AlbianDataAccessOpt.Save, mu2)
                .add(AlbianDataAccessOpt.Save, user, StorageInfo.SingleUserStorageName)
                .commit("sessionId");


        return false;
    }

    @Override
    public void queryMulitUserById() {
        // where条件推荐使用表达式这种写法
        IChainExpression whrs1 = new FilterExpression("Id", LogicalOperation.Equal, "1539240117605_1_1_1");
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IQueryContext qctx = da.newQueryContext();
        IOrgMultiUser mu1 = qctx.loadObject("sessionId", IOrgMultiUser.class, LoadType.quickly, whrs1);
        System.out.println(String.format("MU1:id->%s uname->%s pwd->%s",
                mu1.getId(), mu1.getUserName(), mu1.getPassword()));
        qctx.reset();

        IChainExpression whrs2 = new FilterExpression("Id", LogicalOperation.Equal, "1539240117606_2_2_2");
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IOrgMultiUser mu2 = qctx.loadObject("sessionId", IOrgMultiUser.class, LoadType.quickly, whrs2);
        System.out.println(String.format("MU2:id->%s uname->%s pwd->%s",
                mu2.getId(), mu2.getUserName(), mu2.getPassword()));


    }

    @Override
    public boolean tranOptUser() {
        return false;
    }
}
