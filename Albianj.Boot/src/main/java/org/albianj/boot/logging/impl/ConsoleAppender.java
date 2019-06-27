package org.albianj.boot.logging.impl;

import org.albianj.boot.logging.ILoggerAppender;
import org.albianj.boot.tags.BundleSharingTag;

import java.io.PrintStream;

@BundleSharingTag
public class ConsoleAppender implements ILoggerAppender {
    private PrintStream outStream;
    private  boolean isClosed = false;

    public ConsoleAppender() {
        outStream = System.out;
    }

    @Override
    public void write(String src) {
        if(!isClosed) {
            outStream.print(src);
        }
    }

    @Override
    public void flush() {
        outStream.flush();
    }

    @Override
    public void close() {
        this.isClosed = true;
    }


    public String getMaxFilesize() {
        return null;
    }

    public void setMaxFilesize(String sMaxFilesize) {

    }
}
