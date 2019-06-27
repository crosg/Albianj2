package org.albianj.boot.except;

import org.albianj.boot.tags.BundleSharingTag;

/**
 * 对内的异常 具有私密属性的异常,和AlbianDisplayException正好是互补类型
 * 该异常必须经过开发人员处理,开发人员不可以直接抛出这个异常,
 * 开发人员仅可以使用日志记录下该异常的信息,如必须抛出异常,必须转换成AlbianDisplayException后抛出
 * 该异常以后将贯穿这个框架,作为整个框架的根异常
 * 同属性异常请查看:AlbianRuntimeException 与 和AlbianDisplayException
 */
@BundleSharingTag
public class HiddenException extends DisplayException {
    private String hideMsg;


    /**
     * 创建一个新异常
     *
     * @param brief    : 简短的异常描述,通常可以包括异常的id,唯一性指标,业务/模块名称等等
     * @param showMsg      : 异常的详细信息,注意:不能包括敏感信息,包括但不限于密码,用户名,手机,身份证号,数据库信息等等
     * @param hideMsg : 具有私密,敏感性质的异常信息,包括但不限于密码,用户名,手机,身份证号,数据库信息等等
     */
    public HiddenException(int code, Class<?> local, String brief, String hideMsg, String showMsg) {
        super(code,local,brief,showMsg);
        this.hideMsg = hideMsg;
    }

    /**
     * 使用已经抛出的异常创建一个新异常
     *
     * @param brief    : 简短的异常描述,通常可以包括异常的id,唯一性指标,业务/模块名称等等
     * @param showMsg      : 异常的详细信息,注意:不能包括敏感信息,包括但不限于密码,用户名,手机,身份证号,数据库信息等等
     * @param hideMsg : 具有私密,敏感性质的异常信息,包括但不限于密码,用户名,手机,身份证号,数据库信息等等
     * @param interThrow   : 原先程序抛出的异常
     */
    public HiddenException(int code, Class<?> local, Throwable interThrow, String brief, String hideMsg, String showMsg) {
        super(code,local,interThrow,brief,showMsg);
        this.hideMsg = hideMsg;
    }

    /**
     * 该方法将得到私密的信息字段,so不能直接将此函数返回的值直接对外公开
     * 该方法的返回值只能作为记录使用
     * 如欲再向上抛出,请确保记录日志后,使用toDisplayableException方法转化后抛出
     *
     * @return
     */
    public String getLocalizedMessage() {
        return ThrowableServant.Instance.makeMessage(this.brief, this.showMsg, this, this.interThrow).toString();
    }

    public String getMessageWithHide() {
        return ThrowableServant.Instance.makeMessage(this.brief, this.showMsg, this.hideMsg, this, this.interThrow).toString();
    }
}
