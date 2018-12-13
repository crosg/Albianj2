package org.albianj.mvc;

/**
 * Created by xuhaifeng on 16/11/30.
 */
public class AlbianMVCException extends RuntimeException {

    public AlbianMVCException() {
        super();
    }

    /**
     * @param msg
     */
    public AlbianMVCException(String msg) {
        super(msg);
    }

    /**
     * @param msg
     * @param cause
     */
    public AlbianMVCException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * @param cause
     */
    public AlbianMVCException(Throwable cause) {
        super(cause);
    }

}
