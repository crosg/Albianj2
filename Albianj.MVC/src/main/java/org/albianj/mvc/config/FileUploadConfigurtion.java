package org.albianj.mvc.config;


public class FileUploadConfigurtion {

    private long maxFileSize = 4 * 1024 * 1024;
    private long maxRequestSize = 10 * 1024 * 1024;
    private String tempFinder = "/fileupload";
    private Class<?> fileUploadServiceClass;

    /**
     * @return the maxFileSize
     */
    public long getMaxFileSize() {
        return maxFileSize;
    }

    /**
     * @param maxFileSize the maxFileSize to set
     */
    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    /**
     * @return the maxRequestSize
     */
    public long getMaxRequestSize() {
        return maxRequestSize;
    }

    /**
     * @param maxRequestSize the maxRequestSize to set
     */
    public void setMaxRequestSize(long maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    /**
     * @return the tempFinder
     */
    public String getFolder() {
        return tempFinder;
    }

    /**
     * @param tempFinder the tempFinder to set
     */
    public void setFolder(String tempFinder) {
        this.tempFinder = tempFinder;
    }

    public Class<?> getFileUploadServiceClass() {
        return this.fileUploadServiceClass;
    }

    public void setFileUploadServiceClass(Class<?> fileUploadServiceClass) {
        this.fileUploadServiceClass = fileUploadServiceClass;
    }
}
