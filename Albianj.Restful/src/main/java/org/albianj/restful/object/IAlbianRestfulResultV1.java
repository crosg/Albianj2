package org.albianj.restful.object;

/**
 * Created by xuhaifeng on 16/12/7.
 */
public interface IAlbianRestfulResultV1 {
    int getError();

    void setError(int no);

    String getErrorMessage();

    void setErrorMessage(String msg);

    Object getResult();

    void setResult(Object rc);
}
