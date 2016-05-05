/*
Copyright (c) 2016, Shanghai YUEWEN Information Technology Co., Ltd. 
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Shanghai YUEWEN Information Technology Co., Ltd. 
* nor the names of its contributors may be used to endorse or promote products derived from 
* this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY SHANGHAI YUEWEN INFORMATION TECHNOLOGY CO., LTD. 
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright (c) 2016 著作权由上海阅文信息技术有限公司所有。著作权人保留一切权利。

这份授权条款，在使用者符合以下三条件的情形下，授予使用者使用及再散播本软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：

* 对于本软件源代码的再散播，必须保留上述的版权宣告、此三条件表列，以及下述的免责声明。
* 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附于散播包装中的媒介方式，重制上述之版权宣告、此三条件表列，以及下述的免责声明。
* 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称，来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。

免责声明：本软件是由上海阅文信息技术有限公司及本软件之贡献者以现状提供，本软件包装不负任何明示或默示之担保责任，
包括但不限于就适售性以及特定目的的适用性为默示性担保。加州大学董事会及本软件之贡献者，无论任何条件、无论成因或任何责任主义、
无论此责任为因合约关系、无过失责任主义或因非违约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的任何直接性、间接性、
偶发性、特殊性、惩罚性或任何结果的损害（包括但不限于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等），
不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
*/
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
