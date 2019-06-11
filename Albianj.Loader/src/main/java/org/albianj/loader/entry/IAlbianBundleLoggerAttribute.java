package org.albianj.loader.entry;

public interface IAlbianBundleLoggerAttribute {
    String getLoggerName();

    void setLoggerName(String loggerName);

    String getPath();

    void setPath(String path);

    String getLevel();

    void setLevel(String level);

    boolean isOpenConsole();

    void setOpenConsole(boolean openConsole);

    String getMaxFilesize();

    void setMaxFilesize(String maxFilesize);
}
