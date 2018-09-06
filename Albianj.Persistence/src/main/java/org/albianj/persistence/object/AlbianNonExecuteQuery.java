package org.albianj.persistence.object;

import org.albianj.persistence.object.filter.IChainExpression;

/**
 * Created by xuhaifeng on 16/12/28.
 */
public class AlbianNonExecuteQuery implements IAlbianNonExecuteQuery {
    private Class<? extends  IAlbianObject> cla;
    private String innerCommandText;
    private IChainExpression expression;

    @Override
    public Class<? extends IAlbianObject> getAlbianObjectClass() {
        return this.cla;
    }

    @Override
    public void setAlbianObjectClass(Class<? extends IAlbianObject> albianObjectClass) {
        this.cla = albianObjectClass;
    }

    @Override
    public String getInnerCommandText() {
        return this.innerCommandText;
    }

    @Override
    public void setInnerCommandText(String innerCommandText) {
        this.innerCommandText = innerCommandText;
    }

    @Override
    public IChainExpression getCommandFilter() {
        return this.expression;
    }

    @Override
    public void setCommandFilter(IChainExpression commandFilter) {
        this.expression = commandFilter;
    }
}
