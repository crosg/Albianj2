package org.albianj.persistence.impl.db;

import java.util.Map;

import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.db.PersistenceCommandType;

public class PersistenceCommand implements IPersistenceCommand {
	private String commandText = null;
	private PersistenceCommandType commandType = PersistenceCommandType.Text;
	private Map<String, ISqlParameter> paramsters = null;
	private Map<Integer, String> parameterMapper = null;
	
	private String rollbackCommandText = null;
	private PersistenceCommandType rollbackCommandType = PersistenceCommandType.Text;
	private Map<String, ISqlParameter> rollbackParamsters = null;
	private Map<Integer, String> rollbackParameterMapper = null;

	public String getCommandText() {
		// TODO Auto-generated method stub
		return this.commandText;
	}

	public void setCommandText(String commandText) {
		// TODO Auto-generated method stub
		this.commandText = commandText;
	}

	public PersistenceCommandType getCommandType() {
		// TODO Auto-generated method stub
		return this.commandType;
	}

	public void setCommandType(PersistenceCommandType commandType) {
		// TODO Auto-generated method stub
		this.commandType = commandType;
	}

	public Map<Integer, String> getParameterMapper() {
		return this.parameterMapper;
	}

	public void setParameterMapper(Map<Integer, String> parameterMapper) {
		this.parameterMapper = parameterMapper;
	}

	public Map<String, ISqlParameter> getParameters() {
		// TODO Auto-generated method stub
		return this.paramsters;
	}

	public void setParameters(Map<String, ISqlParameter> parameters) {
		// TODO Auto-generated method stub
		this.paramsters = parameters;
	}

	
	
	
	public String getRollbackCommandText() {
		// TODO Auto-generated method stub
		return this.rollbackCommandText;
	}

	public void setRollbackCommandText(String commandText) {
		// TODO Auto-generated method stub
		this.rollbackCommandText = commandText;
	}

	public PersistenceCommandType getRollbackCommandType() {
		// TODO Auto-generated method stub
		return this.rollbackCommandType;
	}

	public void setRollbackCommandType(PersistenceCommandType commandType) {
		// TODO Auto-generated method stub
		this.rollbackCommandType = commandType;
	}

	public Map<Integer, String> getRollbackParameterMapper() {
		return this.rollbackParameterMapper;
	}

	public void setRollbackParameterMapper(Map<Integer, String> parameterMapper) {
		this.rollbackParameterMapper = parameterMapper;
	}

	public Map<String, ISqlParameter> getRollbackParameters() {
		// TODO Auto-generated method stub
		return this.rollbackParamsters;
	}

	public void setRollbackParameters(Map<String, ISqlParameter> parameters) {
		// TODO Auto-generated method stub
		this.rollbackParamsters = parameters;
	}

	

}
