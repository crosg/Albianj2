package org.albianj.persistence.service;

import org.albianj.persistence.object.*;
import org.albianj.verify.Validate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class AlbianObjectDataRouterDefaulter extends FreeAlbianObjectDataRouter {

    @Override
    public List<IDataRouterAttribute> mappingWriterRouting(
            Map<String, IDataRouterAttribute> routings, IAlbianObject obj) {
        // TODO Auto-generated method stub
        if (Validate.isNullOrEmpty(routings)) return null;
        if (1 == routings.size()) {
            Set<String> keys = routings.keySet();
            if (null == keys || 1 != keys.size()) return null;
            Object[] skeys = keys.toArray();
            IDataRouterAttribute dra = routings.get(skeys[0]);
            if (!dra.getEnable()) return null;
            List<IDataRouterAttribute> ras = new Vector<IDataRouterAttribute>();
            ras.add(dra);
            return ras;
        }
        return null;
    }

    @Override
    public IDataRouterAttribute mappingReaderRouting(
            Map<String, IDataRouterAttribute> routings,
            Map<String, IFilterCondition> wheres,
            Map<String, IOrderByCondition> orderbys) {
        // TODO Auto-generated method stub
        if (Validate.isNullOrEmpty(routings)) return null;
        if (1 == routings.size()) {
            Set<String> keys = routings.keySet();
            if (null == keys || 1 != keys.size()) return null;
            Object[] skeys = keys.toArray();
            IDataRouterAttribute dra = routings.get(skeys[0]);
            if (!dra.getEnable()) return null;
            return dra;
        }
        return null;
    }

    @Override
    public String mappingWriterRoutingStorage(IDataRouterAttribute routing,
                                              IAlbianObject obj) {
        // TODO Auto-generated method stub
        if (null == routing) return null;
        return routing.getStorageName();
    }

    @Override
    public String mappingWriterTable(IDataRouterAttribute routing,
                                     IAlbianObject obj) {
        // TODO Auto-generated method stub
        if (null == routing) return null;
        return routing.getTableName();
    }

    @Override
    public String mappingReaderRoutingStorage(IDataRouterAttribute routing,
                                              Map<String, IFilterCondition> wheres,
                                              Map<String, IOrderByCondition> orderbys) {
        // TODO Auto-generated method stub
        if (null == routing) return null;
        return routing.getStorageName();
    }

    @Override
    public String mappingReaderTable(IDataRouterAttribute routing,
                                     Map<String, IFilterCondition> wheres,
                                     Map<String, IOrderByCondition> orderbys) {
        // TODO Auto-generated method stub
        if (null == routing) return null;
        return routing.getTableName();
    }

    public IDataRouterAttribute mappingExactReaderRouting(
            Map<String, IDataRouterAttribute> routings,
            Map<String, IFilterCondition> wheres,
            Map<String, IOrderByCondition> orderbys) {
        return mappingReaderRouting(routings, wheres, orderbys);
    }

    /**
     * @param routing
     * @param wheres
     * @param orderbys
     * @return
     */
    public String mappingExactReaderRoutingStorage(IDataRouterAttribute routing,
                                                   Map<String, IFilterCondition> wheres,
                                                   Map<String, IOrderByCondition> orderbys) {
        return mappingReaderRoutingStorage(routing, wheres, orderbys);
    }

    /**
     * @param routing
     * @param wheres
     * @param orderbys
     * @return
     */
    public String mappingExactReaderTable(IDataRouterAttribute routing,
                                          Map<String, IFilterCondition> wheres,
                                          Map<String, IOrderByCondition> orderbys) {
        return mappingReaderTable(routing, wheres, orderbys);
    }
}

