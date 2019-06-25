package org.albianj.boot.logging.impl;

import org.albianj.boot.except.AlbianDisplayException;
import org.albianj.boot.except.AlbianExceptionServant;
import org.albianj.boot.except.AlbianHiddenException;
import org.albianj.boot.except.AlbianLocationInfo;
import org.albianj.boot.helpers.AlbianDailyServant;
import org.albianj.boot.helpers.AlbianStringServant;
import org.albianj.boot.logging.AlbianLoggerLevel;

/**
 * 日志的记录格式
 * Time Level SessionId BundleName ThreadID Brief Msg { CallChain InnerException[CauseChain,CallChain] }
 * CallChain :
 *  Classname.MethodName(Filename:Line) >> Classname.MethodName(Filename:Line) >> Classname.MethodName(Filename:Line) ...
 *
 * CauseChain : CauseType:CauseMsg >> CauseType:CauseMsg >> CauseType:CauseMsg ...
 *
 */
public class AlbianLayoutServant {
    public static AlbianLayoutServant Instance = null;

    static {
        if(null == Instance) {
            Instance = new AlbianLayoutServant();
        }
    }

    protected AlbianLayoutServant() {

    }

    public String makeLayoutBuffer(String sessionId,String bundleName,AlbianLoggerLevel level, AlbianLocationInfo localInfo,String breif,String secretMsg, String msg){
        StringBuilder sb = new StringBuilder();
        sb.append(AlbianDailyServant.Instance.datetimeLongString()).append(" ").append(level.getTag())
                .append(" Session:").append(sessionId)
                .append(" Bundle:").append(bundleName)
                .append(" Thread:").append(Thread.currentThread().getId())
                .append(" Breif:").append(breif)
                .append(" Secert:").append(AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(secretMsg) ? "" : secretMsg)
                .append(" Msg:").append(msg).append(" Exception:");
        Throwable e = localInfo.getException();
        if(e instanceof AlbianHiddenException) {
            buildInterExceptionMsg( sb ,e,localInfo.getLocal());
        }else if(e instanceof AlbianDisplayException){
            buildExterExceptionMsg(sb,e,localInfo.getLocal());
        } else{
            buildOriginExceptionMsg(e,sb);
        }
        return sb.toString();
    }

    private void buildInterExceptionMsg(StringBuilder sb ,Throwable e,Class<?> localClzz){
        AlbianHiddenException ie = (AlbianHiddenException) e;
        StringBuilder eStackBuffer = AlbianExceptionServant.Instance.makeStackChainBuffer(e,localClzz);
        sb.append("InterException:{ Msg:").append(ie.getMessageWithHide())
                .append(" Stack:").append(eStackBuffer)
                .append(" ");
        if(ie.hasInterThrow()) {
            buildOriginExceptionMsg(ie.getCause(), sb);
        }
        sb.append(" }").append(System.lineSeparator());
    }

    private void buildExterExceptionMsg(StringBuilder sb ,Throwable e,Class<?> localClzz){
        AlbianDisplayException ee = (AlbianDisplayException) e;
        StringBuilder eStackBuffer = AlbianExceptionServant.Instance.makeStackChainBuffer(e,localClzz);

        sb.append("ExterException:{ Msg:").append(ee.getMessage())
                .append(" ").append(eStackBuffer)
                .append(" ");

        if(ee.hasInterThrow()) {
            buildOriginExceptionMsg(ee.getCause(), sb);
        }
        sb.append(" }").append(System.lineSeparator());
    }

    private void buildOriginExceptionMsg(Throwable origin, StringBuilder sb) {
        Throwable t = origin;
        StringBuilder originCauseBuffer = AlbianExceptionServant.Instance.makeCauseChainBuffer(t);
        StringBuilder originStackBuffer = AlbianExceptionServant.Instance.makeStackChainBuffer(t, null);
        sb.append("OriginException:[Cause:").append(originCauseBuffer)
                .append(" Stack:").append(originStackBuffer).append(" ]");
    }
}
