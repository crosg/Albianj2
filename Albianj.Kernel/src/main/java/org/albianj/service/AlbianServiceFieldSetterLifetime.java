package org.albianj.service;

public enum AlbianServiceFieldSetterLifetime {
    /**
     * 在对象被创建，但是service还未被初始化之前赋值
     * 会影响service的loading
     */
    AfterNew,
    /**
     * 在马上要loading service的时候赋值
     * 会影响service的loading
     */
    BeforeLoading,
    /**
     * 在loading结束的时候再赋值
     * 会影响service的loading
     */
    AfterLoading,
    /**
     * 在albianj的kernel service全部加载结束的时候赋值
     * 一般使用在业务的service之间相互引用
     * 不会影响service的loading
     * 当赋值错误的时候，也不会停止kernel的加载，只会日志提示
     */
    AfterKernelLoaded,
}
