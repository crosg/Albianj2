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
package org.albianj.unid.service;

import org.albianj.argument.RefArg;
import org.albianj.service.IAlbianService;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * 获取全站唯一Id，全站唯一为Integer类型，长度为64.
 * </br>
 * 注意：
 * </br>
 * 1：该服务需要服务器的IdCreator支持，请联系管理员确认IdCreator已可用
 * </br>
 * 2: 若全站唯一Id为String类型，请使用 @see org.albianj.service.impl.AlbianIdService
 * </br>
 *
 * @author Seapeak
 */
public interface IAlbianRemoteUNIDService extends IAlbianService {

    static String Name = "AlbianRemoteIdService";
    public static final int TYPE_DEFAULT = 1;

    /**
     * 生成一个使用二进制算法的组合的id，改id对于人不是太友好，不能被很好的辨认
     * 但是对算法友好，计算较快
     *
     * @return 二进制算法生成的一个十进制数，uint64类型
     */
    public BigInteger createBinaryId();

    /**
     * 生成一个十进制、完整的id。
     * 这个id最后的4位将会从0-9999依次出现，这种id适合根据最后的4位做hash或者是轮询分库分表
     *
     * @return 十进制生成的id
     */
    public BigInteger createCompleteDigital();

    /**
     * 生成一个十进制，不完整的id
     * 这个id的最后两位是00，永远是00。这种id比较适合根据自己的规则来指定分库分表，
     * 如果要用这个id来做取模或者是轮询，必须排除最后的2位，排除最后的2位后，和createCompleteDigital生成的id一致
     *
     * @return
     */
    public BigInteger createIncompleteDigital();

    /**
     * 生成一个十进制，保证递增并且完整的十进制id
     * 这个id最后的4位将会从0-9999依次出现，但是如果新的1秒开始，这个计数将会从0重新开始。
     * 注意，这个id不是太适合取模或者是hash等分库分表，因为后面的四位数生成的不充分，可能会引起数据存储的数据量不平衡
     *
     * @return
     */
    public BigInteger createIncrAndCompleteDigital();


    /*
     * 内容中心专用，如需要，请使用createIncompleteDigital函数替代
     */
    public BigInteger createBookId();

    /**
     * albianj kernel专用，需需要请使用createIncompleteDigital替代
     *
     * @return
     */
    public BigInteger createAuthorId();

    /**
     * albianj kernel专用，需需要请使用createIncompleteDigital替代
     *
     * @return
     */
    public BigInteger createConfigItemId();

    /**
     * 内容中心专用，需需要请使用createBinaryId替代
     *
     * @return
     */
    public BigInteger createUNID();

    /**
     * 内容中心专用，需需要请使用createBinaryId替代
     *
     * @return
     */
    public BigInteger createUNID(int type);

    public BigInteger createContractId();


    /**
     * 反解生成的id，目前只对十进制id提供
     *
     * @param bi：需要反解的id
     * @param time       解析出来的id生成的时间，该参数可以为null
     * @param type       解析出来的id的type，一般这个id没有什么用途，该参数可以为null
     */
    public void unpack(BigInteger bi, RefArg<Timestamp> time,
                       RefArg<Integer> type);

    /**
     * @param bi
     * @param time
     * @param sed
     * @param idx
     */
    public void unpack(BigInteger bi, RefArg<Timestamp> time,
                       RefArg<Integer> sed, RefArg<Integer> idx);

}
