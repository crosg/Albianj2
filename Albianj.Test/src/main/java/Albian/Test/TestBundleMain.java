package Albian.Test;

import org.albianj.framework.boot.ApplicationContext;
import org.albianj.framework.boot.BundleContext;
import org.albianj.loader.AlbianBootService;

public class TestBundleMain {
    public void startup(){
        BundleContext bctx =  ApplicationContext.Instance.findCurrentBundleContext(this.getClass(),true);
        String[] args = bctx.getArgs();
        AlbianBootService.start(args[0]);
    }
}
