package Albian.Test;

import Albian.Test.Services.IOrgUserService;
import Albian.Test.Services.IUserService;
import org.albianj.loader.AlbianBootService;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.object.RunningStorageAttribute;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.service.AlbianServiceRouter;
import org.jaxen.function.StringLengthFunction;

import java.sql.Connection;

public class DoTest {
    public static void main(String[] argv){
        try {
            AlbianBootService.start("D:\\work\\github\\albianj2\\Albianj.Test\\src\\main\\resources\\config");

//            final IAlbianStorageParserService stgService = AlbianServiceRouter.getSingletonService(IAlbianStorageParserService.class,IAlbianStorageParserService.Name);
//            for( int i = 0; i < 1200; i++){
//                new Thread(new Runnable(){
//                    @Override
//                    public void run() {
//                        IStorageAttribute stgAttr = stgService.getStorageAttribute("SpxDBCP");
//                        IRunningStorageAttribute runStgAttr = new RunningStorageAttribute(stgAttr,stgAttr.getDatabase());
//                        Connection conn = stgService.getConnection("sessionId:" + Thread.currentThread().getId(), runStgAttr);
//
//                        int sec = 1 * 1000;
//                        long  ts = System.currentTimeMillis() % sec;
//                        if(0 == ts){
//                            ts = 1 * 1000;
//                        }
//                        try {
//                            Thread.sleep(ts);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        if(null == conn){
//                            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
//                                    "DBPOOLMAIN", AlbianLoggerLevel.Info,
//                                    "get conn is null.");
//                        } else {
//                            stgService.returnConnection("ses", runStgAttr, conn);
//                        }
//
//                    }
//                }).start();
//                if(0 == (i % 300)){
//                    Thread.sleep(3000);
//                }
//            }
//            Thread.sleep(50000000);


            IUserService us = AlbianServiceRouter.getSingletonService(IUserService.class,IUserService.Name);
            us.addUser("uname-SpxDBCP","pwd");
            if(us.login("uname-SpxDBCP","pwd")) {
                System.out.println("login success.");
            }
            System.out.println("login fail.");
            if(us.modifyPwd("uname-SpxDBCP","pwd","newpwd-SpxDBCP")){
                System.out.println("modify password success.");
            }
            System.out.println("modify password fail.");

//            if(us.batchAddUser()){
//                System.out.println("batch add use success");
//            } else {
//                System.out.println("batch add user fail.");
//            }
//            us.queryMulitUserById();
            test2();
            return;

        }catch(Throwable t){
            t.printStackTrace();
        }
    }

    private static  void test1(){
        IUserService us = AlbianServiceRouter.getSingletonService(IUserService.class,IUserService.Name);
        //            us.addUser("uname","pwd");
//            if(us.login("uname","pwd")) {
//                System.out.println("login success.");
//            }
//            System.out.println("login fail.");
//            if(us.modifyPwd("uname","pwd","newpwd")){
//                System.out.println("modify password success.");
//            }
//            System.out.println("modify password fail.");

//            if(us.batchAddUser()){
//                System.out.println("batch add use success");
//            } else {
//                System.out.println("batch add user fail.");
//            }
        us.queryMulitUserById();
    }

    private static  void test2(){
        IOrgUserService us = AlbianServiceRouter.getSingletonService(IOrgUserService.class,IOrgUserService.Name);
//                   if( us.addUser("uname-org","pwd-org")){
//                       System.out.println("add org user success.");
//                   } else {
//                       System.out.println("add org user fail.");
//
//                   }
//        if(us.login("uname-org","pwd-org")) {
//            System.out.println("login success.");
//        } else {
//            System.out.println("login with org user is fail..");
//
//        }
//            if(us.modifyPwd("uname-org","pwd-org","newpwd-org")){
//                System.out.println("modify password success.");
//            } else {
//                System.out.println("modify password fail.");
//            }

            if(us.batchAddUser()){
                System.out.println("batch add use success");
            } else {
                System.out.println("batch add user fail.");
            }
        us.queryMulitUserById();
    }
}
