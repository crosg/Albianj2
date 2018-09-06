package org.albianj.restful.impl.object;

import org.albianj.restful.object.IAlbianRestfulResultV1;

/**
 * Created by xuhaifeng on 16/12/7.
 */
public class AlbianRestfulResultV1 implements IAlbianRestfulResultV1 {
    private int error = 0;
    private String errorMessage = "Success";
    private Object result = null;

    public AlbianRestfulResultV1() {

    }


    public AlbianRestfulResultV1(Object rc) {
        this.error = 0;
        this.result = rc;
    }

    public AlbianRestfulResultV1(int no, String msg) {
        this.error = no;
        this.errorMessage = msg;
    }


    @Override
    public int getError() {
        return 0;
    }

    @Override
    public void setError(int no) {
        this.error = no;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public void setErrorMessage(String msg) {
        this.errorMessage = msg;
    }

    @Override
    public Object getResult() {
        return this.result;
    }

    @Override
    public void setResult(Object rc) {
        this.result = rc;
    }
}
