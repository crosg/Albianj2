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

import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.context.dactx.IAlbianObjectWarp;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.object.rants.AlbianObjectDataFieldRant;
import org.albianj.persistence.service.AlbianEntityMetadata;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

import java.util.HashMap;
import java.util.Map;


public abstract class FreeAlbianObject implements IAlbianObject {


    @AlbianObjectDataFieldRant(IsSave = false, Ignore = true)
    private static final long serialVersionUID = 1608573290358087720L;

    @AlbianObjectDataFieldRant(IsSave = false, Ignore = true)
    protected transient HashMap<String, Object> dic = null;

    @AlbianObjectDataFieldRant(IsSave = false, Ignore = true)
    protected transient Map<String, IAlbianObjectWarp> chainEntity = null;

    @AlbianObjectDataFieldRant(IsSave = false, Ignore = true)
    private transient boolean isAlbianNew = true;

    protected FreeAlbianObject() {
        chainEntity = new HashMap<>();
    }


    // public String getId()
    // {
    // return this.id;
    // }
    //
    // public void setId(String id)
    // {
    // this.id = id;
    // }

    /**
     * albianj
     * kernnel???????????????????????????????????????????????????????????
     * ????????
     * ??????????????albianj?????????????????????????????????????????????
     * ??????????????????????????????????????????????????????
     */
    @AlbianObjectMemberAttribute(IsSave = false, Ignore = true)
    public boolean getIsAlbianNew() {
        return this.isAlbianNew;
    }

    /**
     * albianj
     * kernnel???????????????????????????????????????????????????????????
     * ????????
     * ??????????????albianj?????????????????????????????????????????????
     * ??????????????????????????????????????????????????????
     */
    @AlbianObjectMemberAttribute(IsSave = false, Ignore = true)
    public void setIsAlbianNew(boolean isAlbianNew) {
        this.isAlbianNew = isAlbianNew;
    }

    @AlbianObjectMemberAttribute(IsSave = false, Ignore = true)
    public void setOldAlbianObject(String key, Object v) {
        if (null == dic) {
            dic = new HashMap<String, Object>();
        }
        dic.put(key, v);
    }

    @AlbianObjectMemberAttribute(IsSave = false, Ignore = true)
    public Object getOldAlbianObject(String key) {
        if (null == dic) {
            return null;
        }
        return dic.get(key);
    }


    @Deprecated
    @org.albianj.comment.SpecialWarning("不推荐使用，推荐使用带sessionid参数的同名函数")
    public boolean needUpdate() throws AlbianDataServiceException {
        return needUpdate(IAlbianLoggerService2.InnerThreadName);
    }

    @Deprecated
    @org.albianj.comment.SpecialWarning("不推荐使用，推荐使用带sessionid,itf的同名函数")
    public boolean needUpdate(String sessionId) throws AlbianDataServiceException {
        String className = this.getClass().getName();
        String itf = AlbianEntityMetadata.type2Interface(className);
        return needUpdate(sessionId, itf);
//        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
//        String interName = amps.getAlbianObjectInterface(className);
//        IAlbianObjectAttribute albianObject = amps.getAlbianObjectAttribute(interName);
//        if (null == albianObject) {
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
//                    sessionId, AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
//                    "PersistenceService is error.",
//                    "albian-object:%s attribute is not found.",
//                    className);
//        }
//
//        PropertyDescriptor[] propertyDesc = amps.getAlbianObjectPropertyDescriptor(className);
//        if (null == propertyDesc) {
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
//                    sessionId,AlbianLoggerLevel.Error,null,
//                    AlbianModuleType.AlbianPersistence,"PersistenceService is error.",
//                    "albian-object:%s PropertyDescriptor is not found.",
//                    className);
//        }
//
//        Map<String, IMemberAttribute> mapMemberAttributes = albianObject.getMembers();
//        try {
//            for (PropertyDescriptor p : propertyDesc) {
//                String name = p.getName();
//                IMemberAttribute attr = mapMemberAttributes.get(name.toLowerCase());
//                if (!attr.getIsSave())
//                    continue;
//                Object value = p.getReadMethod().invoke(this);
//                Object oldValue = getOldAlbianObject(name);
//
//                if ((null == value && null == oldValue)) {
//                    continue;
//                }
//                if (null != value && value.equals(oldValue)) {
//                    continue;
//                }
//                if (null != oldValue && oldValue.equals(value)) {
//                    continue;
//                }
//                return true;
//            }
//        } catch (Exception e) {
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
//                    sessionId,AlbianLoggerLevel.Error,e,
//                    AlbianModuleType.AlbianPersistence,"PersistenceService is error.",
//                    "invoke bean read method is error.the property is:%s.job id:%s.",
//                    albianObject.getType());
//        }
//
//        return false;

    }

    public boolean needUpdate(String sessionId, Class<? extends IAlbianObject> itf) throws AlbianDataServiceException {
        return needUpdate(sessionId, itf.getName());
    }

    private boolean needUpdate(String sessionId, String itf) throws AlbianDataServiceException {
        String className = this.getClass().getName();
        IAlbianObjectAttribute entiryAttr = AlbianEntityMetadata.getEntityMetadata(itf);
        if (null == entiryAttr) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                    "PersistenceService is error.",
                    "albian-object:%s attribute is not found.",
                    className);
        }


        Map<String, IAlbianEntityFieldAttribute> fields = entiryAttr.getFields();
        if (Validate.isNullOrEmpty(fields)) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error, null,
                    AlbianModuleType.AlbianPersistence, "PersistenceService is error.",
                    "albian-object:%s PropertyDescriptor is not found.",
                    className);
        }
        try {

            for (IAlbianEntityFieldAttribute fieldAttr : fields.values()) {
                if (!fieldAttr.getIsSave()) continue;
                Object newVal = fieldAttr.getEntityField().get(this);
                Object oldValue = getOldAlbianObject(AlbianEntityMetadata.makeFieldsKey(fieldAttr.getPropertyName()));

                if ((null == newVal && null == oldValue)) {
                    continue;
                }
                if (null != newVal && newVal.equals(oldValue)) {
                    continue;
                }
                if (null != oldValue && oldValue.equals(newVal)) {
                    continue;
                }
                return true;
            }
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error, e,
                    AlbianModuleType.AlbianPersistence, "PersistenceService is error.",
                    "invoke bean read method is error.the property is:%s.job id:%s.",
                    entiryAttr.getType());
        }

        return false;

    }
}

