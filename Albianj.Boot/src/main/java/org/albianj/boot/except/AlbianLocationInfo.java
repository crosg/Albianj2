package org.albianj.boot.except;

import java.util.ArrayList;
import java.util.List;

public class AlbianLocationInfo {
    private Throwable e;
    private Class<?> local;

    public AlbianLocationInfo(Throwable e, Class<?> local) {
        this.e = e;
        local = local;
    }

//    public List<StackTraceElement> getCallTraceElements() {
//        String clzzName = clzzOfCalled.getName();
//        ArrayList<StackTraceElement> stes = new ArrayList<>();
//        for (StackTraceElement ste : local.getStackTrace()) {
//            if (!ste.getClassName().equals(clzzName)) {
//                continue;
//            }
//            stes.add(ste);
//        }
//        return stes;
//    }
//
//    public String makeCallTraceBuffer() {
//        String clzzName = clzzOfCalled.getName();
//        StringBuilder sb = new StringBuilder();
//        for (StackTraceElement t : local.getStackTrace()) {
//            if (!t.getClassName().equals(clzzName)) {
//                continue;
//            }
//            sb.append("@ ").append(t.getClassName()).append(".").append(t.getMethodName())
//                    .append("(")
//                    .append(t.getFileName()).append(":").append(t.getLineNumber())
//                    .append(")")
//                    .append(" -> ");
////            .append(System.lineSeparator());
//        }
//        return sb.toString();
//    }

    public Throwable getException(){
        return this.e;
    }
    public Class<?> getLocal(){return this.local;}

    //log(bundleName,logName,level,biref,t,callClass,showMsg);

}
