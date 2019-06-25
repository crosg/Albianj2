package org.albianj.loader.logging.impl;

import org.albianj.loader.except.AlbianExceptionServant;
import org.albianj.loader.except.AlbianExterException;
import org.albianj.loader.except.AlbianInterException;
import org.albianj.loader.except.AlbianLocationInfo;
import org.albianj.loader.helpers.AlbianDailyServant;
import org.albianj.loader.helpers.AlbianStringServant;
import org.albianj.loader.logging.AlbianLoggerLevel;

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
        if(e instanceof AlbianInterException) {
            buildInterExceptionMsg( sb ,e,localInfo.getLocal());
        }else if(e instanceof AlbianExterException){
            buildExterExceptionMsg(sb,e,localInfo.getLocal());
        } else{
            buildOriginExceptionMsg(e,sb);
        }
        return sb.toString();
    }

    private void buildInterExceptionMsg(StringBuilder sb ,Throwable e,Class<?> localClzz){
        AlbianInterException ie = (AlbianInterException) e;
        StringBuilder eStackBuffer = AlbianExceptionServant.Instance.makeStackChainBuffer(e,localClzz);
        sb.append("InterException:{ Msg:").append(ie.getInterMessage())
                .append(" Stack:").append(eStackBuffer)
                .append(" ");
        if(ie.hasCause()) {
            buildOriginExceptionMsg(ie.getCause(), sb);
        }
        sb.append(" }").append(System.lineSeparator());
    }

    private void buildExterExceptionMsg(StringBuilder sb ,Throwable e,Class<?> localClzz){
        AlbianExterException ee = (AlbianExterException) e;
        StringBuilder eStackBuffer = AlbianExceptionServant.Instance.makeStackChainBuffer(e,localClzz);

        sb.append("ExterException:{ Msg:").append(ee.getMessage())
                .append(" ").append(eStackBuffer)
                .append(" ");

        if(ee.hasCause()) {
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
