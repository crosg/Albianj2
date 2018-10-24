package org.albianj.except;


public class AlbianRuntimeException extends RuntimeException {

    private Throwable innerThrows = null;
    private String msg = null;

    public AlbianRuntimeException(String msg){
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        String fmt = "%s:%d in %s throws -> %s.";
        this.msg = String.format(fmt,stes[1].getClassName(),stes[1].getMethodName(),stes[1].getLineNumber(),msg);
    }
    public AlbianRuntimeException(String className,String methodName,int line, String msg){
        String fmt = "%s:%d in %s throws -> %s.";
        this.msg = String.format(fmt,className,line,methodName,msg);
    }

    public AlbianRuntimeException(Throwable throwable){
        innerThrows = throwable;
        String fmt = "Trace:%s throws -> %s,Detail -> %s";
        StackTraceElement[] stacks = throwable.getStackTrace();
        int count = stacks.length >=5 ? 5 : stacks.length;
        StringBuilder sb = new StringBuilder("[");
        for(int i = 0;i<count;i++){
            StackTraceElement stack = stacks[i];
            sb.append(stack.getClassName()).append(":").append(stack.getLineNumber()).append("~").append(stack.getMethodName())
                    .append(">>");
        }
        if(0!= sb.length()) {
            sb.deleteCharAt(sb.length()-2);
        }
        this.msg = String.format(fmt,sb,throwable.getClass().getName(),throwable.getMessage());
    }


    @Override
    public String toString() {
        return this.msg;
    }

    public String getMessage(){
        return this.msg;
    }

    public String getLocalizedMessage(){
        return this.msg;
    }

}
