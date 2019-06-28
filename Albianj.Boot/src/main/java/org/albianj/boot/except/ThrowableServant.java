package org.albianj.boot.except;

import org.albianj.boot.helpers.StringServant;
import org.albianj.boot.logging.LoggerLevel;
import org.albianj.boot.tags.BundleSharingTag;

import java.util.Stack;

@BundleSharingTag
public class ThrowableServant {

    public static ThrowableServant Instance = null;

    static {
        if(null == Instance) {
            Instance = new ThrowableServant();
        }
    }

    protected ThrowableServant() {

    }

    /**
     * 分析出现异常的真正原因。一直分析到root为止。
     * 注意：返回的buffer可能具有私密性，故只能作为log日志使用，绝对不能throw出去
     * @param e
     * @return
     */
    public StringBuilder makeCauseChainBuffer(Throwable e){
        Throwable ptr = null;
        Throwable rc = e;
        Stack<Throwable> causeStack = new Stack<>();
        causeStack.push(rc); // push first interThrow
        while(null != (ptr = rc.getCause())  && (rc != ptr) ) {
            causeStack.push(ptr); // push interThrow chain
            rc = ptr;
        }

        StringBuilder sb = new StringBuilder();
        for(Throwable t : causeStack){
           sb.append(t.getClass().getSimpleName()).append(":").append(t.getMessage()).append(" >> ");
        }
        int len = sb.length();
        if(0 != len){
            sb.delete(len -4,len);
            return sb;
        }
        return null;
    }

    /**
     * 组合异常/指定发生异常时的堆栈信息
     * 发生异常的调用点为clzz参数指定
     * 当内部有异常时，clzz应为null，这样
     * @param e
     * @param refType
     * @return
     */
    public StringBuilder makeStackChainBuffer(Throwable e,Class<?> refType){
        StackTraceElement[] stes = e.getStackTrace();
        String clzzName = null;
        if(null != refType) {
            refType.getName();
        }
        StringBuilder sb = new StringBuilder();
        for(StackTraceElement ste : stes){
            if ((null != refType) && !ste.getClassName().equals(clzzName)) {
                continue;
            }
            sb.append(ste.getClassName()).append(".").append(ste.getMethodName())
                    .append("(")
                    .append(ste.getFileName()).append(":").append(ste.getLineNumber())
                    .append(") -> ");
        }
        int len = sb.length();
        if(0 != len){
            sb.delete(len -4,len);
            return sb;
        }
        return null;
    }

    public StringBuilder buildThrowBuffer(Throwable e,Class<?> refType){
        StringBuilder sb  = new StringBuilder();
        Throwable t = null;
        do {
            if (e instanceof HiddenException) {
                HiddenException he = (HiddenException) e;
                sb.append("HiddenThrow:{")
                        .append(" Breif:").append(he.getBrief())
                        .append(" HideMsg:").append(he.getHideMsg())
                        .append(" ShowMsg:").append(he.getShowMsg())
                        .append(" Stacks:").append(makeStackChainBuffer(he, refType));
                if(he.hasInterThrow()){
                    t = he.getInterThrow();
                }
                break;
            }
            if (e instanceof DisplayException) {
                DisplayException de = (DisplayException) e;
                sb.append("DisplayThrow:{")
                .append(" Breif:").append(de.getBrief())
                .append(" ShowMsg:").append(de.getShowMsg())
                .append(" Stacks:").append(makeStackChainBuffer(de, refType));
                if(de.hasInterThrow()){
                    t = de.getInterThrow();
                }
            }
        }while(false);

        /**
         * e is systemException
         */
        boolean isSystemException = false;
        if(t == null){
            t = e;
            isSystemException = true;
        }

        sb.append("SystemThrow:[")
                .append(" Cause:").append(makeCauseChainBuffer(t))
                .append(" Stacks:").append(makeStackChainBuffer(t, refType))
                .append("]");

        if(!isSystemException) {
            sb.append("}");
        }
        return sb;
    }

    public void throwDisplayException(Class<?> refType, Throwable interThrow, String brief, String fmt, Object...obj) {
        String msg = StringServant.Instance.format(fmt,obj);
        if(null == interThrow){
            throw new DisplayException(refType,brief,msg);
        }
        throw new DisplayException(refType,interThrow,brief,msg);
    }

    public void throwHiddenException(Class<?> refType, Throwable interThrow, String brief, String hideMsg, String fmt, Object... obj) {
        String msg = StringServant.Instance.format(fmt,obj);
        if(null == interThrow){
            throw new HiddenException(refType,brief,hideMsg,msg);
        }
        throw new HiddenException(refType,interThrow,brief,hideMsg,msg);
    }

}
