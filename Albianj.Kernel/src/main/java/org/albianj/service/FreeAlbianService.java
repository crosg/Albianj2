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
package org.albianj.service;

import org.albianj.aop.AlbianAopAttribute;
import org.albianj.comment.Comments;
import org.albianj.framework.boot.ApplicationContext;
import org.albianj.framework.boot.BundleContext;
import org.albianj.framework.boot.servants.FileServant;
import org.albianj.io.Path;
import org.albianj.kernel.AlbianKernel;
import org.albianj.kernel.KernelSetting;
import org.albianj.service.parser.AlbianParserException;

import java.io.File;

/**
 * ???????????????????????????????????????????????????????????????service???????
 * ?????????????????????????????????????????????????????????????????????????????
 * ?????????????????????????????????????????????????????????????????????????????
 * ?????????????????????????????????????
 *
 * @author Seapeak
 */
@AlbianKernel
public abstract class FreeAlbianService implements IAlbianService {

    boolean enableProxy = false;
    IAlbianService service = null;
    private AlbianServiceLifetime state = AlbianServiceLifetime.Normal;
    private String bundleName;

    @AlbianAopAttribute(avoid = true)
    public AlbianServiceLifetime getAlbianServiceState() {
        // TODO Auto-generated method stub
        return this.state;
    }

    @AlbianAopAttribute(avoid = true)
    public void beforeLoad() throws AlbianServiceException {
        // TODO Auto-generated method stub
        this.state = AlbianServiceLifetime.BeforeLoading;
    }

    @AlbianAopAttribute(avoid = true)
    public void loading() throws AlbianServiceException, AlbianParserException {
        // TODO Auto-generated method stub
        this.state = AlbianServiceLifetime.Loading;
    }

    @AlbianAopAttribute(avoid = true)
    public void afterLoading() throws AlbianServiceException {
        // TODO Auto-generated method stub
        this.state = AlbianServiceLifetime.Running;
    }

    @AlbianAopAttribute(avoid = true)
    public void beforeUnload() throws AlbianServiceException {
        // TODO Auto-generated method stub
        this.state = AlbianServiceLifetime.BeforeUnloading;

    }

    @AlbianAopAttribute(avoid = true)
    public void unload() throws AlbianServiceException {
        // TODO Auto-generated method stub
        this.state = AlbianServiceLifetime.Unloading;
    }

    @AlbianAopAttribute(avoid = true)
    public void afterUnload() throws AlbianServiceException {
        // TODO Auto-generated method stub
        this.state = AlbianServiceLifetime.Unloaded;
    }

    @AlbianAopAttribute(avoid = true)
    public void init() throws AlbianParserException {
        // TODO Auto-generated method stub

    }

    @AlbianAopAttribute(avoid = true)
    public boolean enableProxy() {
        return enableProxy;
    }

    @AlbianAopAttribute(avoid = true)
    public IAlbianService getRealService() {
        return null == service ? this : service;
    }

    @AlbianAopAttribute(avoid = true)
    public void setRealService(IAlbianService service) {
        if (null != service) {
            this.service = service;
            enableProxy = true;
        } else {
            enableProxy = false;
        }
        return;
    }

    @Override
    @AlbianAopAttribute(avoid = true)
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    @AlbianAopAttribute(avoid = true)
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    @AlbianAopAttribute(avoid = true)
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    @AlbianAopAttribute(avoid = true)
    public String toString() {
        return super.toString();
    }

    @Override
    @AlbianAopAttribute(avoid = true)
    protected void finalize() throws Throwable {
        super.finalize();
    }


    /**
     * 判断配置文件是否存在，如果存在就返回配置文件路径
     * 优先根据配置文件目录判断，如果不存在，直接使用filename，就认为filename就是文件地址
     *
     * @param filename
     * @return
     */
    @AlbianAopAttribute(avoid = true)
    protected String findConfigFile(String filename) {
        try {
            String fname = null;
            BundleContext bctx = ApplicationContext.Instance.findCurrentBundleContext(this.getClass(),false);
            if(null != bctx) {
                fname = bctx.findConfigFile(filename);
                if(FileServant.Instance.isFileOrPathExist(fname)) {
                    return fname;
                }
            }

            File f = new File(filename);
            if (f.exists()) return filename;
            fname = Path.getExtendResourcePath(KernelSetting.getAlbianConfigFilePath() + filename);
            f = new File(fname);
            if (f.exists()) return fname;
            throw new RuntimeException("not found the filename.");
        } catch (Exception e) {
            AlbianServiceRouter.getLogger().error("not found the config filename:%s", filename);
            throw new RuntimeException("not found the filename.");
        }
    }

    @AlbianAopAttribute(avoid = true)
    @Deprecated
    protected String confirmConfigFile(String filename) {
        return findConfigFile(filename);
    }
    @AlbianAopAttribute(avoid = true)
    public String getServiceName() {
        return this.getClass().getSimpleName();
    }

    private String id = null;
    @Comments("设置当前service的名字，service的子类必须实现该方法，并且值必须和service.xml中配置的Id一致。默认为当前类的名称。")
    @AlbianAopAttribute(avoid = true)
    public void setServiceId(String id){
        this.id = id;
    }
    @Comments("设置当前service的名字，service的子类必须实现该方法，并且值必须和service.xml中配置的Id一致。默认为当前类的名称。")
    @AlbianAopAttribute(avoid = true)
    public String getServiceId(){
        return  this.id;
    }

    private IAlbianServiceAttribute attr;
    @AlbianAopAttribute(avoid = true)
    public void setServiceAttribute(IAlbianServiceAttribute attr){
        this.attr = attr;
    }
    @AlbianAopAttribute(avoid = true)
    public IAlbianServiceAttribute getServiceAttribute(){
        return this.attr;
    }

    public String getCurrentBundleName(){
        return this.bundleName;
    }

    @Override
    public void setBandleName(String bundleName){
        this.bundleName = bundleName;
    }
}
