package Albian.Test.Services.Impl;

import Albian.Test.Model.ISingleUser;
import Albian.Test.Model.IUTF8M64;
import Albian.Test.Model.Impl.UTF8M64;
import Albian.Test.Services.IUTF8M64Service;
import Albian.Test.Services.Metadata.StorageInfo;
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

@AlbianServiceRant(Id = "UTF8M64Service")
public class UTF8M64Service extends FreeAlbianService implements IUTF8M64Service {

    @AlbianServiceFieldRant(Type = AlbianServiceFieldType.Ref,Value = "AlbianDataAccessService")
    private IAlbianDataAccessService da;

    @Override
    public boolean saveUtf8M64(int id, String v) {
        IUTF8M64 utf8m64 = new UTF8M64();
        IDataAccessContext dctx = da.newDataAccessContext();
        utf8m64.setId(id);
        utf8m64.setV(v);
        return dctx.add(AlbianDataAccessOpt.Save, utf8m64, StorageInfo.UTF8Mb64TestStorageName,"tb_test_emoji_1").commit("Sessionid");
    }

    @Override
    public String getUtf8M64(int id) {
        IChainExpression wheres = new FilterExpression("id", LogicalOperation.Equal, id);
        //查询sql推荐使用query ctx，不推荐原来的具体方法，通过重载区分
        IQueryContext qctx = da.newQueryContext();
        IUTF8M64 utf8m64 = qctx.useStorage(StorageInfo.UTF8Mb64TestStorageName).fromTable("tb_test_emoji_1") //指定到storage
                .loadObject("sessionId", IUTF8M64.class, LoadType.quickly,wheres);
      return utf8m64.getV();
    }
}
