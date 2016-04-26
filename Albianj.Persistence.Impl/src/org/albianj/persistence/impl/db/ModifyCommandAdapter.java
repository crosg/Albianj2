package org.albianj.persistence.impl.db;

import java.util.HashMap;
import java.util.Map;

import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.db.PersistenceCommandType;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.persistence.object.IDataRoutersAttribute;
import org.albianj.persistence.object.IMemberAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.object.PersistenceDatabaseStyle;
import org.albianj.verify.Validate;

public class ModifyCommandAdapter implements IPersistenceUpdateCommand {

	public IPersistenceCommand builder(IAlbianObject object, IDataRoutersAttribute routings, IAlbianObjectAttribute albianObject,
			Map<String, Object> mapValue, IDataRouterAttribute routing, IStorageAttribute storage) {

		IPersistenceCommand cmd = new PersistenceCommand();
		StringBuilder text = new StringBuilder();
		StringBuilder cols = new StringBuilder();
		StringBuilder where = new StringBuilder();
		
		StringBuilder rollbackText = new StringBuilder();
		StringBuilder rollbackCols = new StringBuilder();
		StringBuilder rollbackWhere = new StringBuilder();
		
		
		text.append("UPDATE ");// .append(routing.getTableName());
		rollbackText.append("UPDATE ");// .append(routing.getTableName());
		String tableName = null;
		if (null != routings && null != routings.getDataRouter()) {
			tableName = routings.getDataRouter().mappingWriterTable(routing, object);
		}
		tableName = Validate.isNullOrEmptyOrAllSpace(tableName) ? routing.getTableName() : tableName;
		if (storage.getDatabaseStyle() == PersistenceDatabaseStyle.MySql) {
			text.append("`").append(tableName).append("`");
			rollbackText.append("`").append(tableName).append("`");
		} else {
			text.append("[").append(tableName).append("]");
			rollbackText.append("[").append(tableName).append("]");
		}

		Map<String, IMemberAttribute> mapMemberAttributes = albianObject.getMembers();
		Map<String, ISqlParameter> sqlParas = new HashMap<String, ISqlParameter>();
		Map<String, ISqlParameter> rollbackParas = new HashMap<String, ISqlParameter>();
		for (Map.Entry<String, IMemberAttribute> entry : mapMemberAttributes.entrySet()) {
			IMemberAttribute member = entry.getValue();
			if (!member.getIsSave())
				continue;
			String name = member.getName();
			Object newValue = mapValue.get(name);
			Object oldValue = null;

			if (member.getPrimaryKey()) {
				where.append(" AND ");
				rollbackWhere.append(" AND ");
				if (storage.getDatabaseStyle() == PersistenceDatabaseStyle.MySql) {
					where.append("`").append(member.getSqlFieldName()).append("`");
					rollbackWhere.append("`").append(member.getSqlFieldName()).append("`");
				} else {
					where.append("[").append( member.getSqlFieldName()).append("]");
					rollbackWhere.append("[").append( member.getSqlFieldName()).append("]");
				}
				where.append(" = ").append("#").append(member.getSqlFieldName()).append("# ");
				rollbackWhere.append(" = ").append("#").append(member.getSqlFieldName()).append("# ");
			} else {
				// cols
				 oldValue = object.getOldAlbianObject(name);
				if ((null == newValue && null == oldValue)) {
					continue;
				}
				if (null != newValue && newValue.equals(oldValue)) {
					continue;
				}
				if (null != oldValue && oldValue.equals(newValue)) {
					continue;
				}
				if (storage.getDatabaseStyle() == PersistenceDatabaseStyle.MySql) {
					cols.append("`").append(member.getSqlFieldName()).append("`");
					rollbackCols.append("`").append(member.getSqlFieldName()).append("`");
				} else {
					cols.append("[").append( member.getSqlFieldName()).append("]");
					rollbackCols.append("[").append( member.getSqlFieldName()).append("]");
				}
				cols.append(" = ").append("#").append(member.getSqlFieldName()).append("# ,");
				rollbackCols.append(" = ").append("#").append(member.getSqlFieldName()).append("# ,");
			}
			ISqlParameter para = new SqlParameter();
			para.setName(name);
			para.setSqlFieldName(member.getSqlFieldName());
			para.setSqlType(member.getDatabaseType());
			para.setValue(newValue);
			
			ISqlParameter rollbackPara = new SqlParameter();
			rollbackPara.setName(name);
			rollbackPara.setSqlFieldName(member.getSqlFieldName());
			rollbackPara.setSqlType(member.getDatabaseType());
			rollbackPara.setValue(oldValue);
			
			sqlParas.put(String.format("#%1$s#", member.getSqlFieldName()), para);
			rollbackParas.put(String.format("#%1$s#", member.getSqlFieldName()), rollbackPara);
		}

		if (0 == cols.length())
			return null;// no the upload operator
		if (0 < cols.length()) {
			cols.deleteCharAt(cols.length() - 1);
			rollbackCols.deleteCharAt(cols.length() - 1);
		}

		text.append(" SET ").append(cols).append(" WHERE 1=1 ").append(where);
		rollbackText.append(" SET ").append(rollbackCols).append(" WHERE 1=1 ").append(rollbackWhere);
		
		cmd.setCommandText(text.toString());
		cmd.setCommandType(PersistenceCommandType.Text);
		cmd.setParameters(sqlParas);
		
		cmd.setRollbackCommandText(rollbackText.toString());
		cmd.setRollbackCommandType(PersistenceCommandType.Text);
		cmd.setRollbackParameters(rollbackParas);
		
		PersistenceNamedParameter.parseSql(cmd);
		return cmd;
	}

	public IPersistenceCommand builder(IAlbianObject object, IDataRoutersAttribute routings, IAlbianObjectAttribute albianObject,
			Map<String, Object> mapValue, IDataRouterAttribute routing, IStorageAttribute storage, String[] members) {

		IPersistenceCommand cmd = new PersistenceCommand();
		StringBuilder text = new StringBuilder();
		StringBuilder cols = new StringBuilder();
		StringBuilder where = new StringBuilder();
		text.append("UPDATE ");// .append(routing.getTableName());
		String tableName = null;
		if (null != routings && null != routings.getDataRouter()) {
			tableName = routings.getDataRouter().mappingWriterTable(routing, object);
		}
		tableName = Validate.isNullOrEmptyOrAllSpace(tableName) ? routing.getTableName() : tableName;
		if (storage.getDatabaseStyle() == PersistenceDatabaseStyle.MySql) {
			text.append("`").append(tableName).append("`");
		} else {
			text.append("[").append(tableName).append("]");
		}

		Map<String, IMemberAttribute> mapMemberAttributes = albianObject.getMembers();
		Map<String, ISqlParameter> sqlParas = new HashMap<String, ISqlParameter>();
		for (String m : members) {
			IMemberAttribute member = mapMemberAttributes.get(m.toLowerCase());
			if (!member.getIsSave())
				continue;
			String name = member.getName();
			Object newValue = mapValue.get(name);
			if (storage.getDatabaseStyle() == PersistenceDatabaseStyle.MySql) {
				cols.append("`").append(member.getSqlFieldName()).append("`");
			} else {
				cols.append("[").append( member.getSqlFieldName()).append("]");
			}
			cols.append(" = ").append("#").append(member.getSqlFieldName()).append("# ,");

			ISqlParameter para = new SqlParameter();
			para.setName(name);
			para.setSqlFieldName(member.getSqlFieldName());
			para.setSqlType(member.getDatabaseType());
			para.setValue(newValue);
			sqlParas.put(String.format("#%1$s#", member.getSqlFieldName()), para);
		}

		for (Map.Entry<String, IMemberAttribute> entry : mapMemberAttributes.entrySet()) {
			IMemberAttribute member = entry.getValue();
			if (!member.getIsSave())
				continue;
			String name = member.getName();
			Object newValue = mapValue.get(name);

			if (member.getPrimaryKey()) {
				where.append(" AND ");
				if (storage.getDatabaseStyle() == PersistenceDatabaseStyle.MySql) {
					where.append("`").append(member.getSqlFieldName()).append("`");
				} else {
					where.append("[").append( member.getSqlFieldName()).append("]");
				}
				where.append(" = ").append("#").append(member.getSqlFieldName()).append("# ");
			}
			ISqlParameter para = new SqlParameter();
			para.setName(name);
			para.setSqlFieldName(member.getSqlFieldName());
			para.setSqlType(member.getDatabaseType());
			para.setValue(newValue);
			sqlParas.put(String.format("#%1$s#", member.getSqlFieldName()), para);
		}

		if (0 == cols.length())
			return null;// no the upload operator
		if (0 < cols.length()) {
			cols.deleteCharAt(cols.length() - 1);
		}
		// if(0 == where.length()){
		// throw new Exception("no the primary key for the update command");
		// }
		text.append(" SET ").append(cols).append(" WHERE 1=1 ").append(where);
		cmd.setCommandText(text.toString());
		cmd.setCommandType(PersistenceCommandType.Text);
		cmd.setParameters(sqlParas);
		PersistenceNamedParameter.parseSql(cmd);
		return cmd;
	}

}
