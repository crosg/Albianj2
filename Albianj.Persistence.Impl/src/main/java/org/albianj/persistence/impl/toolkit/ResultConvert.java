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
package org.albianj.persistence.impl.toolkit;

import org.albianj.datetime.AlbianDateTime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
//import java.util.Date;

public class ResultConvert {

    @SuppressWarnings("deprecation")
    public static Object toBoxValue(Class<?> cls, Object o) throws Exception {
        if (String.class.isAssignableFrom(cls)) {
            return o.toString();
        } else if (BigDecimal.class.isAssignableFrom(cls)) {
            BigDecimal bd = new BigDecimal(o.toString());
            return bd;
        } else if (boolean.class.isAssignableFrom(cls) ||
                Boolean.class.isAssignableFrom(cls)) {
            return Boolean.parseBoolean(o.toString());
        } else if (Integer.class.isAssignableFrom(cls)
                || int.class.isAssignableFrom(cls)) {
            return Integer.parseInt(o.toString());
        } else if (long.class.isAssignableFrom(cls) ||
                Long.class.isAssignableFrom(cls)) {
            return Long.parseLong(o.toString());
        } else if (BigInteger.class.isAssignableFrom(cls)) {
            BigInteger bi = new BigInteger(o.toString());
            return bi;
        } else if (float.class.isAssignableFrom(cls) ||
                Float.class.isAssignableFrom(cls)) {
            return Float.parseFloat(o.toString());
        } else if (double.class.isAssignableFrom(cls) ||
                Double.class.isAssignableFrom(cls)) {
            return Double.parseDouble(o.toString());
        } else if (Time.class.isAssignableFrom(cls)) {
            return Time.parse(o.toString());
        } else if (Timestamp.class.isAssignableFrom(cls)) { //
            /*
                timestamp must do before date
                use isAssignableFrom function must notice class extends relation
             */
            return o;// donot ask me why,if i parser ,it will be crash
        } else if (java.util.Date.class.isAssignableFrom(cls)) {
            java.util.Date d = null;
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        AlbianDateTime.CHINESE_FORMAT);
                d = dateFormat.parse(o.toString());
            } catch (Exception e) {
                d = null;
            }
            if (null == d) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            AlbianDateTime.CHINESE_SIMPLE_FORMAT);
                    d = dateFormat.parse(o.toString());
                } catch (Exception e) {
                    throw e;
                }
            }
            if (java.sql.Date.class.isAssignableFrom(cls)) {
                return new java.sql.Date(d.getTime());
            }
            return d;
        } else {
            return o;
        }
    }

    public static String sqlValueToString(int sqlType, Object v) {
        if (null == v) return "";

        switch (sqlType) {
            case Types.DATE: {
                Date d = (Date) v;
                return AlbianDateTime.getDateTimeString(d);
            }
            case Types.TIME: {
                Time t = (Time) v;
                return t.toString();
            }
            case Types.TIMESTAMP: {
                Timestamp ts = (Timestamp) v;
                return ts.toString();
            }
            default: {
                return v.toString();
            }
        }
    }
}
