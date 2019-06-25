package org.albianj.loader.logging.impl;

import org.albianj.loader.logging.IAlbianLoggerAppender;

import java.io.PrintStream;

public class AlbianConsoleAppender implements IAlbianLoggerAppender {
    private PrintStream outStream;
    private  boolean isClosed = false;

    public AlbianConsoleAppender() {
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
