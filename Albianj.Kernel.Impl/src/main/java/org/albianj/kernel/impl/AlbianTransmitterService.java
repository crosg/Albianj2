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
package org.albianj.kernel.impl;

import ognl.Ognl;
import org.albianj.aop.impl.AlbianServiceAopProxy;
import org.albianj.except.AlbianRuntimeException;
import org.albianj.kernel.*;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.reflection.AlbianReflect;
import org.albianj.reflection.AlbianTypeConvert;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.*;
import org.albianj.service.impl.AlbianServiceParser;
import org.albianj.service.impl.FreeAlbianServiceParser;
import org.albianj.service.parser.IAlbianParserService;
import org.albianj.verify.Validate;
import org.apache.bcel.generic.IALOAD;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Seapeak
 */
@AlbianKernel
public class AlbianTransmitterService implements IAlbianTransmitterService {
    private static AlbianState state = AlbianState.Normal;
    private static Date startDateTime;
    private static String serialId;

    public String getServiceName(){
        return Name;
    }


    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#getStartDateTime()
     */
    @Override
    public Date getStartDateTime() {
        return startDateTime;
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#getSerialId()
     */
    @Override
    public String getSerialId() {
        return serialId;
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#getLifeState()
     */
    @Override
    public AlbianState getLifeState() {
        return state;
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#start(java.lang.String)
     */
    @Override
    public void start(String configUrl) throws Exception {
        makeEnvironment();
        KernelSetting.setAlbianConfigFilePath(configUrl);
        start();
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#start(java.lang.String, java.lang.String)
     */
    @Override
    public void start(String kernelpath, String configPath)
            throws Exception {
        makeEnvironment();
        KernelSetting.setAlbianConfigFilePath(configPath);
        KernelSetting.setAlbianKernelConfigFilePath(kernelpath);
        start();
    }

    @Override
    public void start() throws Exception {
        makeEnvironment();
        startDateTime = new Date();
        Class<?> cls = AlbianClassLoader.getInstance().loadClass("org.albianj.kernel.impl.AlbianLogicIdService");
        if (null == cls) {
            state = AlbianState.Unloaded;
            System.err.println("no the logic id plugin and exit.");
            return;
        }
        IAlbianLogicIdService lid = (IAlbianLogicIdService) cls.newInstance();
        if (null == lid) {
            state = AlbianState.Unloaded;
            System.err.println("can not new the logger instance and exit.");
            return;
        }
        ServiceContainer.addService(IAlbianLogicIdService.Name, lid);
        serialId = lid.generate32UUID();
        state = AlbianState.Initing;
        IAlbianParserService parser = new AlbianServiceParser();
        parser.init();

        System.out.println("startup albianj with normal.");
        doStart();
    }

    public void doStart() throws Exception {
        //the logger is essential module so must init first
        Class<?> cls = AlbianClassLoader.getInstance().loadClass("org.albianj.logger.impl.AlbianLoggerService");
        if (null == cls) {
            state = AlbianState.Unloaded;
            System.err.println("no the logger plugin and exit.");
            return;
        }
        IAlbianLoggerService log = (IAlbianLoggerService) cls.newInstance();
        if (null == log) {
            state = AlbianState.Unloaded;
            System.err.println("can not new the logger instance and exit.");
            System.exit(1);
        }
        try {
            log.beforeLoad();
            log.loading();
            log.afterLoading();
        } catch (Exception e) {
            state = AlbianState.Unloaded;
            System.err.println("loading logger is fail and exit.");
            return;
        }
        ServiceContainer.addService(IAlbianLoggerService.Name, log);

        try {
            Class<?> logger2Clazz = AlbianClassLoader.getInstance().loadClass("org.albianj.logger.impl.AlbianLoggerService2");
            if (null != logger2Clazz) {
                IAlbianLoggerService2 logV2 = (IAlbianLoggerService2) logger2Clazz.newInstance();
                if (null != logV2) {
                    try {
                        logV2.beforeLoad();
                        logV2.loading();
                        logV2.afterLoading();
                    } catch (Exception e) {
                        state = AlbianState.Unloaded;
                        System.err.println("loading logger v2 is fail but still running.");
                    }
                }
                ServiceContainer.addService(IAlbianLoggerService2.Name, logV2);
            }
        }catch (Throwable t){
            state = AlbianState.Unloaded;
            System.err.println("loading logger v2 is fail and exit.");
            return;
        }




        Map<String, IAlbianServiceAttribute> totalMap = (Map<String, IAlbianServiceAttribute>)
                ServiceAttributeMap
                        .get(FreeAlbianServiceParser.ALBIANJSERVICEKEY);

        @SuppressWarnings("unchecked")
        Map<String, IAlbianServiceAttribute> map = (Map<String, IAlbianServiceAttribute>)
                ServiceAttributeMap
                        .get(FreeAlbianServiceParser.ALBIANJSERVICEKEY);

        Map<String,IAlbianServiceAttribute> mapAttr = new HashMap<>();
        mapAttr.putAll(map); // copy it for field setter

        Map<String, IAlbianServiceAttribute> failMap = new LinkedHashMap<String, IAlbianServiceAttribute>();
        int lastFailSize = 0;
        int currentFailSize = 0;
        Exception e = null;
        while (true) {
            lastFailSize = currentFailSize;
            currentFailSize = 0;
            String sType = null;
            String id = null;
            String sInterface = null;
            for (Map.Entry<String, IAlbianServiceAttribute> entry : map
                    .entrySet())
                try {
                    IAlbianServiceAttribute serviceAttr = entry.getValue();
                    sType = serviceAttr.getType();
                    id = serviceAttr.getId();

                    sInterface = serviceAttr.getInterface();
                    Class<?> cla = AlbianClassLoader.getInstance().loadClass(serviceAttr.getType());
                    if (null == cla) {

                        AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                                IAlbianLoggerService2.InnerThreadName,
                                AlbianLoggerLevel.Info, null , AlbianModuleType.AlbianKernel,
                                "Transmitter is error.", "load class:%s for servcice:%s is fail.",
                                sType,id);
                    }

                    Class<?> itf = null;
                    if (!Validate.isNullOrEmptyOrAllSpace(serviceAttr.getInterface())) {
                        itf = AlbianClassLoader.getInstance().loadClass(serviceAttr.getInterface());
                        if (!itf.isAssignableFrom(cla)) {
                            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                                    IAlbianLoggerService2.InnerThreadName,
                                    AlbianLoggerLevel.Warn,"init service:%s with class:%s is not implement from interface:%s.",
                                    id,sType,sInterface);
                        }

                        if (!IAlbianService.class.isAssignableFrom(itf)) {
                            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                                    IAlbianLoggerService2.InnerThreadName,
                                    AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianKernel,
                                    "Transmitter is fail.", "init service :%s with class:%s and interface:%s must implements from IAlbianService  .",
                                    id,sType,sInterface);
                        }
                    }

                    if (!IAlbianService.class.isAssignableFrom(cla)) {
                        AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                                IAlbianLoggerService2.InnerThreadName,
                                AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianKernel,
                                "Transmitter is fail.", "init service :%s class:%s  must implements from interface:%s  .",
                                id,sType,sInterface);
                    }


                    IAlbianService service = (IAlbianService) cla.newInstance();

                    service.beforeLoad();
                    service.loading();
                    service.afterLoading();
                    if (Validate.isNullOrEmpty(serviceAttr.getAopAttributes())) {
                        ServiceContainer.addService(id, service);
                    } else {
                        AlbianServiceAopProxy proxy = new AlbianServiceAopProxy();
                        IAlbianService serviceProxy = (IAlbianService) proxy.newInstance(service, serviceAttr.getAopAttributes());
                        serviceProxy.setRealService(service);
                        serviceProxy.beforeLoad();
                        serviceProxy.loading();
                        serviceProxy.afterLoading();
                        ServiceContainer.addService(id, serviceProxy);
                    }

                } catch (Exception exc) {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                            IAlbianLoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Info, exc , AlbianModuleType.AlbianKernel,
                            "Kernel is error.", "load and init service:%s with class:%s is fail.",
                            id,sType);
                    e = exc;
                    currentFailSize++;
                    failMap.put(entry.getKey(), entry.getValue());
                }
            if (0 == currentFailSize) {
                // if open the distributed mode,
                // please contact to manager machine to logout the system.
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService.AlbianRunningLoggerName,
                            IAlbianLoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Info,
                            "load service is success,then set field in the services!");

                break;// load service successen
            }

            if (lastFailSize == currentFailSize) {
                // startup the service fail in this times,
                // so throw the exception and stop the albianj engine
                state = AlbianState.Unloading;
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                        IAlbianLoggerService2.InnerThreadName,
                        AlbianLoggerLevel.Error,"startup albianj engine is fail,maybe cross refernce.");
                if (null != e) {
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                            IAlbianLoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Error,e,"startup the service:%s is fail.",failMap.keySet().toString());
                }
                ServiceContainer.clear();
                state = AlbianState.Unloaded;
                throw e;
            } else {
                map.clear();
                map.putAll(failMap);
                failMap.clear();
            }
        }

        //set field in service
        if(!setServiceFields(totalMap)) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName,
                    AlbianLoggerLevel.Error,
                    " set field in the services is fail!startup albianj is fail.");
            throw new AlbianRuntimeException("startup albianj is fail.");

        }
        state = AlbianState.Running;
        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService.AlbianRunningLoggerName,
                IAlbianLoggerService2.InnerThreadName,
                AlbianLoggerLevel.Info,
                "set fields in the service over.Startup albianj is success!");

    }

    private boolean setServiceFields(Map<String,IAlbianServiceAttribute> attrMap) {
        // set fields last
        // set fields at the end because for ref service across at some servics
        int last_fail_times = 0;
        int curr_fail_times = 0;
        while(true) {
            for (IAlbianServiceAttribute attr : attrMap.values()) {
                if (Validate.isNullOrEmpty(attr.getServiceFields())) {
                    continue;
                }
                IAlbianService service = ServiceContainer.getService(attr.getId());
                for (IAlbianServiceFieldAttribute fAttr : attr.getServiceFields().values()) {
                    if(fAttr.isReady()) {
                        continue; // already set
                    }
                    if (!fAttr.getType().toLowerCase().equals("ref")) { // not set ref
                        try {
                            Object o = AlbianTypeConvert.toRealObject(
                                    fAttr.getType(), fAttr.getValue());
                            fAttr.getField().set(service, o);
                            fAttr.setReady(true);
                        }catch (Exception e){
                            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                                    IAlbianLoggerService2.InnerThreadName,
                                    AlbianLoggerLevel.Warn,e,
                                    "set field %s.%s = %s is fail.the field type is not ref.",
                                    attr.getId(), fAttr.getName(), fAttr.getValue());
                            ++curr_fail_times;
                        }
                        continue;
                    }

                    String value = fAttr.getValue();
                    Object realObject = null;
                    int indexof = value.indexOf(".");
                    if (-1 == indexof) { // real ref service
                        realObject = AlbianServiceRouter.getSingletonService(
                                IAlbianService.class, value, false);
                        if (!fAttr.getAllowNull() && null == realObject) {
                            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                                    IAlbianLoggerService2.InnerThreadName,
                                    AlbianLoggerLevel.Warn,
                                    "not found ref service ->%s to set field -> %s in service -> %s. ",
                                    value, fAttr.getName(), attr.getId());
                            ++curr_fail_times;
                            continue;
                        }

                        if (null != realObject) {
                            try {
                                fAttr.getField().set(service, realObject);
                                fAttr.setReady(true);
                            }catch (Exception e) {
                                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                                        IAlbianLoggerService2.InnerThreadName,
                                        AlbianLoggerLevel.Warn,e,
                                        "set field %s.%s = %s is fail.the field type is ref.",
                                        attr.getId(), fAttr.getName(), fAttr.getValue());
                                ++curr_fail_times;
                            }
                        }
                        continue;
                    }

                    String refServiceId = value.substring(0, indexof);
                    String exp = value.substring(indexof + 1);
                    IAlbianService refService = AlbianServiceRouter.getSingletonService(
                            IAlbianService.class, refServiceId, false);

                    if (!fAttr.getAllowNull() && null == refService) {
                        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                                IAlbianLoggerService2.InnerThreadName,
                                AlbianLoggerLevel.Warn,
                                " %s.%s = %s.%s is fail. not found ref service -> %s exp -> %s. ",
                                attr.getId(), fAttr.getName(), refServiceId, exp, refServiceId, exp);
                        ++curr_fail_times;
                        continue;
                    }

                    if (null != refService) {
                        IAlbianServiceAttribute sAttr = attrMap.get(refServiceId);
                        Object refRealObj = sAttr.getServiceClass().cast(refService);//must get service full type sign
                        try {
                            realObject = Ognl.getValue(exp, refRealObj);// get read value from full-sgin ref service
                        }catch (Exception e){
                            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                                    IAlbianLoggerService2.InnerThreadName,
                                    AlbianLoggerLevel.Warn,e,
                                    " %s.%s = %s.%s is fail. not found exp -> %s in ref service -> %s. ",
                                    attr.getId(), fAttr.getName(), refServiceId, exp, exp,refServiceId);
                            ++curr_fail_times;
                            continue;
                        }
                        if (null == realObject && !fAttr.getAllowNull()) {
                            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                                    IAlbianLoggerService2.InnerThreadName,
                                    AlbianLoggerLevel.Warn,
                                    " %s.%s = %s.%s is fail. not found ref service -> %s exp -> %s. ",
                                    attr.getId(), fAttr.getName(), refServiceId, exp, refServiceId, exp);
                            ++curr_fail_times;
                            continue;
                        }
                        if (null != realObject) {
                            try {
                                fAttr.getField().set(service, realObject);
                                fAttr.setReady(true);
                            }catch (Exception e) {
                                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                                        IAlbianLoggerService2.InnerThreadName,
                                        AlbianLoggerLevel.Warn,e,
                                        " %s.%s = %s.%s is fail. ",
                                        attr.getId(), fAttr.getName(), refServiceId, exp);
                                ++curr_fail_times;
                                continue;
                            }
                        }
                    }
                }
            }

            if( 0 == curr_fail_times) {
                break;
            }
            if(curr_fail_times == last_fail_times){//across ref in the service

                return false;
            }
            last_fail_times = curr_fail_times;
            curr_fail_times = 0;
        }
        return true;
    }


    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#requestHandlerContext()
     */
    @Override
    public String requestHandlerContext() {
        if (AlbianState.Running != state) {
            return "Albian is not ready,Please wait a minute or contact administrators!";
        }
        return "";
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#unload()
     */
    @Override
    public void unload() throws Exception {
        Set<String> keys = ServiceContainer.getAllServiceNames();
        for (String key : keys) {
            try {
                IAlbianService service = ServiceContainer.getService(key);
                service.beforeUnload();
                service.unload();
                service.afterUnload();
            } catch (Exception e) {
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                        IAlbianLoggerService2.InnerThreadName,
                        AlbianLoggerLevel.Error,e,"unload the service is fail.");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.IAlbianBootService#makeEnvironment()
     */
    @Override
    public void makeEnvironment() {
        String system = System.getProperty("os.name");
        if (system.toLowerCase().contains("windows"))// start with '/'
        {
            KernelSetting.setSystem(KernelSetting.Windows);
        } else {
            KernelSetting.setSystem(KernelSetting.Linux);
        }
    }

}
