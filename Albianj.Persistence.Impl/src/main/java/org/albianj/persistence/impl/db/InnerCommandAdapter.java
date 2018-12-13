package org.albianj.persistence.impl.db;

/**
 * Created by xuhaifeng on 16/12/28.
 */
public class InnerCommandAdapter {

//    public static Map<String, ISqlParameter> makeCreateCommand(String sessionId,
//                                                               IAlbianObjectAttribute albianObject,
//                                                               Map<String, Object> mapValue
//                                                               ) throws AlbianDataServiceException {
//
//        Map<String, IMemberAttribute> mapMemberAttributes = albianObject
//                .getMembers();
//        Map<String, ISqlParameter> sqlParas = new HashMap<String, ISqlParameter>();
//        for (Map.Entry<String, IMemberAttribute> entry : mapMemberAttributes
//                .entrySet()) {
//            IMemberAttribute member = entry.getValue();
//
//            Object v = mapValue.get(member.getName());
//            if (!member.getIsSave() || null == v)
//                continue;
//
//            ISqlParameter para = new SqlParameter();
//            para.setName(member.getName());
//            para.setSqlFieldName(member.getSqlFieldName());
//            para.setSqlType(member.getDatabaseType());
//            para.setValue(v);
//            sqlParas.put(String.format("#%1$s#", member.getSqlFieldName()),
//                    para);
//        }
//        return sqlParas;
//    }

}
