package org.albianj.persistence.service;

import org.albianj.persistence.context.IPersistenceCompensateNotify;
import org.albianj.persistence.context.IPersistenceNotify;
import org.albianj.persistence.context.dactx.IDataAccessContext;
import org.albianj.persistence.context.dactx.IQueryContext;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.db.PersistenceCommandType;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.service.BuiltinNames;
import org.albianj.service.IService;

import java.math.BigInteger;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuhaifeng on 16/12/22.
 * 不推荐使用IAlbainPersistenceService，而使用IAlbianDataAccessService
 */
public interface IDataAccessService extends IService {

    /**
     * 此service在service.xml中的id
     */
    String Name = BuiltinNames.AlbianDataAccessServiceName;

    /**
     * 从存储中删除指定的对象
     * <br />
     * 注意：使用albianj尽量不要使用“硬删除”，应尽量使用“软删除”。
     * 原因是albianj会自动的对于分布式事务的“二次提交”做完整性事务和回滚，硬删除可能会出现回滚不完全。
     * 如果非要使用“硬删除”，最好请在删除之前先根据id加载一下数据，然后在和使用load到的数据进行删除。
     *
     * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param object    需要删除的对象
     * @return 是否完成删除
     * @throws AlbianDataServiceException
     */
    boolean remove(String sessionId, IAlbianObject object) throws AlbianDataServiceException;

    /**
     * 从存储中删除指定的对象
     * <br />
     * 注意：使用albianj尽量不要使用“硬删除”，应尽量使用“软删除”。
     * 原因是albianj会自动的对于分布式事务的“二次提交”做完整性事务和回滚，硬删除可能会出现回滚不完全。
     * 如果非要使用“硬删除”，最好请在删除之前先根据id加载一下数据，然后在和使用load到的数据进行删除。
     *
     * @param sessionId                此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param object                   需要删除的对象
     * @param notifyCallback           事务完成后的通知，该通知不会受事务是否成功完成的影响，肯定会被触发
     * @param notifyCallbackObject     通知的时候，需要传递的自定义参数
     * @param compensateCallback       事务发生异常的时候触发的通知
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
     * @param objects   需要删除的对象
     * @return 是否完成删除
     * @throws AlbianDataServiceException
     */
    boolean remove(String sessionId, List<? extends IAlbianObject> objects) throws AlbianDataServiceException;

    /**
     * 从存储中删除指定的对象集合
     * <br />
     * 注意：使用albianj尽量不要使用“硬删除”，应尽量使用“软删除”。
     * 原因是albianj会自动的对于分布式事务的“二次提交”做完整性事务和回滚，硬删除可能会出现回滚不完全。
     * 如果非要使用“硬删除”，最好请在删除之前先根据id加载一下数据，然后在和使用load到的数据进行删除。
     *
     * @param sessionId                此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param objects                  需要删除的对象
     * @param notifyCallback           事务完成后的通知，该通知不会受事务是否成功完成的影响，肯定会被触发
     * @param notifyCallbackObject     通知的时候，需要传递的自定义参数
     * @param compensateCallback       事务发生异常的时候触发的通知
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
     * @param object    需要保存的对象
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
     * @param sessionId                essionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param object                   需要保存的对象
     * @param notifyCallback           事务完成后的通知，该通知不会受事务是否成功完成的影响，肯定会被触发
     * @param notifyCallbackObject     通知的时候，需要传递的自定义参数
     * @param compensateCallback       事务发生异常的时候触发的通知
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
     * @param objects   需要保存的对象
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
     * @param sessionId                此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param objects                  需要保存的对象
     * @param notifyCallback           事务完成后的通知，该通知不会受事务是否成功完成的影响，肯定会被触发
     * @param notifyCallbackObject     通知的时候，需要传递的自定义参数
     * @param compensateCallback       事务发生异常的时候触发的通知
     * @param compensateCallbackObject 事务发生异常时触发通知的自定义对象
     * @return 是否完成删除
     * @throws AlbianDataServiceException
     */
    boolean save(String sessionId, List<? extends IAlbianObject> objects, IPersistenceNotify notifyCallback,
                 Object notifyCallbackObject, IPersistenceCompensateNotify compensateCallback,
                 Object compensateCallbackObject) throws AlbianDataServiceException;

    /**
     * 从存储层加载数据
     *
     * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param cls       需要加载数据的接口信息
     * @param loadType  加载的方式
     * @param wheres    过滤条件
     * @return 加载的对象
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls, LoadType loadType, IChainExpression wheres)
            throws AlbianDataServiceException;


    <T extends IAlbianObject> T loadObjectById(String sessionId, Class<T> cls, LoadType loadType, BigInteger id)
            throws AlbianDataServiceException;

    <T extends IAlbianObject> T loadObjectById(String sessionId, Class<T> cls, LoadType loadType, String rountingName, BigInteger id)
            throws AlbianDataServiceException;


    /**
     * 慎用
     *
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, LoadType loadType)
            throws AlbianDataServiceException;

    /**
     * 慎用
     *
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, LoadType loadType, LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException;

    /**
     * 慎用
     *
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, LoadType loadType, String rountingName, LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException;


    /**
     * 从存储层加载数据
     *
     * @param sessionId    此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param cls          需要加载数据的接口信息
     * @param loadType     加载的方式
     * @param rountingName 指定加载的数据路由
     * @param wheres       过滤条件
     * @return 加载的对象
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls, LoadType loadType, String rountingName, IChainExpression wheres)
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
     * @param cls       需要加载数据的接口信息
     * @param cmdType   执行命令的类型
     * @param statement 执行命令的语句
     * @return 加载的数据
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls, PersistenceCommandType cmdType,
                                           Statement statement) throws AlbianDataServiceException;

    /**
     * 从存储层批量加载数据
     *
     * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param cls       需要加载数据的接口信息
     * @param loadType  加载的方式
     * @param wheres    过滤条件
     * @return 加载的对象
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, IChainExpression wheres)
            throws AlbianDataServiceException;

    /**
     * 从存储层批量加载数据
     *
     * @param sessionId    此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param cls          需要加载数据的接口信息
     * @param loadType     加载的方式
     * @param rountingName 指定加载数据的路由
     * @param f            过滤条件
     * @param orderbys     排序的条件
     * @return 加载的对象
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, String rountingName,
                                                  IChainExpression f, LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException;

    /**
     * 从存储层批量加载数据
     *
     * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param cls       需要加载数据的接口信息
     * @param loadType  加载的方式
     * @param f         过滤条件
     * @param orderbys  排序的条件
     * @return 加载的对象
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                  IChainExpression f, LinkedList<IOrderByCondition> orderbys)
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
     * @param cls       需要加载数据的接口信息
     * @param cmdType   执行命令的类型
     * @param statement 执行命令的语句
     * @return 加载的数据
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, PersistenceCommandType cmdType,
                                                  Statement statement) throws AlbianDataServiceException;

    /**
     * 执行自定义sql语句来获取结果，暂时不支持存储过程
     *
     * @param sessionId
     * @param cls
     * @param storageName
     * @param cmdType
     * @param text
     * @param paras
     * @param <T>
     * @return
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, IRunningStorageAttribute storageName, PersistenceCommandType cmdType,
                                                  String text, Map<String, ISqlParameter> paras) throws AlbianDataServiceException;

    /**
     * 执行自定义sql语句来获取结果，暂时不支持存储过程
     *
     * @param sessionId
     * @param cls
     * @param storageName
     * @param cmdType
     * @param text
     * @param paras
     * @param <T>
     * @return
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> List<T> loadObject(String sessionId, Class<T> cls, IRunningStorageAttribute storageName, PersistenceCommandType cmdType,
                                                 String text, Map<String, ISqlParameter> paras) throws AlbianDataServiceException;


    /**
     * 从存储层批量加载数据
     *
     * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param cls       需要加载数据的接口信息
     * @param loadType  加载的方式
     * @param start     开始加载的位置
     * @param step      加载的数量
     * @return 加载的对象
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                  int start, int step, IChainExpression f)
            throws AlbianDataServiceException;

    /**
     * 从存储层批量加载数据
     *
     * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param cls       需要加载数据的接口信息
     * @param loadType  加载的方式
     * @param start     开始加载的位置
     * @param step      加载的数量
     * @param wheres    过滤条件
     * @param orderbys  排序的条件
     * @return 加载的对象
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                  int start, int step, IChainExpression wheres, LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException;

    /**
     * 从存储层批量加载数据
     *
     * @param sessionId    此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param cls          需要加载数据的接口信息
     * @param loadType     加载的方式
     * @param rountingName 指定的加载路由
     * @param start        开始加载的位置
     * @param step         加载的数量
     * @param wheres       过滤条件
     * @param orderbys     排序的条件
     * @return 加载的对象
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, String rountingName,
                                                  int start, int step, IChainExpression wheres, LinkedList<IOrderByCondition> orderbys)
            throws AlbianDataServiceException;

    /**
     * 从存储层获取满足条件的对象数量
     *
     * @param sessionId 此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param cls       需要加载数据的接口信息
     * @param loadType  加载的方式
     * @param wheres    过滤条件
     * @return 满足条件的对象数量
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls, LoadType loadType, IChainExpression wheres)
            throws AlbianDataServiceException;

    /**
     * 从存储层获取满足条件的对象数量
     *
     * @param sessionId    此次方法调用的sessionid，建议使用userid或者是任何可以方便排错的id，如果该id为null，albianj会自动生成一个sessionid
     * @param cls          需要加载数据的接口信息
     * @param loadType     加载的方式
     * @param rountingName 指定的加载路由
     * @param wheres       过滤条件
     * @return 满足条件的对象数量
     * @throws AlbianDataServiceException
     */
    <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls, LoadType loadType,
                                                    String rountingName, IChainExpression wheres)
            throws AlbianDataServiceException;


    //-------增加强制制定索引名字

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls, LoadType loadType, IChainExpression wheres, String idxName)
            throws AlbianDataServiceException;

    public <T extends IAlbianObject> T loadObject(String sessionId, Class<T> cls,
                                                  LoadType loadType, String rountingName, IChainExpression wheres, String idxName)
            throws AlbianDataServiceException;

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, IChainExpression wheres, String idxName)
            throws AlbianDataServiceException;

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         IChainExpression wheres, LinkedList<IOrderByCondition> orderbys, String idxName)
            throws AlbianDataServiceException;

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, String rountingName,
                                                         IChainExpression wheres, LinkedList<IOrderByCondition> orderbys, String idxName)
            throws AlbianDataServiceException;

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         int start, int step, IChainExpression wheres, String idxName)
            throws AlbianDataServiceException;

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                         int start, int step, IChainExpression wheres, LinkedList<IOrderByCondition> orderbys, String idxName)
            throws AlbianDataServiceException;

    public <T extends IAlbianObject> List<T> loadObjects(String sessionId, Class<T> cls, LoadType loadType, String rountingName,
                                                         int start, int step, IChainExpression wheres, LinkedList<IOrderByCondition> orderbys, String idxName)
            throws AlbianDataServiceException;


    /**
     * 慎用
     *
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     * @throws AlbianDataServiceException
     */
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, LoadType loadType, String idxName)
            throws AlbianDataServiceException;

    /**
     * 慎用
     *
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     * @throws AlbianDataServiceException
     */
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, LoadType loadType, LinkedList<IOrderByCondition> orderbys, String idxName)
            throws AlbianDataServiceException;

    /**
     * 慎用
     *
     * @param sessionId
     * @param cls
     * @param loadType
     * @param <T>
     * @return
     * @throws AlbianDataServiceException
     */
    public <T extends IAlbianObject> List<T> loadAllObjects(String sessionId, Class<T> cls, LoadType loadType,
                                                            String rountingName, LinkedList<IOrderByCondition> orderbys, String idxName)
            throws AlbianDataServiceException;

    public <T extends IAlbianObject> T loadObjectById(String sessionId, Class<T> cls, LoadType loadType, BigInteger id, String idxName)
            throws AlbianDataServiceException;

    public <T extends IAlbianObject> T loadObjectById(String sessionId, Class<T> cls, LoadType loadType, String rountingName, BigInteger id, String idxName)
            throws AlbianDataServiceException;

    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           LoadType loadType, IChainExpression wheres, String idxName)
            throws AlbianDataServiceException;

    public <T extends IAlbianObject> long loadObjectsCount(String sessionId, Class<T> cls,
                                                           LoadType loadType, String rountingName, IChainExpression wheres, String idxName)
            throws AlbianDataServiceException;


    IDataAccessContext newDataAccessContext();

    IQueryContext newQueryContext();


}
