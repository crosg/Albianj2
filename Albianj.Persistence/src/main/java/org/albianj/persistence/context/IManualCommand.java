package org.albianj.persistence.context;

import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.db.PersistenceCommandType;

import java.util.Map;

/**
 * Created by xuhaifeng on 17/8/18.
 */
public interface IManualCommand {

    String getCommandText();

    void setCommandText(String sqlText);

    PersistenceCommandType getCmdType();

    void setCmdType(PersistenceCommandType cmdType);

    Map<String,ISqlParameter> getCommandParameters();

    void setCommandParameters(Map<String,ISqlParameter> paras);



}
