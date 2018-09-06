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
package org.albianj.restful.impl.object;

import org.albianj.restful.object.AlbianRestfulResultStyle;
import org.albianj.restful.object.IAlbianRestfulActionContext;
import org.albianj.restful.object.IAlbianRestfulResult;
import org.albianj.restful.object.IAlbianRestfulResultV1;
import org.albianj.restful.service.IAlbianRestfulBodyFilterDelegate;
import org.albianj.restful.service.IAlbianRestfulResultParser;
import org.albianj.restful.service.IAlbianRestfulResultParserV1;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class AlbianRestfulActionContext implements IAlbianRestfulActionContext {
    HttpServletRequest req = null;
    HttpServletResponse resp = null;
    ServletContext sc = null;
    String sessionId = null;
    String serviceName = null;
    String actionName = null;
    String sp = null;
    Map<String, String> paras = null;
    String body = null;
    IAlbianRestfulResultParser parser = null;
    IAlbianRestfulResultParserV1 parserV1 = null;

    AlbianRestfulResultStyle style = AlbianRestfulResultStyle.Json;
    IAlbianRestfulResult rc = null;
    IAlbianRestfulBodyFilterDelegate handler = null;
    private boolean _showNull = false;
    IAlbianRestfulResultV1 rcV1 = null;
    private  int version = 0;

    public AlbianRestfulActionContext(HttpServletRequest req, HttpServletResponse resp, ServletContext sc,
                                      String serviceName, String actionName, String sessionId, String sp, Map<String, String> paras,
                                      String body) {
        super();
        this.req = req;
        this.resp = resp;
        this.sc = sc;
        this.serviceName = serviceName;
        this.actionName = actionName;
        this.sessionId = sessionId;
        this.sp = sp;
        this.paras = paras;
        this.body = body;
    }

    @Override
    public HttpServletRequest getCurrentRequest() {
        // TODO Auto-generated method stub
        return this.req;
    }

    @Override
    public HttpServletResponse getCurrentResponse() {
        // TODO Auto-generated method stub
        return this.resp;
    }

    @Override
    public ServletContext getCurrentServletContext() {
        // TODO Auto-generated method stub
        return this.sc;
    }

    @Override
    public String getCurrentSessionId() {
        // TODO Auto-generated method stub
        return this.sessionId;
    }

    @Override
    public String getCurrentServiceName() {
        // TODO Auto-generated method stub
        return this.serviceName;
    }

    @Override
    public String getCurrentActionName() {
        // TODO Auto-generated method stub
        return this.actionName;
    }

    @Override
    public String getCurrentSP() {
        // TODO Auto-generated method stub
        return this.sp;
    }

    @Override
    public Map<String, String> getCurrentParameters() {
        // TODO Auto-generated method stub
        return this.paras;
    }

    @Override
    public String getCurrentRequestBody() {
        // TODO Auto-generated method stub
        return this.body;
    }

    @Override
    public AlbianRestfulResultStyle getResultStyle() {
        // TODO Auto-generated method stub
        return this.style;
    }

    @Override
    public void setResultStyle(AlbianRestfulResultStyle style) {
        // TODO Auto-generated method stub
        this.style = style;
    }

    @Override
    public IAlbianRestfulResult getResult() {
        // TODO Auto-generated method stub
        return this.rc;
    }

    @Override
    public void setResult(IAlbianRestfulResult rc) {
        // TODO Auto-generated method stub
        this.rc = rc;
    }

    @Override
    public void setResult(AlbianRestfulResultStyle style, IAlbianRestfulResultParser parser, IAlbianRestfulResult rc) {
        // TODO Auto-generated method stub
        this.style = style;
        this.rc = rc;
        this.parser = parser;
    }

    @Override
    public IAlbianRestfulResultParser getParser() {
        // TODO Auto-generated method stub
        return this.parser;
    }

    @Override
    public void setParser(IAlbianRestfulResultParser parser) {
        // TODO Auto-generated method stub
        this.parser = parser;
    }

    public IAlbianRestfulResultV1 getResultV1(){
        return this.rcV1;
    }

    public void setResultV1(IAlbianRestfulResultV1 rc){
        this.rcV1 = rc;
        this.version = 1;
    }

    public void setResultV1(AlbianRestfulResultStyle style,
                            IAlbianRestfulResultParserV1 parser, IAlbianRestfulResultV1 rc){
        this.style = style;
        this.rcV1 = rc;
        this.parserV1 = parser;
        this.version = 1;
    }

    public int getVerison(){
        return this.version;
    }

    public IAlbianRestfulResultParserV1 getParserV1(){
        return this.parserV1;
    }



    public boolean getShowNull() {
        return this._showNull;
    }

    public void setShowNull(boolean showNull) {
        _showNull = showNull;
    }

    public IAlbianRestfulBodyFilterDelegate getBodyFilterHandler() {
        return this.handler;
    }

    public void setBodyFilterHandler(IAlbianRestfulBodyFilterDelegate handler) {
        this.handler = handler;
    }
}
