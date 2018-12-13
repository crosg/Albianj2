package Albian.Test.DataRouters;

import Albian.Test.Model.IMultiUser;
import org.albianj.persistence.object.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * 此路由为MultiUser使用，主要用来作为MultiUser分库分表
 * 分库分表的规则：
 *  根据object的id进行划分，为了简单，我们将直接使用string类型的id
 *  id的组成为times_顺序号_库标识_表标识
 */
public class MultiUserDataRouter extends FreeAlbianObjectDataRouter {
    @Override
    public List<IDataRouterAttribute> mappingWriterRouting(
            Map<String, IDataRouterAttribute> routings, IAlbianObject obj) {
        // TODO Auto-generated method stub
        IMultiUser u = (IMultiUser) obj;
        String id = u.getId();
        String drBasename = "MUserWrite";
        String[] ids = id.split("_");
        String drName = drBasename + ids[2]; //使用表标识定位到那个路由
        List<IDataRouterAttribute> drs = new ArrayList<>(1);
        drs.add(routings.get(drName)); // 若一个对象需要同时保存到两个库，请选择两个路由
        return drs;
    }

    @Override
    public String mappingWriterRoutingStorage(IDataRouterAttribute routing,
                                              IAlbianObject obj) {
        return routing.getStorageName(); // 因为使用了一个路由对应了一个storage模式，故直接访问即可
        // 若一个路由下继续对storage进行区分，则需要根据算法进行获取storage
    }

    @Override
    public String mappingWriterRoutingDatabase(IStorageAttribute storage,
                                               IAlbianObject obj) {
        return storage.getDatabase(); // 因为使用了一个storage对应一个db模式，故直接访问即可
        // 若使用了一个storage对应多个数据库模式（注意，这里的数据库用户名，密码，ip地址必须一样），这选择数据库
    }

    @Override
    public String mappingWriterTable(IDataRouterAttribute routing,
                                     IAlbianObject obj) {
        // TODO Auto-generated method stub
        IMultiUser u = (IMultiUser) obj;
        String id = u.getId();
        String[] ids = id.split("_");
        String tablename = routing.getTableName() + "_" + ids[3]; //使用表标识
        return tablename;
    }


    @Override
    public IDataRouterAttribute mappingReaderRouting(
            Map<String, IDataRouterAttribute> routings,
            Map<String, IFilterCondition> wheres,
            Map<String, IOrderByCondition> orderbys) {
        IFilterCondition fc = wheres.get("Id");
        String id = (String) fc.getValue();
        String drBasename = "MUserRead";
        String[] ids = id.split("_");
        String drName = drBasename + ids[2]; //使用表标识定位到那个路由
        return routings.get(drName);
    }

    @Override
    public String mappingReaderTable(IDataRouterAttribute routing,
                                     Map<String, IFilterCondition> wheres,
                                     Map<String, IOrderByCondition> orderbys) {
        // TODO Auto-generated method stub
        IFilterCondition fc = wheres.get("Id");
        String id = (String) fc.getValue();
        String[] ids = id.split("_");
        String tablename = routing.getTableName() + "_" + ids[3]; //使用表标识定位到那个路由
        return tablename;
    }


}
