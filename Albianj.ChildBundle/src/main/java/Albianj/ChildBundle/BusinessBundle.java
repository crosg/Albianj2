package Albianj.ChildBundle;

import Albianj.ChildBundle.Services.IUserService;
import org.albianj.argument.RefArg;
import org.albianj.framework.boot.ApplicationContext;
import org.albianj.framework.boot.BundleContext;
import org.albianj.framework.boot.except.ThrowableServant;
import org.albianj.framework.boot.logging.LogServant;
import org.albianj.framework.boot.logging.LoggerLevel;
import org.albianj.loader.AlbianBootService;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.service.AlbianServiceRouter;

public class BusinessBundle {
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
            IAlbianLoggerService2 logServ = AlbianServiceRouter.getLogger2();
            logServ.log(AlbianServiceRouter.LoggerRunning, "Sessionid", AlbianLoggerLevel.Mark,
                    "Startup albianj...");

            IUserService us = AlbianServiceRouter.getSingletonService(IUserService.class, IUserService.Name);
            logServ.log(AlbianServiceRouter.LoggerRunning, "Sessionid", AlbianLoggerLevel.Mark,
                    "UserService is %s.",null == us ? "NULL" : "NOTNULL");
            us.addUser("uname-SpxDBCP-bundle", "uname-SpxDBCP-bundle");
            if (us.login("uname-SpxDBCP-bundle", "uname-SpxDBCP-bundle")) {
                System.out.println("login success.");
            } else {
                System.out.println("login fail.");
            }
            if (us.modifyPwd("uname-SpxDBCP-bundle", "uname-SpxDBCP-bundle", "newpwd-SpxDBCP")) {
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
            return;
        } catch (Exception e) {
            System.out.println("Exception Stacks -> " + ThrowableServant.Instance.printThrowStackTrace(e));
        }
    }
}
