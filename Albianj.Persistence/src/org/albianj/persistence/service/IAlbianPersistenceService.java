package org.albianj.persistence.service;

import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.albianj.persistence.context.IPersistenceCompensateNotify;
import org.albianj.persistence.context.IPersistenceNotify;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.PersistenceCommandType;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IFilterCondition;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.IAlbianService;

/**
 * 	使用此service时候，请开发者忘了sql的存在和各种以前因为自己写sql带来的便利性。
 * <br />
 * albianj的数据层操作类，使用albianj的数据层，必须使用此service来处理所有的数据层操作。
 * 此service直接面对存储层操作，所以操作的时候请慎重。
 *  <br />
 * albianj的数据层操作集合了数据路由，分布式事务，mapping等主要功能。也集成了简单的缓存使用。
 * 这些功能的使用主要是几个配置文件和一些service之间的配合。有兴趣的同学请查看《albianj使用文档》.
 *  <br />
 * 此类会在albianj启动的时候被加载（前提是在service.xml中配置了此服务）。
 * 配置方法：
 * <br/>
 * {@code
 * <xml>
 *	<Service Id="AlbianPersistenceService"
		Type="org.albianj.persistence.impl.service.AlbianPersistenceService" />
 * <xml>
 * }
 *  <br />
 *  调用此service的方法：
 *   <br />
 *   <pre>
 *   <code>
	  private static IAlbianPersistenceService getPersistenceService() {
			IAlbianPersistenceService aps = AlbianServiceRouter.getSingletonService(IAlbianPersistenceService.class,
					IAlbianPersistenceService.Name, true);
			return aps;
		}
 *   </code>
 *   </pre>
 *   
 *   
 * 注意：	此类为albianj数据层的必须service，使用albianj的数据层必须启动此serivce。
 * 
 * @author seapeak
 * @since v2.0
 *
 */
public interface IAlbianPersistenceService extends IAlbianService {
		/**
		 * 此service在service.xml中的id
		 */
		final static String Name = "AlbianPersistenceService";

		/**
		 * 从存储中删除指定的对象
		 * <br />
		 * 注意：使用albianj尽量不要使用“硬删除”，应尽量使用“软删除”。
		 * 原因是albianj会自动的对于分布式事务的“二次提交”做完整性事务和回滚，硬删除可能会出现回滚不完全。
		 * 如果非要使用“硬删除”，最好请在删除之前先根据id加载一下数据，然后在和使用load到的数据进行删除。
		 * 
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param object 需要删除的对象
		 * @return 是否完成删除
		 * @throws AlbianDataServiceException
		 */
		boolean remove(String sessionId, IAlbianObject object) throws AlbianDataServiceException;
		
		/**
		 *  从存储中删除指定的对象
		 * <br />
		 * 注意：使用albianj尽量不要使用“硬删除”，应尽量使用“软删除”。
		 * 原因是albianj会自动的对于分布式事务的“二次提交”做完整性事务和回滚，硬删除可能会出现回滚不完全。
		 * 如果非要使用“硬删除”，最好请在删除之前先根据id加载一下数据，然后在和使用load到的数据进行删除。
		 * 
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param object 需要删除的对象
		 * @param notifyCallback 事务完成后的通知，该通知不会受事务是否成功完成的影响，肯定会被触发
		 * @param notifyCallbackObject 通知的时候，需要传递的自定义参数
		 * @param compensateCallback 事务发生异常的时候触发的通知
		 * @param compensateCallbackObject 事务发生异常时触发通知的自定义对象
		 * @return 是否完成删除
		 * @throws AlbianDataServiceException
		 */
		boolean remove(String sessionId, IAlbianObject object, IPersistenceNotify notifyCallback,
				Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
				Object compensateCallbackObject) throws AlbianDataServiceException;

		/**
		 * 从存储中删除指定的对象集合
		 * <br />
		 * 注意：使用albianj尽量不要使用“硬删除”，应尽量使用“软删除”。
		 * 原因是albianj会自动的对于分布式事务的“二次提交”做完整性事务和回滚，硬删除可能会出现回滚不完全。
		 * 如果非要使用“硬删除”，最好请在删除之前先根据id加载一下数据，然后在和使用load到的数据进行删除。
		 * 
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param objects 需要删除的对象
		 * @return 是否完成删除
		 * @throws AlbianDataServiceException
		 */
		boolean remove(String sessionId, List<? extends IAlbianObject> objects) throws AlbianDataServiceException;

		/**
		 *  从存储中删除指定的对象集合
		 * <br />
		 * 注意：使用albianj尽量不要使用“硬删除”，应尽量使用“软删除”。
		 * 原因是albianj会自动的对于分布式事务的“二次提交”做完整性事务和回滚，硬删除可能会出现回滚不完全。
		 * 如果非要使用“硬删除”，最好请在删除之前先根据id加载一下数据，然后在和使用load到的数据进行删除。
		 * 
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param objects 需要删除的对象
		 * @param notifyCallback 事务完成后的通知，该通知不会受事务是否成功完成的影响，肯定会被触发
		 * @param notifyCallbackObject 通知的时候，需要传递的自定义参数
		 * @param compensateCallback 事务发生异常的时候触发的通知
		 * @param compensateCallbackObject 事务发生异常时触发通知的自定义对象
		 * @return 是否完成删除
		 * @throws AlbianDataServiceException
		 */
		boolean remove(String sessionId, List<? extends IAlbianObject> objects, IPersistenceNotify notifyCallback,
				Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
				Object compensateCallbackObject) throws AlbianDataServiceException;

		
		/**
		 * 保存对象到存储层
		 * <br />
		 * 注意：
		 * 对于对象的保存操作都可以使用此方法，此方法并不区分你的对象是不是已经在存储层存在。也就是说：
		 * 当对象在存储层中不存在的时候，将会执行insert操作；当对象在存储层中存在的时候，执行update操作。
		 * <br />
		 * 为了保证数据的一致性，请在使用此方法保存数据的时候，先load一下数据，然后在load的数据上更改后再调用此方法保存到存储层。
		 * 
		 * @param sessionId essionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param object 需要保存的对象
		 * @return 是否保存成功
		 * @throws AlbianDataServiceException
		 */
		boolean save(String sessionId, IAlbianObject object) throws AlbianDataServiceException;
		
		/**
		 * 保存对象到存储层
		 * <br />
		 * 注意：
		 * 对于对象的保存操作都可以使用此方法，此方法并不区分你的对象是不是已经在存储层存在。也就是说：
		 * 当对象在存储层中不存在的时候，将会执行insert操作；当对象在存储层中存在的时候，执行update操作。
		 * <br />
		 * 为了保证数据的一致性，请在使用此方法保存数据的时候，先load一下数据，然后在load的数据上更改后再调用此方法保存到存储层。
		 * 
		 * @param sessionId essionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param object 需要保存的对象
		 * @param notifyCallback 事务完成后的通知，该通知不会受事务是否成功完成的影响，肯定会被触发
		 * @param notifyCallbackObject 通知的时候，需要传递的自定义参数
		 * @param compensateCallback 事务发生异常的时候触发的通知
		 * @param compensateCallbackObject 事务发生异常时触发通知的自定义对象
		 * @return 是否完成删除
		 * @throws AlbianDataServiceException
		 */
		boolean save(String sessionId, IAlbianObject object, 
				IPersistenceNotify notifyCallback, Object notifyCallbackObject,
				IPersistenceCompensateNotify compensateCallback, Object compensateCallbackObject)
						throws AlbianDataServiceException;

		/**
		 * 保存对象集合到存储层
		 * <br />
		 * 注意：
		 * 对于对象的保存操作都可以使用此方法，此方法并不区分你的对象是不是已经在存储层存在。也就是说：
		 * 当对象在存储层中不存在的时候，将会执行insert操作；当对象在存储层中存在的时候，执行update操作。
		 * <br />
		 * 为了保证数据的一致性，请在使用此方法保存数据的时候，先load一下数据，然后在load的数据上更改后再调用此方法保存到存储层。
		 * 
		 * @param sessionId essionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param objects 需要保存的对象
		 * @return 是否保存成功
		 * @throws AlbianDataServiceException
		 */
		boolean save(String sessionId, List<? extends IAlbianObject> objects) throws AlbianDataServiceException;
		
		/**
		 * 保存对象集合到存储层
		 * <br />
		 * 注意：
		 * 对于对象的保存操作都可以使用此方法，此方法并不区分你的对象是不是已经在存储层存在。也就是说：
		 * 当对象在存储层中不存在的时候，将会执行insert操作；当对象在存储层中存在的时候，执行update操作。
		 * <br />
		 * 为了保证数据的一致性，请在使用此方法保存数据的时候，先load一下数据，然后在load的数据上更改后再调用此方法保存到存储层。
		 * 
		 * @param sessionId  此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param objects 需要保存的对象
		 * @param notifyCallback 事务完成后的通知，该通知不会受事务是否成功完成的影响，肯定会被触发
		 * @param notifyCallbackObject 通知的时候，需要传递的自定义参数
		 * @param compensateCallback 事务发生异常的时候触发的通知
		 * @param compensateCallbackObject 事务发生异常时触发通知的自定义对象
		 * @return 是否完成删除
		 * @throws AlbianDataServiceException
		 */
		boolean save(String sessionId, List<? extends IAlbianObject> objects, IPersistenceNotify notifyCallback,
				Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
				Object compensateCallbackObject) throws AlbianDataServiceException;
		
		/**
		 * 从存储层加载数据
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param cls 需要加载数据的接口信息
		 * @param loadType 加载的方式
		 * @param wheres 过滤条件
		 * @return 加载的对象
		 * @throws AlbianDataServiceException
		 */
		<T extends IAlbianObject> T loadObject(String sessionId,Class<T> cls,LoadType loadType, IChainExpression wheres)
				throws AlbianDataServiceException;
		
		/**
		 * 
		 * 从存储层加载数据
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param cls 需要加载数据的接口信息
		 * @param loadType 加载的方式
		 * @param rountingName 指定加载的数据路由
		 * @param wheres 过滤条件
		 * @return 加载的对象
		 * @throws AlbianDataServiceException
		 */
		<T extends IAlbianObject> T loadObject(String sessionId,Class<T> cls,LoadType loadType,String rountingName, IChainExpression wheres)
				throws AlbianDataServiceException;
		
		/**
		  * 从存储层加载数据
		  * <br />
		  * 该方法一般用在从存储过程中加载数据，当然也可以执行sql语句。在使用这个方法加载数据的时候，对于albianj来说是完全托管的状态。
		  * albianj不会管理你的数据路由，也不会管理你的数据库连接，也不会处理sql注入等等各种常见的数据层问题。请在使用的时候自行解决。
		  * <br />
		  * 注意：重要的事情说三遍。此方法在不到万不得已应该永远不会被使用。此方法在不到万不得已应该永远不会被使用。此方法在不到万不得已应该永远不会被使用。
		  * 
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param cls 需要加载数据的接口信息
		 * @param cmdType 执行命令的类型
		 * @param statement 执行命令的语句
		 * @return 加载的数据
		 * @throws AlbianDataServiceException
		 */
		<T extends IAlbianObject> T loadObject(String sessionId,Class<T> cls,PersistenceCommandType cmdType,
				Statement statement) throws AlbianDataServiceException;
		
		/**
		 * 从存储层批量加载数据
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param cls 需要加载数据的接口信息
		 * @param loadType 加载的方式
		 * @param wheres 过滤条件
		 * @return 加载的对象
		 * @throws AlbianDataServiceException
		 */
		<T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls, LoadType loadType,IChainExpression wheres)
				throws AlbianDataServiceException;
		
		/**
		 * 从存储层批量加载数据
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param cls 需要加载数据的接口信息
		 * @param loadType 加载的方式
		 *  @param rountingName 指定加载数据的路由
		 * @param f 过滤条件
		 * @param orderbys 排序的条件
		 * @return 加载的对象
		 * @throws AlbianDataServiceException
		 */
		<T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,String rountingName, 
				IChainExpression f,LinkedList<IOrderByCondition> orderbys)
				throws AlbianDataServiceException;
		
		/**
		 * 从存储层批量加载数据
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param cls 需要加载数据的接口信息
		 * @param loadType 加载的方式
		 * @param f 过滤条件
		 * @param orderbys 排序的条件
		 * @return 加载的对象
		 * @throws AlbianDataServiceException
		 */
		<T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,
				IChainExpression f,LinkedList<IOrderByCondition> orderbys)
				throws AlbianDataServiceException;
		
		/**
		  * 从存储层批量加载数据
		  * <br />
		  * 该方法一般用在从存储过程中加载数据，当然也可以执行sql语句。在使用这个方法加载数据的时候，对于albianj来说是完全托管的状态。
		  * albianj不会管理你的数据路由，也不会管理你的数据库连接，也不会处理sql注入等等各种常见的数据层问题。请在使用的时候自行解决。
		  * <br />
		  * 注意：重要的事情说三遍。此方法在不到万不得已应该永远不会被使用。此方法在不到万不得已应该永远不会被使用。此方法在不到万不得已应该永远不会被使用。
		  * 
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param cls 需要加载数据的接口信息
		 * @param cmdType 执行命令的类型
		 * @param statement 执行命令的语句
		 * @return 加载的数据
		 * @throws AlbianDataServiceException
		 */
		<T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,  PersistenceCommandType cmdType,
				Statement statement) throws AlbianDataServiceException;
		
		/**
		 * 从存储层批量加载数据
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param cls 需要加载数据的接口信息
		 * @param loadType 加载的方式
		 * @param start 开始加载的位置
		 * @param step 加载的数量
		 * @param wheres 过滤条件
		 * @return 加载的对象
		 * @throws AlbianDataServiceException
		 */
		<T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,
				int start,int step, IChainExpression f)
				throws AlbianDataServiceException;
		
		/**
		 * 从存储层批量加载数据
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param cls 需要加载数据的接口信息
		 * @param loadType 加载的方式
		 * @param start 开始加载的位置
		 * @param step 加载的数量
		 * @param wheres 过滤条件
		 * @param orderbys 排序的条件
		 * @return 加载的对象
		 * @throws AlbianDataServiceException 
		 */
		<T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,
				int start,int step, IChainExpression wheres,LinkedList<IOrderByCondition> orderbys)
				throws AlbianDataServiceException;
		
		/**
		 * 从存储层批量加载数据
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param cls 需要加载数据的接口信息
		 * @param loadType 加载的方式
		 * @param rountingName 指定的加载路由
		 * @param start 开始加载的位置
		 * @param step 加载的数量
		 * @param wheres 过滤条件
		 * @param orderbys 排序的条件
		 * @return 加载的对象
		 * @throws AlbianDataServiceException 
		 */
		<T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,String rountingName, 
				int start,int step,IChainExpression wheres,LinkedList<IOrderByCondition> orderbys)
				throws AlbianDataServiceException;
		
		/**
		 * 从存储层获取满足条件的对象数量
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param cls 需要加载数据的接口信息
		 * @param loadType 加载的方式
		 * @param wheres 过滤条件
		 * @return 满足条件的对象数量
		 * @throws AlbianDataServiceException 
		 */
		<T extends IAlbianObject> long loadObjectsCount(String sessionId,Class<T> cls,LoadType loadType, IChainExpression wheres) 
				throws AlbianDataServiceException;
		
		/**
		 * 从存储层获取满足条件的对象数量
		 * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
		 * @param cls 需要加载数据的接口信息
		 * @param loadType 加载的方式
		 * @param rountingName 指定的加载路由
		 * @param wheres 过滤条件
		 * @return 满足条件的对象数量
		 * @throws AlbianDataServiceException 
		 */
		<T extends IAlbianObject> long loadObjectsCount(String sessionId,Class<T> cls,LoadType loadType, 
				String rountingName,IChainExpression wheres)
				throws AlbianDataServiceException;
		
		@Deprecated
		<T extends IAlbianObject> T loadObject(String sessionId,Class<T> cls,LoadType loadType, LinkedList<IFilterCondition> wheres)
				throws AlbianDataServiceException;
		@Deprecated
		<T extends IAlbianObject> T loadObject(String sessionId,Class<T> cls,LoadType loadType,String rountingName, LinkedList<IFilterCondition> wheres)
				throws AlbianDataServiceException;
		@Deprecated
		<T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls, LoadType loadType,LinkedList<IFilterCondition> wheres)
				throws AlbianDataServiceException;
		@Deprecated
		<T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,String rountingName, 
				LinkedList<IFilterCondition> wheres,LinkedList<IOrderByCondition> orderbys)
				throws AlbianDataServiceException;
		@Deprecated
		<T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,
				LinkedList<IFilterCondition> wheres,LinkedList<IOrderByCondition> orderbys)
				throws AlbianDataServiceException;
		@Deprecated
		<T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,
				int start,int step, LinkedList<IFilterCondition> wheres)
				throws AlbianDataServiceException;
		@Deprecated
		<T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,
				int start,int step, LinkedList<IFilterCondition> wheres,LinkedList<IOrderByCondition> orderbys)
				throws AlbianDataServiceException;
		@Deprecated
		<T extends IAlbianObject> List<T> loadObjects(String sessionId,Class<T> cls,LoadType loadType,String rountingName, 
				int start,int step,LinkedList<IFilterCondition> wheres,LinkedList<IOrderByCondition> orderbys)
				throws AlbianDataServiceException;
		@Deprecated
		<T extends IAlbianObject> long loadObjectsCount(String sessionId,Class<T> cls,LoadType loadType, LinkedList<IFilterCondition> wheres) 
				throws AlbianDataServiceException;
		@Deprecated
		<T extends IAlbianObject> long loadObjectsCount(String sessionId,Class<T> cls,LoadType loadType, 
				String rountingName,LinkedList<IFilterCondition> wheres)
				throws AlbianDataServiceException;
		
		
		/**
		 * @param sessionId
		 * @param object
		 * @return
		 * @throws AlbianDataServiceException
		 */
		@Deprecated
		boolean create(String sessionId, IAlbianObject object) throws AlbianDataServiceException;

		/**
		 * @param sessionId
		 * @param object
		 * @param notifyCallback
		 * @param notifyCallbackObject
		 * @param compensateCallback
		 * @param compensateCallbackObject
		 * @return
		 * @throws AlbianDataServiceException
		 */
		@Deprecated
		boolean create(String sessionId, IAlbianObject object, IPersistenceNotify notifyCallback,
				Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
				Object compensateCallbackObject) throws AlbianDataServiceException;

		/**
		 * @param sessionId
		 * @param objects
		 * @return
		 * @throws AlbianDataServiceException
		 */
		@Deprecated
		boolean create(String sessionId, List<? extends IAlbianObject> objects) throws AlbianDataServiceException;
		
		/**
		 * @param sessionId
		 * @param objects
		 * @param notifyCallback
		 * @param notifyCallbackObject
		 * @param compensateCallback
		 * @param compensateCallbackObject
		 * @return
		 * @throws AlbianDataServiceException
		 */
		@Deprecated
		boolean create(String sessionId, List<? extends IAlbianObject> objects, IPersistenceNotify notifyCallback,
				Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
				Object compensateCallbackObject) throws AlbianDataServiceException;

		/**
		 * @param sessionId
		 * @param object
		 * @return
		 * @throws AlbianDataServiceException
		 */
		@Deprecated
		boolean modify(String sessionId, IAlbianObject object) throws AlbianDataServiceException;
		
		/**
		 * @param sessionId
		 * @param object
		 * @param notifyCallback
		 * @param notifyCallbackObject
		 * @param compensateCallback
		 * @param compensateCallbackObject
		 * @return
		 * @throws AlbianDataServiceException
		 */
		@Deprecated
		boolean modify(String sessionId, IAlbianObject object, IPersistenceNotify notifyCallback,
				Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
				Object compensateCallbackObject) throws AlbianDataServiceException;

		/**
		 * @param sessionId
		 * @param objects
		 * @return
		 * @throws AlbianDataServiceException
		 */
		@Deprecated
		boolean modify(String sessionId, List<? extends IAlbianObject> objects) throws AlbianDataServiceException;
		
		/**
		 * @param sessionId
		 * @param objects
		 * @param notifyCallback
		 * @param notifyCallbackObject
		 * @param compensateCallback
		 * @param compensateCallbackObject
		 * @return
		 * @throws AlbianDataServiceException
		 */
		@Deprecated
		boolean modify(String sessionId, List<? extends IAlbianObject> objects, IPersistenceNotify notifyCallback,
				Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
				Object compensateCallbackObject) throws AlbianDataServiceException;
	}
