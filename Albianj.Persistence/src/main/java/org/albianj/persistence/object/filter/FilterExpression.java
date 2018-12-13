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
package org.albianj.persistence.object.filter;

import org.albianj.persistence.object.LogicalOperation;
import org.albianj.persistence.object.RelationalOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * 链式表达式过滤项
 *
 * @author seapeak
 * @since v2.1
 */
public class FilterExpression implements IFilterExpression {

    private RelationalOperator _ro = RelationalOperator.Normal;
    private int _style = IChainExpression.STYLE_FILTER;

    private String _fieldName = null;
    private String _aliasName = null;
    private LogicalOperation _lo = LogicalOperation.Equal;
    private Object _value = null;
    private Class<?> _cls = null;
    private boolean _isAddition = false;
    private boolean _isIdentical = false;

    private List<IChainExpression> _chains = new ArrayList<>();

    public FilterExpression() {
        // TODO Auto-generated constructor stub
    }

    public FilterExpression(String fieldName, String aliasName, LogicalOperation lo, Object value) {
        this._fieldName = fieldName;
        this._aliasName = aliasName;
        this._lo = lo;
        this._value = value;
        this._chains.add(this);
    }

    public FilterExpression(String fieldName, LogicalOperation lo, Object value) {
        this._fieldName = fieldName;
        this._lo = lo;
        this._value = value;
        this._chains.add(this);
    }


    @Override
    public RelationalOperator getRelationalOperator() {
        // TODO Auto-generated method stub
        return this._ro;
    }

    @Override
    public void setRelationalOperator(RelationalOperator relationalOperator) {
        // TODO Auto-generated method stub
        this._ro = relationalOperator;
    }

    @Override
    public int getStyle() {
        // TODO Auto-generated method stub
        return this._style;
    }

    @Override
    public void setStyle(int style) {
        // TODO Auto-generated method stub
        this._style = style;
    }

    @Override
    public IFilterExpression and(IFilterExpression fe) {
        fe.setRelationalOperator(RelationalOperator.And);
        _chains.add(fe);
        return this;
        // TODO Auto-generated method stub
    }

    @Override
    public IFilterExpression or(IFilterExpression fe) {
        // TODO Auto-generated method stub
        fe.setRelationalOperator(RelationalOperator.OR);
        _chains.add(fe);
        return this;
    }

    public IFilterExpression addAddition(IFilterExpression fe) {
        // TODO Auto-generated method stub
        fe.setRelationalOperator(RelationalOperator.Normal);
        fe.setAddition(true);
        _chains.add(fe);
        return this;
    }


    @Override
    public IFilterExpression and(String fieldName, LogicalOperation lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression ce = new FilterExpression(fieldName, lo, value);
        this.and(ce);
        return this;
    }

    @Override
    public IFilterExpression and(String fieldName, String aliasName, LogicalOperation lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression ce = new FilterExpression(fieldName, aliasName, lo, value);
        this.and(ce);
        return this;
    }

    @Override
    public IFilterExpression or(String fieldName, LogicalOperation lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression ce = new FilterExpression(fieldName, lo, value);
        this.or(ce);
        return this;
    }

    @Override
    public IFilterExpression or(String fieldName, String aliasName, LogicalOperation lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression ce = new FilterExpression(fieldName, aliasName, lo, value);
        this.or(ce);
        return this;
    }

    @Override
    public IFilterExpression addAddition(String fieldName, LogicalOperation lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression ce = new FilterExpression(fieldName, lo, value);
        this.addAddition(ce);
        return this;
    }

    @Override
    public IFilterExpression addAddition(String fieldName, String aliasName, LogicalOperation lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression ce = new FilterExpression(fieldName, aliasName, lo, value);
        this.addAddition(ce);
        return this;
    }

    @Override
    public IFilterExpression and(IFilterGroupExpression fge) {
        // TODO Auto-generated method stub
        fge.setStyle(STYLE_FILTER_GROUP);
        fge.setRelationalOperator(RelationalOperator.And);
        _chains.add(fge);
        return this;
    }

    @Override
    public IChainExpression addIdenticalExpression() {
        IFilterExpression fe = new FilterExpression();
        fe.setIdentical(true);
        this.add(fe);
        return this;
    }

    @Override
    public IFilterExpression or(IFilterGroupExpression fge) {
        // TODO Auto-generated method stub
        fge.setStyle(STYLE_FILTER_GROUP);
        fge.setRelationalOperator(RelationalOperator.OR);
        _chains.add(fge);
        return this;
    }

    @Override
    public IFilterExpression add(String fieldName, LogicalOperation lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression fe = new FilterExpression(fieldName, lo, value);
        this.add(fe);
        return this;
    }

    @Override
    public IFilterExpression add(String fieldName, String aliasName, LogicalOperation lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression fe = new FilterExpression(fieldName, aliasName, lo, value);
        this.add(fe);
        return this;
    }

    public IFilterExpression add(IFilterExpression fe) {
        fe.setRelationalOperator(RelationalOperator.Normal);
        // TODO Auto-generated method stub
        this._chains.add(fe);
        return this;
    }


    @Override
    public String getFieldName() {
        // TODO Auto-generated method stub
        return this._fieldName;
    }

    @Override
    public void setFieldName(String fieldName) {
        // TODO Auto-generated method stub
        this._fieldName = fieldName;
    }

    @Override
    public Class<?> getFieldClass() {
        // TODO Auto-generated method stub
        return this._cls;
    }

    @Override
    public void setFieldClass(Class<?> cls) {
        // TODO Auto-generated method stub
        this._cls = cls;
    }

    @Override
    public LogicalOperation getLogicalOperation() {
        // TODO Auto-generated method stub
        return this._lo;
    }

    @Override
    public void setLogicalOperation(LogicalOperation logicalOperation) {
        // TODO Auto-generated method stub
        this._lo = logicalOperation;
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return this._value;
    }

    @Override
    public void setValue(Object value) {
        // TODO Auto-generated method stub
        this._value = value;
    }

    @Override
    public boolean isAddition() {
        // TODO Auto-generated method stub
        return this._isAddition;
    }

    @Override
    public void setAddition(boolean isAddition) {
        // TODO Auto-generated method stub
        this._isAddition = isAddition;
    }

    @Override
    public String getAliasName() {
        // TODO Auto-generated method stub
        return this._aliasName;
    }

    @Override
    public void setAliasName(String an) {
        // TODO Auto-generated method stub
        this._aliasName = an;
    }

    public List<IChainExpression> getChainExpression() {
        return this._chains;
    }

    public boolean isIdentical() {
        return this._isIdentical;
    }

    public void setIdentical(boolean identical) {
        this._isIdentical = identical;
    }

}
