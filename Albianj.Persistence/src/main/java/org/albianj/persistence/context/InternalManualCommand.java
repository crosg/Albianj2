package org.albianj.persistence.context;

import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.db.PersistenceCommandType;

import java.util.Map;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public class InternalManualCommand implements IInternalManualCommand {

    private String cmdText;
    private PersistenceCommandType cmdType = PersistenceCommandType.Text;
    private Map<Integer,String> cmdParameters = null;
    private Map<String,ISqlParameter> paras = null;



    @Override
    public PersistenceCommandType getCmdType() {
        return this.cmdType;
    }

    @Override
    public void setCmdType(PersistenceCommandType cmdType) {
        this.cmdType = cmdType;
    }

    /**
     * 命令的执行参数
     *
     * @return
     */
    @Override
    public Map<Integer, String> getParameterMapper() {
        return this.cmdParameters;
    }

    /**
     * 命令的执行参数
     *
     * @param parameterMapper
     */
    @Override
    public void setParameterMapper(Map<Integer, String> parameterMapper) {
        this.cmdParameters = parameterMapper;
    }

    @Override
    public String getSqlText() {
        return this.cmdText;
    }

    /**
     * 经过正则表达式过滤后的可执行sql
     *
     * @param sql
     */
    @Override
    public void setSqlText(String sql) {
        this.cmdText = sql;
    }


    @Override
    public Map<String, ISqlParameter> getCommandParameters() {
        return this.paras;
    }

    @Override
    public void setCommandParameters(Map<String, ISqlParameter> paras) {
        this.paras = paras;
    }
}
