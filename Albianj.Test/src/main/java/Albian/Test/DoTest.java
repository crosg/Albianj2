package Albian.Test;

import Albian.Test.Services.IOrgUserService;
import Albian.Test.Services.IUserService;
import org.albianj.loader.AlbianBootService;
import org.albianj.service.AlbianServiceRouter;

public class DoTest {
    public static void main(String[] argv){
        try {
            AlbianBootService.start("D:\\work\\github\\albianj2\\Albianj.Test\\src\\main\\resources\\config");
//            IUserService us = AlbianServiceRouter.getSingletonService(IUserService.class,IUserService.Name);
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
//            us.queryMulitUserById();
            test2();
            return;

        }catch(Throwable t){
            t.printStackTrace();
        }
    }

    private static  void test1(){
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
    }

    private static  void test2(){
        IOrgUserService us = AlbianServiceRouter.getSingletonService(IOrgUserService.class,IOrgUserService.Name);
//                   if( us.addUser("uname-org","pwd-org")){
//                       System.out.println("add org user success.");
//                   } else {
//                       System.out.println("add org user fail.");
//
//                   }
//        if(us.login("uname-org","pwd-org")) {
//            System.out.println("login success.");
//        } else {
//            System.out.println("login with org user is fail..");
//
//        }
//            if(us.modifyPwd("uname-org","pwd-org","newpwd-org")){
//                System.out.println("modify password success.");
//            } else {
//                System.out.println("modify password fail.");
//            }

            if(us.batchAddUser()){
                System.out.println("batch add use success");
            } else {
                System.out.println("batch add user fail.");
            }
        us.queryMulitUserById();
    }
}
