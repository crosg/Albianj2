package org.albianj.qidian.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.albianj.loader.AlbianBootService;
import org.albianj.qidian.test.common.IdGenerator;
import org.albianj.qidian.test.object.IUser;
import org.albianj.qidian.test.object.impl.User;
import org.albianj.qidian.test.service.IUserService;
import org.albianj.service.AlbianServiceRouter;

public class qidian {

	public static void main(String[] argv){
		try {
		AlbianBootService.start(argv[0],argv[0]);
		
		IUserService us = AlbianServiceRouter.getService(IUserService.class, IUserService.Name, true);
		BigInteger id = IdGenerator.make();
		IUser u11 = us.load("session", id);
		
		IUser u1 = new User();
		u1.setId(id);
		u1.setName("name");
		
		IUser u2 = new User();
		u2.setId(IdGenerator.make(20150301));
		u2.setName("name");
		List<IUser> users = new ArrayList<>();
		users.add(u1);
		users.add(u2);
		us.create("sessionId", u1);
		
		us.modifyName("sessionid", id, "namename");
		
		//IUser u11 = us.load("session", id);
		System.out.println(u11);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
