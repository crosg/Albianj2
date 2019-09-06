package Albianj.ChildBundle.Model.Impl;

import Albianj.ChildBundle.DataRouters.MultiUserDataRouter;
import Albianj.ChildBundle.Model.IMultiUser;
import org.albianj.persistence.object.FreeAlbianObject;
import org.albianj.persistence.object.rants.AlbianObjectDataFieldRant;
import org.albianj.persistence.object.rants.AlbianObjectDataRouterRant;
import org.albianj.persistence.object.rants.AlbianObjectDataRoutersRant;
import org.albianj.persistence.object.rants.AlbianObjectRant;

@AlbianObjectRant(Interface = IMultiUser.class, // 配置当前实体继承的接口，一个接口对应一个实现类
        DataRouters = @AlbianObjectDataRoutersRant( // 数据路由配置
                DataRouter = MultiUserDataRouter.class, //指定数据路由算法
                ReaderRouters = { // 配置读路由
                        @AlbianObjectDataRouterRant(Name = "MUserRead1", StorageName = "MUserStorage1", TableName = "MUser"),
                        @AlbianObjectDataRouterRant(Name = "MUserRead2", StorageName = "MUserStorage2")
                },
                WriterRouters = { //配置写路由
                        @AlbianObjectDataRouterRant(Name = "MUserWrite1", StorageName = "MUserStorage1", TableName = "MUser"),
                        @AlbianObjectDataRouterRant(Name = "MUserWrite2", StorageName = "MUserStorage2")
                }
        )

)
public class MultiUser extends FreeAlbianObject implements IMultiUser {
    @AlbianObjectDataFieldRant(IsPrimaryKey = true)
    private String id;
    private String userName;
    @AlbianObjectDataFieldRant(FieldName = "Pwd")
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
