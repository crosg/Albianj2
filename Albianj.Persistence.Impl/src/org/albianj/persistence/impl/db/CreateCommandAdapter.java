package org.albianj.persistence.impl.db;

import java.util.HashMap;
import java.util.Map;

import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.db.AlbianDataServiceException;
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
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

public class CreateCommandAdapter implements IPersistenceUpdateCommand {

	public IPersistenceCommand builder(String sessionId,IAlbianObject object, IDataRoutersAttribute routings,
			IAlbianObjectAttribute albianObject, Map<String, Object> mapValue,
			IDataRouterAttribute routing, IStorageAttribute storage) throws AlbianDataServiceException{
		
		if(!object.getIsAlbianNew()) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, "DataService is error.",
					"the loaded albianj object can not be insert.please new the object from database first. job id:%s.", sessionId);
		}
		
		IPersistenceCommand cmd = new PersistenceCommand();
		StringBuilder sqlText = new StringBuilder();
				
		Map<String, ISqlParameter> sqlParas = makeCreateCommand(object, routings, albianObject, mapValue, routing,
				storage, sqlText);
		
		StringBuilder rollbackText = new StringBuilder();
		Map<String, ISqlParameter> rollbackParas = RemoveCommandAdapter.makeRomoveCommand(sessionId,object, routings, albianObject, mapValue, routing,
				storage, rollbackText);
		
		cmd.setCommandText(sqlText.toString());
		cmd.setCommandType(PersistenceCommandType.Text);
		cmd.setParameters(sqlParas);
		
		cmd.setRollbackCommandText(rollbackText.toString());
		cmd.setRollbackCommandType(PersistenceCommandType.Text);
		cmd.setRollbackParameters(rollbackParas);
		
		PersistenceNamedParameter.parseSql(cmd);
		return cmd;
	}

	public static Map<String, ISqlParameter> makeCreateCommand(IAlbianObject object, IDataRoutersAttribute routings,
			IAlbianObjectAttribute albianObject, Map<String, Object> mapValue, IDataRouterAttribute routing,
			IStorageAttribute storage, StringBuilder sqlText) {
		StringBuilder cols = new StringBuilder();
		StringBuilder paras = new StringBuilder();

		sqlText.append("INSERT INTO ");// .append(routing.getTableName());
		String tableName = null;
		if (null != routings && null != routings.getDataRouter()) {
			tableName = routings.getDataRouter().mappingWriterTable(routing,
					object);
		}
		tableName = Validate.isNullOrEmptyOrAllSpace(tableName) ? routing
				.getTableName() : tableName;
		if (storage.getDatabaseStyle() == PersistenceDatabaseStyle.MySql) {
			sqlText.append("`").append(tableName).append("`");
		} else {
			sqlText.append("[").append(tableName).append("]");
		}

		Map<String, IMemberAttribute> mapMemberAttributes = albianObject
				.getMembers();
		Map<String, ISqlParameter> sqlParas = new HashMap<String, ISqlParameter>();
		for (Map.Entry<String, IMemberAttribute> entry : mapMemberAttributes
				.entrySet()) {
			IMemberAttribute member = entry.getValue();
			if (!member.getIsSave())
				continue;
			ISqlParameter para = new SqlParameter();
			para.setName(member.getName());
			para.setSqlFieldName(member.getSqlFieldName());
			para.setSqlType(member.getDatabaseType());
			para.setValue(mapValue.get(member.getName()));
			sqlParas.put(String.format("#%1$s#", member.getSqlFieldName()),
					para);
			if (storage.getDatabaseStyle() == PersistenceDatabaseStyle.MySql) {
				cols.append("`").append(member.getSqlFieldName()).append("`");
			} else {
				cols.append("[").append( member.getSqlFieldName()).append("]");
			}
			cols.append(",");
			paras.append("#").append(member.getSqlFieldName()).append("# ,");
		}
		if (0 < cols.length()) {
			cols.deleteCharAt(cols.length() - 1);
		}
		if (0 < paras.length()) {
			paras.deleteCharAt(paras.length() - 1);
		}
		sqlText.append(" (").append(cols).append(") ").append("VALUES (")
				.append(paras).append(") ");
		return sqlParas;
	}
	
	public IPersistenceCommand builder(String sessionId,IAlbianObject object, IDataRoutersAttribute routings, IAlbianObjectAttribute albianObject,
			Map<String, Object> mapValue, IDataRouterAttribute routing, IStorageAttribute storage, String[] members)throws NoSuchMethodException{
		throw new NoSuchMethodException("no the service");
	}

}
