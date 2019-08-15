package Albian.Test;

import org.albianj.framework.boot.ApplicationContext;
import org.albianj.framework.boot.BundleContext;
import org.albianj.framework.boot.IBundleListener;
import org.albianj.framework.boot.except.ThrowableServant;

public class FrameworkMain {
    public static void main(String[] args) {
        try {

            ApplicationContext.Instance.setAppStartupType(FrameworkMain.class)
                    .setWorkFolder("D:\\work\\test\\main_1")
                    .setLoggerAttr("D:\\work\\test\\main_1\\logs", true)
                    .addBundle(FrameworkMain.class, BundleContext.newInstance().setArgs(args)
                            .setStartupClassName(TestBundleMain.class.getName())
                            .setInstallSpxFile(false)
                            .setWorkFolder("D:\\work\\test\\main_1")
//                            .setConfFolder("D:\\work\\github\\albianj2\\Albianj.Test\\src\\main\\resources\\config")
//                            .setLibFolder("D:\\work\\github\\albianj2\\Albianj.Test\\target\\lib")
//                            .setWorkFolder("D:\\work\\github\\albianj2\\Albianj.Test")
                            .setBundleName("FrameworkMain")
                            .setPrintScanClasses(true)

                            .setBeginStartupEvent(new IBundleListener() {
                                @Override
                                public void onActionExecute(BundleContext bctx) {
                                    System.out.println("Start bundle -> " + bctx.getBundleName());
                                }
                            })
                            .build());
            ApplicationContext.Instance.run(args);
        } catch (Exception e) {
            System.out.println("Exception Stacks -> " + ThrowableServant.Instance.printThrowStackTrace(e));
        }
    }
}
