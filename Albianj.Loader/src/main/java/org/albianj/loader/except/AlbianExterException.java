package org.albianj.loader.except;

/**
 *
 * 对外的异常，可显示的异常,一般使用在抛出异常时需要抛出可视的信息
 * 该异常必须经过开发人员处理,开发人员可以直接抛出这个异常
 * 该异常以后将贯穿这个框架,作为整个框架的根异常(另外一个根异常是AlbianRuntimeException)
 *
 */
public class AlbianExterException extends RuntimeException {
    private Throwable origin = null;
    private String msg = null;
    private String brief;
    private int code = ExceptionUtil.ExceptForWarn;

    /**
     * 创建一个新异常
     * @param brief : 简短的异常描述,通常可以包括异常的id,唯一性指标,业务/模块名称等等
     * @param msg : 异常的详细信息,注意:不能包括敏感信息,包括但不限于密码,用户名,手机,身份证号,数据库信息等等
     */
    public AlbianExterException(int code, String brief, Object... msg){
        this.brief = brief;
        this.msg = ExceptionUtil.join(msg);
        this.code= code;
    }

    /**
     * 使用已经抛出的异常创建一个新异常
     * @param brief : 简短的异常描述,通常可以包括异常的id,唯一性指标,业务/模块名称等等
     * @param msg : 异常的详细信息,注意:不能包括敏感信息,包括但不限于密码,用户名,手机,身份证号,数据库信息等等
     * @param origin : 原先程序抛出的异常
     */
    public AlbianExterException(int code, Throwable origin, String brief, Object... msg){
        this.brief = brief;
        this.msg = ExceptionUtil.join(msg);;
        this.origin = origin;
        this.code = code;
    }

    public int getCode(){
        return code;
    }

    @Override
    public String toString() {
        return ExceptionUtil.makeMessage(this.brief,this.msg,this,this.origin).toString();
    }

    public String getMessage() {
        return ExceptionUtil.makeMessage(this.brief,this.msg,this,this.origin).toString();
    }

    public String getLocalizedMessage() {
        return ExceptionUtil.makeMessage(this.brief,this.msg,this,this.origin).toString();
    }

    public AlbianInterException toInterException(String secret){
        return new AlbianInterException(code,origin,secret,brief,msg);
    }
}
