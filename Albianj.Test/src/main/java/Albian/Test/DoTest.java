package Albian.Test;

import Albian.Test.Services.IUserService;
import org.albianj.loader.AlbianBootService;
import org.albianj.service.AlbianServiceRouter;

public class DoTest {
    public static void main(String[] argv){
        try {
            AlbianBootService.start("D:\\work\\github\\albianj2\\Albianj.Test\\src\\main\\resources\\config");
            IUserService us = AlbianServiceRouter.getSingletonService(IUserService.class,IUserService.Name);
//            us.addUser("uname","pwd");
//            if(us.login("uname","pwd")) {
//                System.out.println("login success.");
//            }
//            System.out.println("login fail.");
//            if(us.modifyPwd("uname","pwd","newpwd")){
//                System.out.println("modify password success.");
//            }
//            System.out.println("modify password fail.");

//            if(us.batchAddUser()){
//                System.out.println("batch add use success");
//            } else {
//                System.out.println("batch add user fail.");
//            }
            us.queryMulitUserById();
            return;

        }catch(Throwable t){
            t.printStackTrace();
        }
    }
}
