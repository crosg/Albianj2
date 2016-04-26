package org.albianj.qidian.test.service;

import java.math.BigInteger;
import java.util.List;

import org.albianj.persistence.object.IAlbianObject;
import org.albianj.qidian.test.object.IUser;
import org.albianj.service.IAlbianService;

public interface IUserService extends IAlbianService {
	
	final static String Name="UserService";
	
	public boolean create(String sessionId,IUser user);
	public boolean create(String sessionId,List<IAlbianObject> users);
	public boolean modifyName(String sessionId,BigInteger id,String name);
	public IUser load(String sessionId,BigInteger id);
	public boolean remove(String sessionId,BigInteger id);
	
}
