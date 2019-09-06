package Albian.Test;

import Albian.Test.Services.IOrgUserService;
import Albian.Test.Services.IUserService;
import org.albianj.framework.boot.ApplicationContext;
import org.albianj.framework.boot.BundleContext;
import org.albianj.framework.boot.IBundleListener;
import org.albianj.framework.boot.logging.LoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.service.AlbianServiceRouter;

public class DoTest {
    public static void main(String[] args) {
        try {
            ApplicationContext.Instance.setAppStartupType(DoTest.class)
                    .setWorkFolder("D:\\work\\github\\albianj2\\Albianj.Test\\src\\main\\resources")
                    .setLoggerAttr("D:\\work\\github\\albianj2\\Albianj.Test\\logs", LoggerLevel.Debug, true)
                    .addBundle(DoTest.class,BundleContext.newInstance().setArgs(args)
                                .setStartupClassName(TestBundleMain.class.getName())
                                .setInstallSpxFile(false)
                                .setConfFolder("D:\\work\\github\\albianj2\\Albianj.Test\\src\\main\\resources\\config")
                                .setLibFolder("D:\\work\\github\\albianj2\\Albianj.Test\\target\\lib",true)
                                .setWorkFolder("D:\\work\\github\\albianj2\\Albianj.Test")
                                .setBundleName("DoTest")
                            .setBeginStartupEvent(new IBundleListener() {
                                @Override
                                public void onActionExecute(BundleContext bctx) {
                                    System.out.println("Start bundle -> " + bctx.getBundleName());
                                }})
                            .build());
            ApplicationContext.Instance .run(args);


            IAlbianLoggerService2 logServ = AlbianServiceRouter.getLogger2();
//            logServ.log("AlbianMonitorLogger","session", AlbianLoggerLevel.Debug,
//                    "wolaile");
//            Thread.sleep(65* 1000);
//            logServ.log("AlbianMonitorLogger","session", AlbianLoggerLevel.Debug,
//                    "wolaile2");

//            IAlbianMonitorLoggerService mlogServ = AlbianServiceRouter.getSingletonService(IAlbianMonitorLoggerService.class,IAlbianMonitorLoggerService.Name);
//            mlogServ.addMonitorLog("session",
//                    AlbianMonitorData.build()
//                            .setAppName("appname").setBizExtend("bizExtend")
//                    .setBizId("bizid").setBizName("bizName").setDesIp("desip")
//                    .setDesPort(8080).setDetail("detail")
//                    .setLevel("debug").setSessionId("session")
//                    .setStatus(200).setTasktime(60)
//                    );
            return;


//            String v = "@徐海峰(xuhaifeng)\uD83C\uDF4E\uD83D\uDCB0\uD83D\uDCF1\uD83C\uDF19\uD83C\uDF41\uD83C\uDF42\uD83C\uDF43\uD83C\uDF37\uD83D\uDC8E\uD83D\uDD2A\uD83D\uDD2B\uD83C\uDFC0⚽⚡\uD83D\uDC44\uD83D\uDC4D\uD83D\uDD25tm都是啥玩意jb玩意";
//            IUTF8M64Service utf = AlbianServiceHub.getSingletonService(IUTF8M64Service.class, IUTF8M64Service.ServiceId);
//            int id = 100;
//            utf.saveUtf8M64(id, v);
//            String v1 = utf.getUtf8M64(id);
//            System.out.println(v1);


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


//            IUserService us = AlbianServiceRouter.getSingletonService(IUserService.class, IUserService.Name);
//            us.addUser("uname-SpxDBCP", "pwd");
//            if (us.login("uname-SpxDBCP", "pwd")) {
//                System.out.println("login success.");
//            }
//            System.out.println("login fail.");
//            if (us.modifyPwd("uname-SpxDBCP", "pwd", "newpwd-SpxDBCP")) {
//                System.out.println("modify password success.");
//            }
//            System.out.println("modify password fail.");

//            if(us.batchAddUser()){
//                System.out.println("batch add use success");
//            } else {
//                System.out.println("batch add user fail.");
//            }
//            us.queryMulitUserById();
//            test2();
//            return;

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void test1() {
        IUserService us = AlbianServiceRouter.getSingletonService(IUserService.class, IUserService.Name);
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
//        us.queryMulitUserById();
    }

    private static void test2() {
        IOrgUserService us = AlbianServiceRouter.getSingletonService(IOrgUserService.class, IOrgUserService.Name);
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

        if (us.batchAddUser()) {
            System.out.println("batch add use success");
        } else {
            System.out.println("batch add user fail.");
        }
        us.queryMulitUserById();
    }
}
