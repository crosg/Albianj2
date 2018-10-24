package Albian.Core.Service;

import org.albianj.except.AlbianRuntimeException;
import org.albianj.logger.RuntimeLogType;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.service.AlbianObjectCreator;
import org.albianj.service.AlbianServiceRouter;

public final class AlbianServiceHub extends AlbianServiceRouter {

    public static IAlbianObject newInstance(String sessionId, String itf){
        return AlbianObjectCreator.newInstance(sessionId,itf);
    }

    public static <T extends  IAlbianObject> T newInstance(String sessionId, Class<T> clazz){
        return (T) newInstance(sessionId, clazz.getName());
    }

    public static void throwException(String sessionId,Throwable throwable){
        throwException(sessionId,throwable,true);
    }

    public static void throwException(String sessionId,Throwable throwable,boolean throwsOut){
        if(AlbianRuntimeException.class.isAssignableFrom(throwable.getClass())){
            //warp once over,and not again
            StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
            addLog(sessionId,RuntimeLogType.Warn,stacks[1].getClassName() + ":" + stacks[1].getLineNumber(),throwable.getMessage());
            if(throwsOut){
                throw  ((AlbianRuntimeException) throwable);
            }
            return;
        }
        AlbianRuntimeException thw = new AlbianRuntimeException(throwable);
        addLog(sessionId,RuntimeLogType.Warn,null,throwable.getMessage());
        if(throwsOut){
            throw  thw;
        }
    }

    public static void throwException(String sessionId, String msg){
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        AlbianRuntimeException thw = new AlbianRuntimeException(stacks[1].getClassName(),stacks[1].getMethodName(),stacks[1].getLineNumber(),msg);
        addLog(sessionId,RuntimeLogType.Warn,stacks[1].getClassName() + ":" + stacks[1].getLineNumber(),thw.getMessage());
        throw thw;
    }

    public static void addLog(String sessionId, RuntimeLogType logType, String typeName, String fmt, Object[]... args){

    }

    public static void addLog(String sessionId, RuntimeLogType logType, String typeName, Throwable t, String fmt, Object[]... args){

    }




}
