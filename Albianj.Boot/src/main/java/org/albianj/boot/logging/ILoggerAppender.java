package org.albianj.boot.logging;

import org.albianj.boot.tags.BundleSharingTag;

@BundleSharingTag
public interface ILoggerAppender {
//    void open();

    void write(String src);

    void flush();

    void close();

    public String getMaxFilesize();

    public void setMaxFilesize(String sMaxFilesize) ;
}
