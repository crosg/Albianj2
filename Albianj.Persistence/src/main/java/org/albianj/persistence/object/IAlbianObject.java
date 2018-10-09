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
package org.albianj.persistence.object;

import org.albianj.persistence.context.dactx.AlbianDataAccessOpt;
import org.albianj.persistence.db.AlbianDataServiceException;

import java.io.Serializable;

/**
 * Albian对象的基接口，所有需要Albianj管理的数据对象都需要直接或者间接的继承置此接口。
 * </br>
 * 一般情况下，没有必须要直接继承此接口，继承该接口的抽象类 org.albianj.persistence.impl.object.FreeAlbianObject
 * </br>
 * 如果必须直接继承此接口，必须要明确IsAlbianNew和OldAlbianObject的值，否则数据库操作将会出现无法预料的问题。
 * <p>
 *
 * @author Seapeak
 * @see org.albianj.persistence.impl.object.FreeAlbianObject
 */
public interface IAlbianObject extends Serializable {

    /**
     * 此变量为AlbianObject的默认缓存名称.
     * </br>
     * 当persisten.xml配置文件中的AlbianObject缓存打开(Enable=true)并且Name项为空、未配置或者是""时，使用此名字。
     * </br>
     * Name的xpath：AlbianObjects/AlbianObject/Cache/Name
     */
    static final String AlbianObjectCachedNameDefault = "AlbianObjectCached";

    /**
     * Albian 内核级方法，不要调用。
     * </br>
     * 此方法为Albian内部使用
     * <p>
     *
     * @return 当前对象是否为新建对象。new的对象为新对象，从数据库load或者find的对象为old对象
     */
    public boolean getIsAlbianNew();

    /**
     * Albian 内核级方法，不要调用
     * </br>
     * 此方法为Albian内部使用，调用此方法将会影响到对于数据库的Insert和Update操作，导致数据不正确或者操作无法完成
     * <p>
     *
     * @param isNew:默认为true，方便初始化对象操作；当从数据库获取对象时，isNew将会自动赋值为false
     */
    public void setIsAlbianNew(boolean isNew);

    /**
     * Albian 内核级方法，不要调用
     *
     * @param key
     * @param v
     */
    public void setOldAlbianObject(String key, Object v);

    /**
     * Albian 内核级方法，不要调用
     *
     * @param key
     * @return
     */
    public Object getOldAlbianObject(String key);


    boolean needUpdate() throws AlbianDataServiceException;

    boolean needUpdate(String sessionId) throws AlbianDataServiceException;

}
