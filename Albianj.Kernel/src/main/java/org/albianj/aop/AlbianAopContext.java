package org.albianj.aop;

/**
 * Created by xuhaifeng on 16/7/25.
 */
public class AlbianAopContext implements IAlbianAopContext {
    private Object data = null;

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }
}
