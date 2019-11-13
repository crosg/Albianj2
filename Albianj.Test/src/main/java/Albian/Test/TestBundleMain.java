package Albian.Test;

import Albian.Test.Services.IOrgUserService;
import Albian.Test.Services.IUserService;
import org.albianj.argument.RefArg;
import org.albianj.bundle.bridge.BundleBridge;
import org.albianj.framework.boot.ApplicationContext;
import org.albianj.framework.boot.BundleContext;
import org.albianj.framework.boot.except.ThrowableServant;
import org.albianj.framework.boot.logging.LogServant;
import org.albianj.framework.boot.logging.LoggerLevel;
import org.albianj.loader.AlbianBootService;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.service.AlbianServiceRouter;

public class TestBundleMain {
    private static void test1() {
        IUserService us = AlbianServiceRouter.getSingletonService(IUserService.class, IUserService.Name);
        us.addUser("uname", "pwd");
        if (us.login("uname", "pwd")) {
            System.out.println("login success.");
        } else {
            System.out.println("login fail.");
        }
        if (us.modifyPwd("uname", "pwd", "newpwd")) {
            System.out.println("modify password success.");
        } else {
            System.out.println("modify password fail.");
        }

        RefArg<String> idl = new RefArg<>();
        RefArg<String> idr = new RefArg<>();
        if (us.batchAddUser(idl,idr)) {
            System.out.println("batch add use success");
        } else {
            System.out.println("batch add user fail.");
        }
        us.queryMulitUserById(idl.getValue(),idr.getValue());
    }

    private static void test2() {
        IOrgUserService us = AlbianServiceRouter.getSingletonService(IOrgUserService.class, IOrgUserService.Name);
        if (us.addUser("uname-org", "pwd-org")) {
            System.out.println("add org user success.");
        } else {
            System.out.println("add org user fail.");

        }
        if (us.login("uname-org", "pwd-org")) {
            System.out.println("login success.");
        } else {
            System.out.println("login with org user is fail..");

        }
        if (us.modifyPwd("uname-org", "pwd-org", "newpwd-org")) {
            System.out.println("modify password success.");
        } else {
            System.out.println("modify password fail.");
        }

        if (us.batchAddUser()) {
            System.out.println("batch add use success");
        } else {
            System.out.println("batch add user fail.");
        }
        us.queryMulitUserById();
    }

    public void startup() {
        try {
            BundleContext bctx = ApplicationContext.Instance.findCurrentBundleContext(this.getClass(), true);
            String[] args = bctx.getArgs();
            String bundleName =  bctx.getBundleName();
            boolean rc = false;

            try {
                if(null == args ||0 == args.length) {
                    rc = AlbianBootService.start(bctx.getConfFolder());
                } else {
                    rc = AlbianBootService.start(args[0]);
                }
                if (!rc) {
                    LogServant.Instance.newLogPacketBuilder()
                            .forSessionId("BundleMain")
                            .atLevel(LoggerLevel.Error)
                            .byCalled(this.getClass())
                            .aroundBundle(bundleName)
                            .takeBrief("Bundle launcher")
                            .addMessage("Bundle -> {0} startup... startup albianj fail",
                                    bundleName)
                            .build().toLogger();
                }
            } catch (Exception e) {
                LogServant.Instance.newLogPacketBuilder()
                        .forSessionId("BundleMain")
                        .atLevel(LoggerLevel.Error)
                        .byCalled(this.getClass())
                        .withCause(e)
                        .alwaysThrow(true)
                        .aroundBundle(bundleName)
                        .takeBrief("Bundle launcher")
                        .addMessage("Bundle -> {0} startup... startup albianj fail",
                                bundleName)
                        .build().toLogger();
            }
            System.out.println("Startup albianj is success then exec......");
            BundleBridge.newBundle(args);
            IAlbianLoggerService2 logServ = AlbianServiceRouter.getLogger2();
            logServ.log(AlbianServiceRouter.LoggerRunning, "Sessionid", AlbianLoggerLevel.Mark,
                    "Startup albianj...");
//            ApplicationContext.Instance.exit("myself",bctx,this.getClass(),0);

//            BundleClassLoader cl = (BundleClassLoader) bctx.getClassLoader();
//            cl.findChildFileEntries("org.albianj.comment");

            StringBuilder sb = new StringBuilder();
            sb.append("作者有话说");
            sb.append('\'');
            sb.append('\u001a');
            sb.append('\'');

            sb.append(',');
            sb.append('\'');
            sb.append('\u001a');
            sb.append('\'');
            sb.append('\'');
            sb.append('\u001a');
            sb.append('\'');

            IUserService us = AlbianServiceRouter.getSingletonService(IUserService.class, IUserService.Name);
            us.addUsers("BatchSubmit",sb.toString());
            us.addUsersV2("NotBatchSubmit",sb.toString());

            logServ.log(AlbianServiceRouter.LoggerRunning, "Sessionid", AlbianLoggerLevel.Mark,
                    "UserService is %s.",null == us ? "NULL" : "NOTNULL");
            us.addUser("uname-SpxDBCP", "pwd");
            if (us.login("uname-SpxDBCP", "pwd")) {
                System.out.println("login success.");
            } else {
                System.out.println("login fail.");
            }
            if (us.modifyPwd("uname-SpxDBCP", "pwd", "newpwd-SpxDBCP")) {
                System.out.println("modify password success.");
            } else {
                System.out.println("modify password fail.");
            }
            RefArg<String> idl = new RefArg<>();
            RefArg<String> idr = new RefArg<>();
            if (us.batchAddUser(idl,idr)) {
                System.out.println("batch add use success");
            } else {
                System.out.println("batch add user fail.");
            }
            us.queryMulitUserById(idl.getValue(),idr.getValue());
//            test2();

            return;
        } catch (Exception e) {
            System.out.println("Exception Stacks -> " + ThrowableServant.Instance.printThrowStackTrace(e));
        }
    }
}
