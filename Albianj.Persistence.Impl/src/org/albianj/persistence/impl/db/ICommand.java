package org.albianj.persistence.impl.db;

import java.util.Map;

import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.db.PersistenceCommandType;


public interface ICommand {
	public String getCommandText();

	public void setCommandText(String commandText);

	public PersistenceCommandType getCommandType();

	public void setCommandType(PersistenceCommandType commandType);

	public Map<Integer, String> getParameterMapper();

	public void setParameterMapper(Map<Integer, String> parameterMapper);

	public Map<String, ISqlParameter> getParameters();

	public void setParameters(Map<String, ISqlParameter> parameters);
}
