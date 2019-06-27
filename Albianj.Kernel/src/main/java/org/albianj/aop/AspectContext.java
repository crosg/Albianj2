package org.albianj.aop;

/**
 * aop的上下文
 */
public class AspectContext implements IAspectContext {
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
