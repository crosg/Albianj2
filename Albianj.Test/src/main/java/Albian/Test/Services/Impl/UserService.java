package Albian.Test.Services.Impl;

import Albian.Core.Service.AlbianServiceHub;
import Albian.Test.Model.IMultiUser;
import Albian.Test.Model.ISingleUser;
import Albian.Test.Services.IUserService;
import Albian.Test.Services.Metadata.StorageInfo;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.context.dactx.AlbianDataAccessOpt;
import org.albianj.persistence.context.dactx.IDataAccessContext;
import org.albianj.persistence.context.dactx.IQueryContext;
import org.albianj.persistence.object.LogicalOperation;
import org.albianj.persistence.object.filter.FilterExpression;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.persistence.service.IAlbianDataAccessService;
import org.albianj.persistence.service.LoadType;
import org.albianj.service.AlbianServiceFieldRant;
import org.albianj.service.AlbianServiceFieldType;
import org.albianj.service.AlbianServiceRant;
import org.albianj.service.FreeAlbianService;

import java.math.BigInteger;

// service必须使用此特性进行标注，否则albianj不对其进行解析
@AlbianServiceRant(Id = "UserService",Interface = IUserService.class)
public class UserService extends FreeAlbianService implements IUserService {

    //在没有确认与把握的情况下，慎用之慎用之慎用之（重要的话说三遍）
    //使用albianj的ioc直接对其属性进行赋值
    // 注意，所有使用AlbianServiceFieldRant赋值的值都是单利模式，故在albianj中会自动提升为静态变量状态
    @AlbianServiceFieldRant(Type = AlbianServiceFieldType.Ref,Value = "AlbianDataAccessService")
    private IAlbianDataAccessService da;

    @Override
    public boolean login(String uname,String pwd) {

        // where条件推荐使用表达式这种写法
        IChainExpression wheres = new FilterExpression("UserName", LogicalOperation.Equal, uname);
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IQueryContext qctx = da.newQueryContext();
        ISingleUser user = qctx.useStorage(StorageInfo.SingleUserStorageName) //指定到storage
                            .loadObject("sessionId", ISingleUser.class, LoadType.quickly,wheres);
        if(user.getPassword().equals(pwd)){
            return true;
        }
        // 如果还有查询，可以使用reset对其进行重置，再根据实际的需求进行组合使用
        qctx.reset();
        return false;
    }

    @Override
    public boolean addUser(String uname, String pwd) {
        AlbianServiceHub.addLog("Sessionid", IAlbianLoggerService.AlbianRunningLoggerName,
                AlbianLoggerLevel.Info,"i am %s","log");

        NullPointerException exc = new NullPointerException();
        AlbianServiceHub.addLog("Sessionid", IAlbianLoggerService.AlbianRunningLoggerName,
                AlbianLoggerLevel.Info,exc,"i am %s","log");

        //创建对象请使用此方法
        ISingleUser user = AlbianServiceHub.newInstance("SessionId",ISingleUser.class);
        user.setId(BigInteger.valueOf(System.currentTimeMillis()));
        user.setPassword(pwd);
        user.setUserName(uname);


        // 创建保存数据的上下文，不推荐使用save或者是create等诸如此类的原来的方法及其重载
        IDataAccessContext dctx = da.newDataAccessContext();
        return dctx.add(AlbianDataAccessOpt.Save, user,StorageInfo.SingleUserStorageName).commit("Sessionid");
    }

    @Override
    public boolean modifyPwd(String uname,String orgPwd,String newPwd) {
        // 如果是更改数据库记录，必须先需要load一下数据库记录，
        IChainExpression wheres = new FilterExpression("UserName", LogicalOperation.Equal, uname);
        IQueryContext qctx = da.newQueryContext();
        ISingleUser user = qctx.useStorage(StorageInfo.SingleUserStorageName) //指定到storage
                // 如果需要及其精确，使用LoadType.exact，并且指定主数据库或根据DataRouter走WriteRouters配置
                .loadObject("sessionId", ISingleUser.class, LoadType.quickly,wheres);
        if(user.getPassword().equals(orgPwd)){
            user.setPassword(newPwd);
            IDataAccessContext dctx = da.newDataAccessContext();
            return dctx.add(AlbianDataAccessOpt.Save, user,StorageInfo.SingleUserStorageName).commit("Sessionid");
        }
        return false;
    }

    int idx = 0;
    @Override
    public boolean batchAddUser() {
        IDataAccessContext dctx = da.newDataAccessContext();
        IMultiUser mu1 = AlbianServiceHub.newInstance("sessionId",IMultiUser.class);
        String id1 = String.format("%d_%d_%d_%d",System.currentTimeMillis(),++idx,1,1);
        mu1.setId(id1);
        mu1.setUserName("mu1");
        mu1.setPassword("mu1pwd");

        IMultiUser mu2 = AlbianServiceHub.newInstance("sessionId",IMultiUser.class);
        String id2 = String.format("%d_%d_%d_%d",System.currentTimeMillis(),++idx,2,2);
        mu2.setId(id2);
        mu2.setUserName("mu2");
        mu2.setPassword("mu2pwd");

        ISingleUser user = AlbianServiceHub.newInstance("SessionId",ISingleUser.class);
        user.setId(BigInteger.valueOf(System.currentTimeMillis()));
        user.setPassword("batcher");
        user.setUserName("batcher");
        //同时使用数据路由与单数据库保存
        dctx.add(AlbianDataAccessOpt.Save, mu1)
                .add(AlbianDataAccessOpt.Save,mu2)
                .add(AlbianDataAccessOpt.Save,user,StorageInfo.SingleUserStorageName)
                .commit("sessionId");


        return false;
    }

    @Override
    public void queryMulitUserById() {
        // where条件推荐使用表达式这种写法
        IChainExpression whrs1 = new FilterExpression("Id", LogicalOperation.Equal, "1539240117605_1_1_1");
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IQueryContext qctx = da.newQueryContext();
        IMultiUser mu1 = qctx.loadObject("sessionId", IMultiUser.class, LoadType.quickly,whrs1);
        System.out.println(String.format("MU1:id->%s uname->%s pwd->%s",
                mu1.getId(),mu1.getUserName(),mu1.getPassword()));
        qctx.reset();

        IChainExpression whrs2 = new FilterExpression("Id", LogicalOperation.Equal, "1539240117606_2_2_2");
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IMultiUser mu2 = qctx.loadObject("sessionId", IMultiUser.class, LoadType.quickly,whrs2);
        System.out.println(String.format("MU2:id->%s uname->%s pwd->%s",
                mu2.getId(),mu2.getUserName(),mu2.getPassword()));


    }

    @Override
    public boolean tranOptUser() {
        return false;
    }

}
