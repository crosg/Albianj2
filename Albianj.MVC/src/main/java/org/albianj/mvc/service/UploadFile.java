package org.albianj.mvc.service;

/**
 * Created by xuhaifeng on 16/12/30.
 */
public class UploadFile {
    private String fieldName;
    private String clientFileName;
    private byte[] data;

    public String getFieldName(){
        return this.fieldName;
    }

    public void setFieldName(String fieldName){
        this.fieldName = fieldName;
    }

    public String getClientFileName(){
        return this.clientFileName;
    }

    public void setClientFileName(String clientFileName){
        this.clientFileName = clientFileName;
    }

    public byte[] getData(){
        return this.data;
    }

    public void setData(byte[] data){
        this.data = data;
    }

}
