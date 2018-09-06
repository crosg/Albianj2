package org.albianj.restful.client;


/**
 * Created by xuhaifeng on 17/2/13.
 */
public class MultiParams {
    private String name;
    private byte[] body;
    private String mimeType;
    private String filename;

    /**
     * Getter for property 'filename'.
     *
     * @return Value for property 'filename'.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Setter for property 'filename'.
     *
     * @param filename Value to set for property 'filename'.
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Getter for property 'name'.
     *
     * @return Value for property 'name'.
     */

    public String getName() {
        return name;
    }

    /**
     * Setter for property 'name'.
     *
     * @param name Value to set for property 'name'.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for property 'body'.
     *
     * @return Value for property 'body'.
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * Setter for property 'body'.
     *
     * @param body Value to set for property 'body'.
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * Getter for property 'mimeType'.
     *
     * @return Value for property 'mimeType'.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Setter for property 'mimeType'.
     *
     * @param mimeType Value to set for property 'mimeType'.
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
