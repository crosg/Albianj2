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

public class RemoveCommandAdapter implements IPersistenceUpdateCommand {

	public IPersistenceCommand builder(IAlbianObject object, IDataRoutersAttribute routings,
			IAlbianObjectAttribute albianObject, Map<String, Object> mapValue,
			IDataRouterAttribute routing, IStorageAttribute storage) {
		IPersistenceCommand cmd = new PersistenceCommand();
		StringBuilder text = new StringBuilder();
		
		Map<String, ISqlParameter> sqlParas = makeRomoveCommand(object, routings, albianObject, mapValue, routing,
				storage, text);
		
		
		
		StringBuilder rollbackText = new StringBuilder();
		
		Map<String, ISqlParameter> rollbackParas = CreateCommandAdapter.makeCreateCommand(object, routings, albianObject, mapValue, routing,
				storage, rollbackText);
		
		cmd.setCommandText(text.toString());
		cmd.setCommandType(PersistenceCommandType.Text);
		cmd.setParameters(sqlParas);
		
		cmd.setRollbackCommandText(rollbackText.toString());
		cmd.setRollbackCommandType(PersistenceCommandType.Text);
		cmd.setRollbackParameters(rollbackParas);
		
		PersistenceNamedParameter.parseSql(cmd);
		return cmd;
	}

	public static Map<String, ISqlParameter> makeRomoveCommand(IAlbianObject object, IDataRoutersAttribute routings,
			IAlbianObjectAttribute albianObject, Map<String, Object> mapValue, IDataRouterAttribute routing,
			IStorageAttribute storage, StringBuilder text) {
		StringBuilder where = new StringBuilder();
		text.append("DELETE FROM ");// .append(routing.getTableName());
		String tableName = null;
		if (null != routings && null != routings.getDataRouter()) {
			tableName = routings.getDataRouter().mappingWriterTable(routing,
					object);
		}
		tableName = Validate.isNullOrEmptyOrAllSpace(tableName) ? routing
				.getTableName() : tableName;
		if (storage.getDatabaseStyle() == PersistenceDatabaseStyle.MySql) {
			text.append("`").append(tableName).append("`");
		} else {
			text.append("[").append(tableName).append("]");
		}

		Map<String, IMemberAttribute> mapMemberAttributes = albianObject
				.getMembers();
		Map<String, ISqlParameter> sqlParas = new HashMap<String, ISqlParameter>();
		for (Map.Entry<String, IMemberAttribute> entry : mapMemberAttributes
				.entrySet()) {
			IMemberAttribute member = entry.getValue();
			if (!member.getIsSave() || !member.getPrimaryKey())
				continue;
			ISqlParameter para = new SqlParameter();
			para.setName(member.getName());
			para.setSqlFieldName(member.getSqlFieldName());
			para.setSqlType(member.getDatabaseType());
			para.setValue(mapValue.get(member.getName()));
			sqlParas.put(String.format("#%1$s#", member.getSqlFieldName()),
					para);

			where.append(" AND ");
			if (storage.getDatabaseStyle() == PersistenceDatabaseStyle.MySql) {
				where.append("`").append(member.getSqlFieldName()).append("`");
			} else {
				where.append("[").append( member.getSqlFieldName()).append("]");
			}
			where.append(" = ").append("#").append(member.getSqlFieldName())
					.append("# ");
		}

		text.append(" WHERE 1=1 ").append(where);
		return sqlParas;
	}
	
	public IPersistenceCommand builder(IAlbianObject object, IDataRoutersAttribute routings, IAlbianObjectAttribute albianObject,
			Map<String, Object> mapValue, IDataRouterAttribute routing, IStorageAttribute storage, String[] members) throws NoSuchMethodException{
		throw new NoSuchMethodException();
	}

	

}
