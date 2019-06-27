package org.albianj.aop;

import org.albianj.boot.except.DisplayException;

public class RetryException extends DisplayException {
    /**
     * 创建一个新异常
     * @param brief : 简短的异常描述,通常可以包括异常的id,唯一性指标,业务/模块名称等等
     * @param showMsg : 异常的详细信息,注意:不能包括敏感信息,包括但不限于密码,用户名,手机,身份证号,数据库信息等等
     */
    public RetryException(int code, Class<?> local, String brief, String showMsg){
        super(code,local,brief,showMsg);
    }

    /**
     * 使用已经抛出的异常创建一个新异常
     * @param brief : 简短的异常描述,通常可以包括异常的id,唯一性指标,业务/模块名称等等
     * @param msg : 异常的详细信息,注意:不能包括敏感信息,包括但不限于密码,用户名,手机,身份证号,数据库信息等等
     * @param origin : 原先程序抛出的异常
     */
    public RetryException(int code, Class<?> local, Throwable interThrow, String brief, String showMsg){
        super(code,local,interThrow,brief,showMsg);
    }
}
