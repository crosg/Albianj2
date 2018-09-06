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

import java.util.List;

/**
 * 链式查询表达式的接口定义
 * <br/>
 * 在albianj中，链式的查询表达式主要表示sql语句中where部分的语句。
 * 对于albianj而言，where语句表达式项之间逻辑关系只支持and和or，表达式项的逻辑关系支持<,<=,=,>=,>，暂时不支持in，not in等操作
 * <br/>
 * 链式表达式的表达式支持两种类型：过滤项表达式：IFilterExpression；表达式组：IFilterGroupExpression
 * 过滤项表达式值具体的表达式，比如id = 001，而表达式组表示sql语句中由（）括起来的表达式项组，比如（id > 10 and id< 100)
 * </br>
 * 注意：IChainExpression 只要是为了约束过滤项表达式和表达式组的行为和定义，一般编程具体会使用其子类：IFilterExpression和IFilterGroupExpression
 * <pre>
 * 		示例：使用链式表达式组合一个复杂的sql语句where部分
 * <br/>
 * 		sql语句where部分： id = id and name = name or ((age > 10 and age < 80) and  (sex > 10 or sex = sex)) or unit = unit
 * <code>
 * 			IChainExpression fe = new FilterExpression();
 * fe.add("id", LogicalOperation.Equal,id);
 * fe.and("name", LogicalOperation.Equal, "name");
 * IFilterGroupExpression fge = new FilterGroupExpression();
 * IFilterGroupExpression fge_child = new FilterGroupExpression();
 *
 * fge_child.add("age", LogicalOperation.Greater, 10);
 * fge_child.and("age", "age1", LogicalOperation.Less, 80);
 * IFilterGroupExpression fge_child2 = new FilterGroupExpression();
 * fge_child2.add("sex","sex1", LogicalOperation.Greater, 10);
 * fge_child2.or("sex", LogicalOperation.Equal, "sex");
 *
 * fge.addFilterGroup(fge_child);
 * fge.and(fge_child2);
 * fe.or(fge);
 * fe.or("unit", LogicalOperation.Equal, "unit");
 * </code>
 * </pre>
 *
 * @author seapeak
 * @since v2.1
 */
public interface IChainExpression {
    /**
     * 当前的表达式项为过滤表达式
     */
    static int STYLE_FILTER = 0;
    /**
     * 当前的表达式项为一个表达式组，等同于sql语句中使用()括起来的内容
     */
    static int STYLE_FILTER_GROUP = 1;

    /**
     * 返回表达式项和前面的兄弟表达式项的逻辑关系。
     * 目前过滤表达式只支持and/or
     * 对于表达式组，支持and/or/normal 当关系为normal时，表示当前表达式项是前一个表达式项的子表达式项
     *
     * @return
     */
    RelationalOperator getRelationalOperator();

    /**
     * 设置表达式项和前面的兄弟表达式项的逻辑关系。
     * 目前过滤表达式只支持and/or
     * 对于表达式组，支持and/or/normal 当关系为normal时，表示当前表达式项是前一个表达式项的子表达式项
     *
     * @param relationalOperator RelationalOperator枚举值
     */
    void setRelationalOperator(RelationalOperator relationalOperator);

    /**
     * 得到当前表达式项的类型。
     * 当前的表达式类型只支持过滤表达式项和表达式组
     * 值：STYLE_FILTER，STYLE_FILTER_GROUP
     *
     * @return STYLE_FILTER或者STYLE_FILTER_GROUP
     */
    int getStyle();

    /**
     * 设置当前表达式项的类型
     * 当前的表达式类型只支持过滤表达式项和表达式组
     * 值：STYLE_FILTER，STYLE_FILTER_GROUP
     *
     * @param style
     */
    void setStyle(int style);

    /**
     * 前一个表达式项and上当前的表达式fe
     *
     * @param fe 当前需要被加入and关系的表达式项
     * @return 表达式项关系链的头对象
     */
    IChainExpression and(IFilterExpression fe);

    /**
     * 前一个表达式项and上当前的表达式
     *
     * @param fieldName 当前表达式的属性名，这个fieldName是编程实体的属性，而不是数据库的值字段名。albianj会自动根据实体属性获取数据库的字段名
     * @param lo        当前表达式的等式关系
     * @param value     当前表达式的值
     * @return 表达式项关系链的头对象
     */
    IChainExpression and(String fieldName, LogicalOperation lo, Object value);

    /**
     * 前一个表达式项and上当前的表达式
     *
     * @param fieldName 当前表达式的属性名，这个fieldName是编程实体的属性，而不是数据库的值字段名。albianj会自动根据实体属性获取数据库的字段名
     * @param aliasName 当前表达式属性名的别名，当条件中对于同一个属性有多个过滤条件，
     *                  那么就必须给除第一个以外的属性一个别名，别名可以任意，唯一的要求是不能重复
     * @param lo        当前表达式的等式关系
     * @param value     当前表达式的值
     * @return 表达式项关系链的头对象
     */
    IChainExpression and(String fieldName, String aliasName, LogicalOperation lo, Object value);

    /**
     * * 前一个表达式项or上当前的表达式fe
     *
     * @param fe 当前需要被加入or关系的表达式项
     * @return 表达式项关系链的头对象
     */
    IChainExpression or(IFilterExpression fe);

    /**
     * 前一个表达式项or上当前的表达式
     *
     * @param fieldName 当前表达式的属性名，这个fieldName是编程实体的属性，而不是数据库的值字段名。albianj会自动根据实体属性获取数据库的字段名
     * @param lo        当前表达式的等式关系
     * @param value     当前表达式的值
     * @return 表达式项关系链的头对象
     */
    IChainExpression or(String fieldName, LogicalOperation lo, Object value);

    /**
     * 前一个表达式项or上当前的表达式
     *
     * @param fieldName 当前表达式的属性名，这个fieldName是编程实体的属性，而不是数据库的值字段名。albianj会自动根据实体属性获取数据库的字段名
     * @param aliasName 当前表达式属性名的别名，当条件中对于同一个属性有多个过滤条件，
     *                  那么就必须给除第一个以外的属性一个别名，别名可以任意，唯一的要求是不能重复
     * @param lo        当前表达式的等式关系
     * @param value     当前表达式的值
     * @return 表达式项关系链的头对象
     */
    IChainExpression or(String fieldName, String aliasName, LogicalOperation lo, Object value);

    /**
     * 加上做为数据路由的依据的条件，但是当前的表达式项不会加入到sql语句的where条件中
     *
     * @param 当前需要被加入or关系的表达式项
     * @return 表达式项关系链的头对象
     */
    public IChainExpression addAddition(IFilterExpression fe);

    /**
     * 加上做为数据路由的依据的条件，但是当前的表达式项不会加入到sql语句的where条件中
     *
     * @param fieldName 当前表达式的属性名，这个fieldName是编程实体的属性，而不是数据库的值字段名。albianj会自动根据实体属性获取数据库的字段名
     * @param lo        当前表达式的等式关系
     * @param value     当前表达式的值
     * @return 表达式项关系链的头对象
     */
    IChainExpression addAddition(String fieldName, LogicalOperation lo, Object value);

    /**
     * 加上做为数据路由的依据的条件，但是当前的表达式项不会加入到sql语句的where条件中
     *
     * @param aliasName 当前表达式属性名的别名，当条件中对于同一个属性有多个过滤条件，
     *                  那么就必须给除第一个以外的属性一个别名，别名可以任意，唯一的要求是不能重复
     * @param lo        当前表达式的等式关系
     * @param value     当前表达式的值
     * @return 表达式项关系链的头对象
     * @ fieldName 当前表达式的属性名，这个fieldName是编程实体的属性，而不是数据库的值字段名。albianj会自动根据实体属性获取数据库的字段名
     */
    IChainExpression addAddition(String fieldName, String aliasName, LogicalOperation lo, Object value);

    /**
     * 加上当前的过滤表达式项。该表达式项会被加入到sql语句的where条件中
     * <br/>
     * 此方法一般使用在IFilterGroup连接第一个表达式项的时候
     *
     * @param fe 当前表达式项
     * @return 表达式项关系链的头对象
     */
    IChainExpression add(IFilterExpression fe);

    /**
     * 加上当前的过滤表达式项。该表达式项会被加入到sql语句的where条件中
     * <br/>
     * 此方法一般使用在IFilterGroup连接第一个表达式项的时候
     *
     * @param fieldName 当前表达式的属性名，这个fieldName是编程实体的属性，而不是数据库的值字段名。albianj会自动根据实体属性获取数据库的字段名
     * @param lo        当前表达式的等式关系
     * @param value     当前表达式的值
     * @return 表达式项关系链的头对象
     */
    IChainExpression add(String fieldName, LogicalOperation lo, Object value);

    /**
     * 加上当前的过滤表达式项。该表达式项会被加入到sql语句的where条件中
     * <br/>
     * 此方法一般使用在IFilterGroup连接第一个表达式项的时候
     *
     * @param aliasName 当前表达式属性名的别名，当条件中对于同一个属性有多个过滤条件，
     *                  那么就必须给除第一个以外的属性一个别名，别名可以任意，唯一的要求是不能重复
     * @param lo        当前表达式的等式关系
     * @param value     当前表达式的值
     * @return 表达式项关系链的头对象
     * @ fieldName 当前表达式的属性名，这个fieldName是编程实体的属性，而不是数据库的值字段名。albianj会自动根据实体属性获取数据库的字段名
     */
    IChainExpression add(String fieldName, String aliasName, LogicalOperation lo, Object value);

    /**
     * and 当前的表达式组项。该表达式组项会被加入到sql语句的where条件中
     *
     * @param fge 当前表达式项
     * @return 表达式项关系链的头对象
     */
    IChainExpression and(IFilterGroupExpression fge);

    IChainExpression addIdenticalExpression();

    /**
     * or 当前的表达式组项。该表达式组项会被加入到sql语句的where条件中
     *
     * @param fge 当前表达式项
     * @return 表达式项关系链的头对象
     */
    IChainExpression or(IFilterGroupExpression fge);

    /**
     * 得到当前表达式项下面的表达式
     *
     * @return
     */
    List<IChainExpression> getChainExpression();
}
