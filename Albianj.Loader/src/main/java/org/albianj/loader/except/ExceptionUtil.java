package org.albianj.loader.except;

import java.io.File;


public class ExceptionUtil {
    /**
     * 异常级别,表示正常的异常,可能只是一个过程的需要,或者用来控制一个程序的流程
     */
    public final static int ExceptForNormal = 10;
    /**
     * 警告的异常,通常对程序无实质性影响,一把会使用默认值等处理掉或者容错机制处理掉
     */
    public final static int ExceptForWarn = 20;
    /**
     * 错误的异常,程序无法对该异常做出任何可修正的措施,程序必须中断或者停止
     */
    public final static int ExceptForError = 30;
    /**
     * 无比重要的异常,比刑爷还要重要的异常,必须引起所有人的注意,不管什么程序都需要12w分警惕
     */
    public final static int ExceptForMark = 40;


    public static StringBuilder makeMessage(String  brief,String msg,Exception self,Throwable origin){
        return makeMessage(brief,msg,null,self,origin);
    }

    public static StringBuilder makeMessage(String  brief,String msg,String secret,Exception self,Throwable origin){
        StringBuilder sb = new StringBuilder();
        sb.append("brief -> ").append(((null == brief) || (0 == brief.trim().length()))? "EMPTY" : brief)
                .append(" msg -> ").append(((null == msg) || (0 == msg.trim().length())) ? "EMPTY" : msg);
        if(!((null == secret) || (0 == secret.trim().length()))) {
            sb.append(" Secret -> ").append(secret);
        }
        sb.append(" SelfStacks -> ").append(makeStackBuffer(self.getStackTrace()));
        if(null != origin) {
            if(!origin.getClass().isAssignableFrom(self.getClass())) {
                String orgMsg = origin.getMessage();
                sb.append(" OriginMessage -> ").append(((null == orgMsg) || (0 == orgMsg.trim().length())) ? "EMPTY" : orgMsg);
            }
            sb.append(" OriginStacks ->").append(makeStackBuffer(origin.getStackTrace()));
        }
        return sb;
    }

//    public static  int logLevel2Code(AlbianLoggerLevel level){
//        return level.getLevel()  * 10;
//    }


    private static StringBuilder makeStackBuffer(StackTraceElement[] stacks) {
        StringBuilder sb = new StringBuilder();
        sb.append(" {");
        for(int i = 0;i < stacks.length;i++) {
            StackTraceElement ste = stacks[i];
            if(0 != i) {
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

        static String join(Object... args){
        if(null == args || 0 == args.length) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(Object arg : args){
            sb.append(arg);
        }
        return sb.toString();
    }
}
