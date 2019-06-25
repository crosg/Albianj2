package org.albianj.boot;

/**
 * albianj bundle的启动器接口
 * 所有的bundle必须实现该接口
 * 该接口会在bundle加载完毕所有的信息后执行
 */
public interface IAlbianBundleLauncher {

    public void startup(String[] args);
}
