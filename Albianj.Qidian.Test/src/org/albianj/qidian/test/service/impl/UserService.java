package org.albianj.qidian.test.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.object.FilterCondition;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IFilterCondition;
import org.albianj.persistence.object.LogicalOperation;
import org.albianj.persistence.object.filter.FilterExpression;
import org.albianj.persistence.object.filter.FilterGroupExpression;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.persistence.object.filter.IFilterExpression;
import org.albianj.persistence.object.filter.IFilterGroupExpression;
import org.albianj.persistence.service.IAlbianPersistenceService;
import org.albianj.persistence.service.LoadType;
import org.albianj.qidian.test.common.IdGenerator;
import org.albianj.qidian.test.object.IUser;
import org.albianj.qidian.test.object.impl.User;
import org.albianj.qidian.test.service.IUserService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.FreeAlbianService;

public class UserService extends FreeAlbianService implements IUserService {

	private static IAlbianPersistenceService getPersistenceService() {
		IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,
				IAlbianPersistenceService.Name, true);
		return aps;
	}

	@Override
	public boolean create(String sessionId, IUser user) {
		// TODO Auto-generated method stub
		try {
			getPersistenceService().create(sessionId, user);
		} catch (AlbianDataServiceException e) {
			return false;
		}
		return true;
	}

	public boolean create(String sessionId, List<IAlbianObject> users) {
		try {
			getPersistenceService().create(sessionId, users);
		} catch (AlbianDataServiceException e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean modifyName(String sessionId, BigInteger id, String name) {
		// TODO Auto-generated method stub
		try {

			LinkedList<IFilterCondition> wheres = new LinkedList<>();
			wheres.add(new FilterCondition("Id", id));
			IUser u = getPersistenceService().loadObject(sessionId, IUser.class, LoadType.exact, wheres);
			if (null != u) {
				u.setName(name);
			} else {
				u = new User();
				u.setId(id);
				u.setName(name);
			}
			getPersistenceService().save(sessionId, u);
		} catch (AlbianDataServiceException e) {
			return false;
		}
		return true;
	}

	@Override
	public IUser load(String sessionId, BigInteger id) {
		// TODO Auto-generated method stub
		try {
			IChainExpression fe = new FilterExpression();
			fe.add("id", LogicalOperation.Equal,id);
			fe.and("name", LogicalOperation.Equal, "name");
			IFilterGroupExpression fge = new FilterGroupExpression();
			IFilterGroupExpression fge_child = new FilterGroupExpression();
			
			fge_child.add("age", LogicalOperation.Greater, 10);
			fge_child.and("age", "age1", LogicalOperation.Less, 80);
			IFilterGroupExpression fge_child2 = new FilterGroupExpression();
			fge_child2.add("sex","sex1", LogicalOperation.Greater, 10);
			fge_child2.or("sex", LogicalOperation.Equal, "sex");
			
			fge.addFilterGroup(fge_child);
			fge.and(fge_child2);
			fe.or(fge);
			fe.or("unit", LogicalOperation.Equal, "unit");
			

			getPersistenceService().loadObjects("", IUser.class,LoadType.quickly, fe);

			
		} catch (AlbianDataServiceException e) {
			return null;
		}
		return null;
	}

	@Override
	public boolean remove(String sessionId, BigInteger id) {
		// TODO Auto-generated method stub
		try {
			LinkedList<IFilterCondition> wheres = new LinkedList<>();
			wheres.add(new FilterCondition("Id", id));
			IUser u = getPersistenceService().loadObject(sessionId, IUser.class, LoadType.exact, wheres);
			if (null != u) {
				u.setIsDelete(true);
				return getPersistenceService().save(sessionId, u);
			} else {
				return true;
			}
		} catch (AlbianDataServiceException e) {
			return false;
		}
	}

}
