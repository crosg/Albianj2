package org.albianj.unid.service;

import java.math.BigInteger;
import java.sql.Timestamp;

import org.albianj.argument.RefArg;
import org.albianj.service.IAlbianService;

/**
 * 获取全站唯一Id，全站唯一为Integer类型，长度为64. 
 * </br>
 * 注意：
 * </br>
 * 		1：该服务需要服务器的IdCreator支持，请联系管理员确认IdCreator已可用
 * </br>
 * 		2: 若全站唯一Id为String类型，请使用 @see org.albianj.service.impl.AlbianIdService
 * </br>
 * @author Seapeak
 *
 */
public interface IAlbianRemoteUNIDService extends IAlbianService {
	
	static String Name = "AlbianRemoteIdService";
	public static final int TYPE_DEFAULT = 1;

	/**
	 * 生成一个使用二进制算法的组合的id，改id对于人不是太友好，不能被很好的辨认
	 * 但是对算法友好，计算较快
	 * @return 二进制算法生成的一个十进制数，uint64类型
	 */
	public BigInteger createBinaryId();
	/**
	 * 生成一个十进制、完整的id。
	 * 这个id最后的4位将会从0-9999依次出现，这种id适合根据最后的4位做hash或者是轮询分库分表
	 * @return 十进制生成的id
	 */
	public BigInteger createCompleteDigital();
	/**
	 * 生成一个十进制，不完整的id
	 * 这个id的最后两位是00，永远是00。这种id比较适合根据自己的规则来指定分库分表，
	 * 如果要用这个id来做取模或者是轮询，必须排除最后的2位，排除最后的2位后，和createCompleteDigital生成的id一致
	 * @return
	 */
	public BigInteger createIncompleteDigital();
	
	/**
	 * 生成一个十进制，保证递增并且完整的十进制id
	 *  这个id最后的4位将会从0-9999依次出现，但是如果新的1秒开始，这个计数将会从0重新开始。
	 *  注意，这个id不是太适合取模或者是hash等分库分表，因为后面的四位数生成的不充分，可能会引起数据存储的数据量不平衡
	 * @return
	 */
	public BigInteger createIncrAndCompleteDigital();
	
	

	/*
	 * 内容中心专用，如需要，请使用createIncompleteDigital函数替代
	 */
	public BigInteger createBookId();
	
	/**
	 * albianj kernel专用，需需要请使用createIncompleteDigital替代
	 * @return
	 */
	public BigInteger createAuthorId();
	
	/**
	 * albianj kernel专用，需需要请使用createIncompleteDigital替代
	 * @return
	 */
	public BigInteger createConfigItemId();

	/**
	 * 内容中心专用，需需要请使用createBinaryId替代
	 * @return
	 */
	public BigInteger createUNID();

	/**
	 * 内容中心专用，需需要请使用createBinaryId替代
	 * @return
	 */
	public BigInteger createUNID(int type);


	/**
	 * 反解生成的id，目前只对十进制id提供
	 * @param bi：需要反解的id
	 * @param time 解析出来的id生成的时间，该参数可以为null
	 * @param type 解析出来的id的type，一般这个id没有什么用途，该参数可以为null
	 */
	public void unpack(BigInteger bi, RefArg<Timestamp> time,
			RefArg<Integer> type);

	/**
	 * 
	 * @param bi
	 * @param time
	 * @param sed
	 * @param idx
	 */
	public void unpack(BigInteger bi, RefArg<Timestamp> time,
			RefArg<Integer> sed, RefArg<Integer> idx);

}
