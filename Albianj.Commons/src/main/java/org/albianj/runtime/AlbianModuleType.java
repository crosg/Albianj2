package org.albianj.runtime;

/**
 * Created by xuhaifeng on 17/2/14.
 */
public enum AlbianModuleType {
    AlbianKernel(1,"Kernel","KernelService is error."),
    AlbianPersistence(2,"Persistence","PersistenceService is error."),
    AlbianUniqueId(3,"UniqueId","RemoteUniqueIdService is error."),
    AlbianSecurity(4,"Security","SecurityService is error."),
    AlbianRestful(5,"Restful","RestfulService is error."),
    AlbianMvf(6,"Mvf","MvfService is error."),
    AlbianCaced(7,"Cached","CachedService is error."),
    AlbianConfigurtion(8,"Configurtion","ConfigurtionService is error."),
    AlbianDFSClient(9,"DFSClient","DFSClientService is error."),
    RestfulClient(10,"RestfulClient","RestfulClientService is error."),


    BusinessService(100,"BusinessService","BusinessService is error."),
    BusinessRestful(101,"BusinessRestful","BusinessRestfulService is error."),
    BusinessWeb(102,"BusinessWeb","BusinessWebService is error.");

    private int model;
    private String name;
    private String throwInfo;

    AlbianModuleType(int model,String name,String throwInfo){
        this.model = model;
        this.name = name;
        this.throwInfo = throwInfo;
    }

    public String getName(){
        return this.name;
    }

    public int getModel(){
        return  this.model;
    }

    public String getThrowInfo(){return this.throwInfo;}

    @Override
    public String toString() {
        return new StringBuilder("ModelId:").append(this.model).append(",ModelName:").append(this.getName()).toString();
    }
}
