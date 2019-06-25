package org.albianj.boot.except;

import org.albianj.boot.helpers.AlbianStringServant;
import org.albianj.boot.logging.AlbianLoggerLevel;

import java.io.File;
import java.util.Stack;


public class AlbianExceptionServant {

    public static AlbianExceptionServant Instance = null;

    static {
        if(null == Instance) {
            Instance = new AlbianExceptionServant();
        }
    }

    protected AlbianExceptionServant() {

    }

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

    public int logLevel2Code(AlbianLoggerLevel level){
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
                .append(" showMsg -> ").append(((null == msg) || (0 == msg.trim().length())) ? "EMPTY" : msg);
        if (!((null == secret) || (0 == secret.trim().length()))) {
            sb.append(" Secret -> ").append(secret);
        }
        sb.append(" SelfStacks -> ").append(makeStackBuffer(self.getStackTrace()));
        if (null != origin) {
            if (!origin.getClass().isAssignableFrom(self.getClass())) {
                String orgMsg = origin.getMessage();
                sb.append(" OriginMessage -> ").append(((null == orgMsg) || (0 == orgMsg.trim().length())) ? "EMPTY" : orgMsg);
            }
            sb.append(" OriginStacks ->").append(makeStackBuffer(origin.getStackTrace()));
        }
        return sb;
    }

    private StringBuilder makeStackBuffer(StackTraceElement[] stacks) {
        StringBuilder sb = new StringBuilder();
        sb.append(" {");
        for (int i = 0; i < stacks.length; i++) {
            StackTraceElement ste = stacks[i];
            if (0 != i) {
                sb.append("->");
            }
            sb.append(" [")
                    .append(ste.getFileName())
                    .append("@").append(ste.getLineNumber())
                    .append("@").append(ste.getClassName())
                    .append("@").append(ste.getMethodName())
                    .append("] ").append(File.pathSeparator);
        }
        sb.append("} ");
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
           String msg =  t.getMessage();
           String type = t.getClass().getSimpleName();
           sb.append(type).append(":").append(msg).append(" >> ");
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
     * @param clzz
     * @return
     */
    public StringBuilder makeStackChainBuffer(Throwable e,Class<?> clzz){
        StackTraceElement[] stes = e.getStackTrace();
        String clzzName = null;
        if(null != clzz) {
            clzz.getName();
        }
        StringBuilder sb = new StringBuilder();
        for(StackTraceElement ste : stes){
            if ((null != clzz) && !ste.getClassName().equals(clzzName)) {
                continue;
            }
            sb.append(ste.getClassName()).append(".").append(ste.getMethodName())
                    .append("(")
                    .append(ste.getFileName()).append(":").append(ste.getLineNumber())
                    .append(") >> ");
        }
        int len = sb.length();
        if(0 != len){
            sb.delete(len -4,len);
            return sb;
        }
        return null;
    }

    public void throwDisplayException(int code, Class<?> local, Throwable cause, String brief, String displayFmt, Object...obj) {
        String msg = AlbianStringServant.Instance.format(displayFmt,obj);
        if(null == cause){
            throw new AlbianDisplayException(code,local,brief,msg);
        }
        throw new AlbianDisplayException(code,local,cause,brief,msg);
    }

    public void throwHiddenException(int code, Class<?> local, Throwable cause, String brief, String hideMsg, String displayFmt, Object... obj) {
        String msg = AlbianStringServant.Instance.format(displayFmt,obj);
        if(null == cause){
            throw new AlbianHiddenException(code,local,brief,hideMsg,msg);
        }
        throw new AlbianHiddenException(code,local,cause,brief,hideMsg,msg);
    }

}
