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

    @BundleSharingTag
    public class Code{
        /**
         * 异常级别,表示正常的异常,可能只是一个过程的需要,或者用来控制一个程序的流程
         */
        public static final int Normal = 10;
        /**
         * 警告的异常,通常对程序无实质性影响,一把会使用默认值等处理掉或者容错机制处理掉
         */
        public static final int Warn = 20;
        /**
         * 错误的异常,程序无法对该异常做出任何可修正的措施,程序必须中断或者停止
         */
        public static final int Error = 30;
        /**
         * 无比重要的异常,比刑爷还要重要的异常,必须引起所有人的注意,不管什么程序都需要12w分警惕
         */
        public static final int Mark = 40;
    }

    public int logLevel2Code(LoggerLevel level){
        int levelCode = level.getLevel();
        if(levelCode < 20){
            return Code.Normal;
        } else if(levelCode == 30){
            return Code.Warn;
        }else if(levelCode == 40 || levelCode == 50){
            return Code.Error;
        } else {
            return Code.Mark;
        }
    }


    public StringBuilder makeMessage(String brief, String msg, Exception self, Throwable origin) {
        return makeMessage(brief, msg, null, self, origin);
    }

    public StringBuilder makeMessage(String brief, String msg, String secret, Exception self, Throwable origin) {
        StringBuilder sb = new StringBuilder();
        sb.append("brief -> ").append(((null == brief) || (0 == brief.trim().length())) ? "EMPTY" : brief)
                .append(" DisplayMsg -> ").append(((null == msg) || (0 == msg.trim().length())) ? "EMPTY" : msg);
        if (!((null == secret) || (0 == secret.trim().length()))) {
            sb.append(" HiddenMsg -> ").append(secret);
        }
        sb.append(" SelfStacks -> ").append(makeStackBuffer(self.getStackTrace()));
        if (null != origin) {
            if (!origin.getClass().isAssignableFrom(self.getClass())) {
                String orgMsg = origin.getMessage();
                sb.append(" SystemMessage -> ").append(((null == orgMsg) || (0 == orgMsg.trim().length())) ? "EMPTY" : orgMsg);
            }
            sb.append(" SystemStacks ->").append(makeStackBuffer(origin.getStackTrace()));
        }
        return sb;
    }

    private StringBuilder makeStackBuffer(StackTraceElement[] stacks) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");

        for(StackTraceElement ste : stacks){
            sb.append(ste.getClassName()).append(".") .append(ste.getMethodName())
                    .append("@").append(ste.getFileName()).append(":").append(ste.getLineNumber())
            .append(" >> ");
        }

        int len = sb.length();
        if(2 != len){
            sb.delete(len -3,len);
            return sb;
        }
        sb.append("}");
        return sb;
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

    public void throwDisplayException(int code, Class<?> refType, Throwable interThrow, String brief, String fmt, Object...obj) {
        String msg = StringServant.Instance.format(fmt,obj);
        if(null == interThrow){
            throw new DisplayException(code,refType,brief,msg);
        }
        throw new DisplayException(code,refType,interThrow,brief,msg);
    }

    public void throwHiddenException(int code, Class<?> refType, Throwable interThrow, String brief, String hideMsg, String fmt, Object... obj) {
        String msg = StringServant.Instance.format(fmt,obj);
        if(null == interThrow){
            throw new HiddenException(code,refType,brief,hideMsg,msg);
        }
        throw new HiddenException(code,refType,interThrow,brief,hideMsg,msg);
    }

}
