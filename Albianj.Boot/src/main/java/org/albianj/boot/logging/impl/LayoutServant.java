package org.albianj.boot.logging.impl;

import org.albianj.boot.except.DisplayException;
import org.albianj.boot.except.ThrowableServant;
import org.albianj.boot.except.HiddenException;
import org.albianj.boot.except.LocationInfo;
import org.albianj.boot.helpers.DailyServant;
import org.albianj.boot.helpers.StringServant;
import org.albianj.boot.logging.LoggerLevel;
import org.albianj.boot.tags.BundleSharingTag;

/**
 * 日志的记录格式
 * Time Level SessionId BundleName ThreadID Brief Msg { CallChain InnerException[CauseChain,CallChain] }
 * CallChain :
 *  Classname.MethodName(Filename:Line) >> Classname.MethodName(Filename:Line) >> Classname.MethodName(Filename:Line) ...
 *
 * CauseChain : CauseType:CauseMsg >> CauseType:CauseMsg >> CauseType:CauseMsg ...
 *
 */
@BundleSharingTag
public class LayoutServant {
    public static LayoutServant Instance = null;

    static {
        if(null == Instance) {
            Instance = new LayoutServant();
        }
    }

    protected LayoutServant() {

    }

    public String makeLayoutBuffer(String sessionId, String bundleName, LoggerLevel level, LocationInfo localInfo, String breif, String secretMsg, String msg){
        StringBuilder sb = new StringBuilder();
        sb.append(DailyServant.Instance.datetimeLongString()).append(" ").append(level.getTag())
                .append(" Session=").append(sessionId)
                .append(" Bundle=").append(bundleName)
                .append(" Thread=").append(Thread.currentThread().getId())
                .append(" Breif=").append(breif)
                .append(" Secert=").append(StringServant.Instance.isNullOrEmptyOrAllSpace(secretMsg) ? "" : secretMsg)
                .append(" Msg=").append(msg).append(" Exception=");
        Throwable e = localInfo.getThrowable();
        if(e instanceof HiddenException) {
            buildDisplayExceptionMsg( sb ,e,localInfo.getRefType());
        }else if(e instanceof DisplayException){
            buildHiddenExceptionMsg(sb,e,localInfo.getRefType());
        } else{
            buildInterExceptionMsg(e,localInfo.getRefType(),sb);
        }
        return sb.toString();
    }

    private void buildDisplayExceptionMsg(StringBuilder sb ,Throwable e,Class<?> refType){
        HiddenException ie = (HiddenException) e;
        StringBuilder eStackBuffer = ThrowableServant.Instance.makeStackChainBuffer(e,refType);
        sb.append("DisplayException:{ Msg:").append(ie.getMessageWithHide())
                .append(" Stack:").append(eStackBuffer)
                .append(" ");
        if(ie.hasInterThrow()) {
            buildInterExceptionMsg(ie.getCause(), refType,sb);
        }
        sb.append(" }").append(System.lineSeparator());
    }

    private void buildHiddenExceptionMsg(StringBuilder sb ,Throwable e,Class<?> refType){
        DisplayException ee = (DisplayException) e;
        StringBuilder eStackBuffer = ThrowableServant.Instance.makeStackChainBuffer(e,refType);

        sb.append("HiddenException:{ Msg:").append(ee.getMessage())
                .append(" ").append(eStackBuffer)
                .append(" ");

        if(ee.hasInterThrow()) {
            buildInterExceptionMsg(ee.getCause(), refType,sb);
        }
        sb.append(" }").append(System.lineSeparator());
    }

    private void buildInterExceptionMsg(Throwable interExcption,Class<?> refType, StringBuilder sb) {
//        Throwable t = origin;
        StringBuilder originCauseBuffer = ThrowableServant.Instance.makeCauseChainBuffer(interExcption);
        StringBuilder originStackBuffer = ThrowableServant.Instance.makeStackChainBuffer(interExcption, refType);
        sb.append("SystemException:[Cause:").append(originCauseBuffer)
                .append(" Stack:").append(originStackBuffer).append(" ]");
    }
}
