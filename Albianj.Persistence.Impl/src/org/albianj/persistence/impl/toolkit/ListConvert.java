package org.albianj.persistence.impl.toolkit;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.object.ICondition;
import org.albianj.verify.Validate;

public class ListConvert {
	public static <T extends ICondition> Map<String, T> toLinkedHashMap(
			LinkedList<T> filters) {
		if (null == filters) {
			return null;
		}
		int len = filters.size();
		if (0 == len) {
			return new LinkedHashMap<String, T>(0);
		}
		Map<String, T> map = new LinkedHashMap<String, T>(len);
		for (T filter : filters) {
			map.put(Validate.isNullOrEmptyOrAllSpace(filter.getAliasName()) ? filter.getFieldName() : filter.getAliasName() , filter);
			//map.put(filter.getFieldName(), filter);
		}
		return map;
	}

	public static <T extends ICondition> Map<String, T> toLinkedHashMap(
			List<T> filters) {
		if (null == filters) {
			return null;
		}
		int size = filters.size();
		if (0 == size) {
			return new LinkedHashMap<String, T>(0);
		}
		Map<String, T> map = new LinkedHashMap<String, T>(size);
		for (T filter : filters) {
			map.put(Validate.isNullOrEmptyOrAllSpace(filter.getAliasName()) ? filter.getFieldName() : filter.getAliasName() , filter);
		//	map.put(filter.getFieldName(), filter);
		}
		return map;
	}
	
	public static String toString(Map<String,ISqlParameter> paras) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, ISqlParameter> para : paras.entrySet()) {
			ISqlParameter sp = para.getValue();
			//sb.append(sp.getSqlFieldName()).append(" = ")
			sb.append(para.getKey()).append(" = ")
			.append(ResultConvert.sqlValueToString(sp.getSqlType(),sp.getValue()))
			.append(" ");
		}
		return sb.toString();
	}

	// public static Map<String,IOrderByCondition>
	// convertOrderByConditions(LinkedList<IOrderByCondition> orderbys)
	// {
	// if(null == orderbys)
	// {
	// return null;
	// }
	// int len = orderbys.size();
	// if(0 == len)
	// {
	// return new LinkedHashMap<String, IOrderByCondition>(0);
	// }
	// Map<String,IOrderByCondition> map =new LinkedHashMap<String,
	// IOrderByCondition>(len);
	// for(IOrderByCondition filter : orderbys)
	// {
	// map.put(filter.getFieldName(), filter);
	// }
	// return map;
	// }
	//
	// public static Map<String,IOrderByCondition>
	// convertOrderByConditions(List<IOrderByCondition> orderbys)
	// {
	// if(null == orderbys)
	// {
	// return null;
	// }
	// int size = orderbys.size();
	// if(0 == size)
	// {
	// return new LinkedHashMap<String, IOrderByCondition>(0);
	// }
	// Map<String,IOrderByCondition> map =new LinkedHashMap<String,
	// IOrderByCondition>(size);
	// for(IOrderByCondition filter : orderbys)
	// {
	// map.put(filter.getFieldName(), filter);
	// }
	// return map;
	// }

	// public static IFilterCondition[]
	// convertFilterConditions(LinkedList<IFilterCondition> filters){
	//
	// }

}
