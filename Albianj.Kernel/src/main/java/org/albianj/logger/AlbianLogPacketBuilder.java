package org.albianj.logger;

import org.albianj.framework.boot.ApplicationContext;
import org.albianj.framework.boot.BundleContext;
import org.albianj.framework.boot.except.ThrowableServant;
import org.albianj.framework.boot.servants.StringServant;


public class AlbianLogPacketBuilder {
    private AlbianLoggerLevel level = AlbianLoggerLevel.Warn;
    private Class<?> refType;
    private Throwable cause;
    private boolean isThrow = false;
    private String brief;
    private String secretMsg;
    private String logMsg;
    private String sessionId;
    private String bundleName;
    private Thread refThread;

    public AlbianLogPacketBuilder(){
    }
    /**
     * 记录日志的操作者或者会话id
     * @param sessionId
     * @return
     */
    public AlbianLogPacketBuilder forSessionId(String sessionId){
       this.sessionId = sessionId;
        return this;
    }

    /**
     * 日志级别
     * @param level
     * @return
     */
    public AlbianLogPacketBuilder atLevel(AlbianLoggerLevel level){
        this.level = level;
        return this;
    }

    /**
     * 记录日志的发生地class
     * @param refType
     * @return
     */
    public AlbianLogPacketBuilder byCalled(Class<?> refType){
        this.refType = refType;
        return this;
    }

    /**
     * 日志记录发生的bundle名称
     * @param bundleName
     * @return
     */
    public AlbianLogPacketBuilder aroundBundle(String bundleName){
        this.bundleName = bundleName;
        return this;
    }

    /**
     * 当前的线程
     * @param currThread
     * @return
     */
    public AlbianLogPacketBuilder inThread(Thread currThread) {
        this.refThread = currThread;
        return this;
    }

    /**
     * 记录异常,并且是否继续抛出异常
     * @param cause
     * @return
     */
    public AlbianLogPacketBuilder withCause(Throwable cause){
        this.cause = cause;
        return this;
    }

    /**
     * 继续活着new一个异常抛出
     * 默认不会继续抛出异常
     * @param isThrow
     * @return
     */
    public AlbianLogPacketBuilder alwaysThrow(boolean isThrow){
        this.isThrow = isThrow;
        return this;
    }

    /**
     * 日志的简短说明
     * @param brief
     * @return
     */
    public AlbianLogPacketBuilder takeBrief(String brief){
        this.brief = brief;
        return this;
    }

    /**
     * 具有保密信息的日志内容
     * @param fmt
     * @param vals
     * @return
     */
    public AlbianLogPacketBuilder keepSecret(String fmt, Object... vals){
        this.secretMsg = StringServant.Instance.format(fmt,vals);
        return this;
    }

    /**
     * 日志的内容
     * @param fmt
     * @param vals
     * @return
     */
    public AlbianLogPacketBuilder addMessage(String fmt, Object... vals){
        this.logMsg = StringServant.Instance.format(fmt,vals);
        return this;
    }

    public AlbianLogPacket build(){
         if(StringServant.Instance.isNullOrEmptyOrAllSpace(sessionId)) {
             ThrowableServant.Instance.throwDisplayException(this.getClass(),
                     null,"Log Argument Error",
                     "Argument:sessionId -> [{0}]，but is must not be null,empty and all space.",
                     null == sessionId ? "NULL" : sessionId);
         }

        if(null == this.refThread) {
            this.refThread = Thread.currentThread();
        }
        if(null == this.refType ){
            this.refType = this.getClass();
        }

        String bName = "Application";
        if(StringServant.Instance.isNullOrEmptyOrAllSpace(bundleName)) {
            BundleContext ctx =  ApplicationContext.Instance.findCurrentBundleContext(this.getClass(),false);
            if(null != ctx){
                bName = ctx.getBundleName();
            }
        } else {
            bName = bundleName;
        }
        AlbianLogPacket packet = new AlbianLogPacket();
        packet.setRefThread(this.refThread);
        packet.setBrief(StringServant.Instance.isNullOrEmptyOrAllSpace(this.brief) ? "EmptyString" : this.brief);
        packet.setThrow(this.isThrow);
        packet.setCause(this.cause);
        packet.setBundle(bName);
        packet.setCalled(this.refType);
        packet.setLevel(this.level);
        packet.setMsg(StringServant.Instance.isNullOrEmptyOrAllSpace(this.logMsg) ? "EmptyString" : this.logMsg);
        packet.setSecret(StringServant.Instance.isNullOrEmptyOrAllSpace(this.secretMsg) ? "EmptyString" : this.secretMsg);
        packet.setSessionId(this.sessionId);
        return packet;
    }
}
