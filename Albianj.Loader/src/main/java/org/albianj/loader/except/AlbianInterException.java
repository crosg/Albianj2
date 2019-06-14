package org.albianj.loader.except;

/**
 * 对内的异常 具有私密属性的异常,和AlbianDisplayException正好是互补类型
 * 该异常必须经过开发人员处理,开发人员不可以直接抛出这个异常,
 * 开发人员仅可以使用日志记录下该异常的信息,如必须抛出异常,必须转换成AlbianDisplayException后抛出
 *  该异常以后将贯穿这个框架,作为整个框架的根异常
 *  同属性异常请查看:AlbianRuntimeException 与 和AlbianDisplayException
 */
public class AlbianInterException extends RuntimeException{
    private Throwable origin = null;
    private String msg = null;
    private String brief;
    private String interMsg;
    private int code = ExceptionUtil.ExceptForWarn;


    /**
     * 创建一个新异常
     * @param brief : 简短的异常描述,通常可以包括异常的id,唯一性指标,业务/模块名称等等
     * @param msg : 异常的详细信息,注意:不能包括敏感信息,包括但不限于密码,用户名,手机,身份证号,数据库信息等等
     * @param interMsg : 具有私密,敏感性质的异常信息,包括但不限于密码,用户名,手机,身份证号,数据库信息等等
     */
    public AlbianInterException(int code, String interMsg, String brief, Object... msg){
        this.brief = brief;
        this.msg = ExceptionUtil.join(msg);
        this.interMsg = interMsg;
        this.code = code;
    }

    /**
     * 使用已经抛出的异常创建一个新异常
     * @param brief : 简短的异常描述,通常可以包括异常的id,唯一性指标,业务/模块名称等等
     * @param msg : 异常的详细信息,注意:不能包括敏感信息,包括但不限于密码,用户名,手机,身份证号,数据库信息等等
     * @param interMsg : 具有私密,敏感性质的异常信息,包括但不限于密码,用户名,手机,身份证号,数据库信息等等
     * @param origin : 原先程序抛出的异常
     */
    public AlbianInterException(int code, Throwable origin, String interMsg, String brief, Object... msg){
        this.brief = brief;
        this.msg = ExceptionUtil.join(msg);
        this.origin = origin;
        this.interMsg = interMsg;
        this.code = code;
    }

    public int getCode(){
        return code;
    }

    /**
     * 该方法将得到私密的信息字段,so不能直接将此函数返回的值直接对外公开
     * 该方法的返回值只能作为记录使用
     * 如欲再向上抛出,请确保记录日志后,使用toDisplayableException方法转化后抛出
     * @return
     */
    @Override
    public String toString() {
        return ExceptionUtil.makeMessage(this.brief,this.msg,this.interMsg,this,this.origin).toString();
    }

    /**
     * 该方法将得到私密的信息字段,so不能直接将此函数返回的值直接对外公开
     * 该方法的返回值只能作为记录使用
     * 如欲再向上抛出,请确保记录日志后,使用toDisplayableException方法转化后抛出
     * @return
     */
    public String getMessage() {
        return ExceptionUtil.makeMessage(this.brief,this.msg,this.interMsg,this,this.origin).toString();
    }


    /**
     * 该方法将得到私密的信息字段,so不能直接将此函数返回的值直接对外公开
     * 该方法的返回值只能作为记录使用
     * 如欲再向上抛出,请确保记录日志后,使用toDisplayableException方法转化后抛出
     * @return
     */
    public String getLocalizedMessage() {
        return ExceptionUtil.makeMessage(this.brief,this.msg,this.interMsg,this,this.origin).toString();
    }


    /**
     * 转化到AlbianDisplayException
     * 转换的时候将会丢失私密信息(secret字段),所以请在转换之前确保已经记录下该信息,或者承担丢失的风险
     * @return AlbianDisplayableException对象
     */
    public AlbianExterException toExterException(){
        return new AlbianExterException(this.code,origin,this.brief,this.msg);
    }
}
