package org.albianj.persistence.impl.context;

import java.util.List;
import java.util.Map;

import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.impl.db.SqlParameter;
import org.albianj.persistence.impl.toolkit.Convert;
import org.albianj.persistence.impl.toolkit.EnumMapping;
import org.albianj.persistence.object.FilterCondition;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IFilterCondition;
import org.albianj.persistence.object.IMemberAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.object.PersistenceDatabaseStyle;
import org.albianj.persistence.object.RelationalOperator;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.persistence.object.filter.IFilterExpression;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

public class ChainExpressionParser {

	public static void toFilterConditionMap(IChainExpression f, Map<String, IFilterCondition> map) {
		List<IChainExpression> ces = f.getChainExpression();
		if (null == ces || 0 == ces.size())
			return;
		for (IChainExpression ce : ces) {
			if (IChainExpression.STYLE_FILTER_GROUP == ce.getStyle()) {
				toFilterConditionMap(ce, map);
			} else {
				IFilterExpression fe = (IFilterExpression) ce;
				map.put(Validate.isNullOrEmptyOrAllSpace(fe.getAliasName()) ? fe.getFieldName() : fe.getAliasName(),
						new FilterCondition(fe));
			}
		}
	}
	
	public static void toFilterConditionArray(IChainExpression f, List<IFilterCondition> list) {
		List<IChainExpression> ces = f.getChainExpression();
		if (null == ces || 0 == ces.size())
			return;
		for (IChainExpression ce : ces) {
			if (IChainExpression.STYLE_FILTER_GROUP == ce.getStyle()) {
				toFilterConditionArray(ce, list);
			} else {
				IFilterExpression fe = (IFilterExpression) ce;
				list.add(new FilterCondition(fe));
			}
		}
	}

	public static void toConditionText(String sessionId, Class<?> cls, IAlbianObjectAttribute albianObject,
			IStorageAttribute storage, IChainExpression f, StringBuilder sb, Map<String, ISqlParameter> paras)
			throws AlbianDataServiceException {

		List<IChainExpression> ces = f.getChainExpression();
		if (null == ces || 0 == ces.size())
			return;
		for (IChainExpression ce : ces) {
			if (IChainExpression.STYLE_FILTER_GROUP == ce.getStyle()) {
				if (null == ce.getChainExpression() || 0 == ce.getChainExpression().size()) {
					continue;
				}
				
				if(RelationalOperator.Normal != ce.getRelationalOperator() ){
					String slo = EnumMapping.toRelationalOperators(ce.getRelationalOperator());
					if (!Validate.isNullOrEmptyOrAllSpace(slo)) {
						sb.append(" ").append(slo);
					}
				}
				sb.append(" (");
				toConditionText(sessionId, cls, albianObject, storage, ce, sb, paras);
				sb.append(" )");
			} else {
				IFilterExpression fe = (IFilterExpression) ce;

				if (fe.isAddition())
					continue;

				String className = cls.getName();
				IMemberAttribute member = albianObject.getMembers().get(fe.getFieldName().toLowerCase());

				if (null == member) {
					AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
							AlbianDataServiceException.class, "DataService is error.",
							"albian-object:%s member:%s is not found.job id:%s.", className, fe.getFieldName(),
							sessionId);
				}

				String slo = EnumMapping.toRelationalOperators(fe.getRelationalOperator());
				if (!Validate.isNullOrEmptyOrAllSpace(slo)) {
					sb.append(" ").append(slo);
				}

				if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
					sb.append(" `").append(member.getSqlFieldName()).append("`");
				} else {
					sb.append(" [").append(member.getSqlFieldName()).append("]");
				}
				sb.append(EnumMapping.toLogicalOperation(fe.getLogicalOperation())).append("#")
						.append(Validate.isNullOrEmptyOrAllSpace(fe.getAliasName()) ? member.getSqlFieldName()
								: fe.getAliasName())
						.append("# ");

				ISqlParameter para = new SqlParameter();
				para.setName(member.getSqlFieldName());
				para.setSqlFieldName(member.getSqlFieldName());
				if (null == fe.getFieldClass()) {
					para.setSqlType(member.getDatabaseType());
				} else {
					para.setSqlType(Convert.toSqlType(fe.getFieldClass()));
				}
				para.setValue(fe.getValue());
				paras.put(String.format("#%1$s#", Validate.isNullOrEmptyOrAllSpace(fe.getAliasName())
						? member.getSqlFieldName() : fe.getAliasName()), para);
			}
		}
	}

}
