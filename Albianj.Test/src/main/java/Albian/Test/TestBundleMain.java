package Albian.Test;

import org.albianj.framework.boot.ApplicationContext;
import org.albianj.framework.boot.BundleContext;
import org.albianj.framework.boot.except.ThrowableServant;
import org.albianj.loader.AlbianBootService;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.service.AlbianServiceRouter;

public class TestBundleMain {
    public void startup(){
        try {
            BundleContext bctx =  ApplicationContext.Instance.findCurrentBundleContext(this.getClass(),true);
            String[] args = bctx.getArgs();
            try {
                boolean rc = AlbianBootService.start(args[0]);
                if(!rc){
                    System.out.println("Startup albianj is fail.");
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            IAlbianLoggerService2 logServ = AlbianServiceRouter.getLogger2();
            logServ.log(AlbianServiceRouter.LoggerRunning,"Sessionid", AlbianLoggerLevel.Mark,
            "Startup albianj...");
//            logServ.log("AlbianMonitorLogger","session", AlbianLoggerLevel.Debug,
//                    "wolaile");
//            Thread.sleep(65* 1000);
//            logServ.log("AlbianMonitorLogger","session", AlbianLoggerLevel.Debug,
//                    "wolaile2");

//            IAlbianMonitorLoggerService mlogServ = AlbianServiceRouter.getSingletonService(IAlbianMonitorLoggerService.class, IAlbianMonitorLoggerService.Name);
//            mlogServ.addMonitorLog("session",
//                    AlbianMonitorData.build()
//                            .setAppName("appname").setBizExtend("bizExtend")
//                            .setBizId("bizid").setBizName("bizName").setDesIp("desip")
//                            .setDesPort(8080).setDetail("detail")
//                            .setLevel("debug").setSessionId("session")
//                            .setStatus(200).setTasktime(60)
//            );
            return;
        }catch (Exception e){
            System.out.println("Exception Stacks -> " + ThrowableServant.Instance.printThrowStackTrace(e));
        }
    }
}
