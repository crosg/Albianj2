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

import java.sql.Types;

public class Convert {
	public static int toSqlType(Class<?> cls) {
		String type = cls.getSimpleName().toLowerCase();
		if ("string".equalsIgnoreCase(type)) {
			return Types.VARCHAR;
		} else if ("bigdecimal".equalsIgnoreCase(type)) {
			return Types.NUMERIC;
		} else if ("boolean".equalsIgnoreCase(type)) {
			return Types.BIT;
		} else if ("integer".equalsIgnoreCase(type)
				|| "int".equalsIgnoreCase(type)) {
			return Types.INTEGER;
		} else if ("long".equalsIgnoreCase(type)) {
			return Types.BIGINT;
		} else if ("biginteger".equalsIgnoreCase(type)) {
			return Types.BIGINT;
		} else if ("float".equalsIgnoreCase(type)) {
			return Types.FLOAT;
		} else if ("double".equalsIgnoreCase(type)) {
			return Types.DOUBLE;
		} else if ("date".equalsIgnoreCase(type)) {
			return Types.DATE;
		} else if ("time".equalsIgnoreCase(type)) {
			return Types.TIME;
		} else if ("timestamp".equalsIgnoreCase(type)) {
			return Types.TIMESTAMP;
		} else if ("clob".equalsIgnoreCase(type)) {
			return Types.CLOB;
		} else if ("blob".equalsIgnoreCase(type)) {
			return Types.BLOB;
		} else if ("array".equalsIgnoreCase(type)) {
			return Types.ARRAY;
		} else {
			return Types.VARCHAR;
		}
	}

	public static int toSqlType(String typeSimpleName) {
		String typeName = typeSimpleName.toLowerCase();
		if ("char".equalsIgnoreCase(typeName)) {
			return Types.CHAR;
		} else if ("varchar".equalsIgnoreCase(typeName)) {
			return Types.VARCHAR;
		} else if ("longvarchar".equalsIgnoreCase(typeName)) {
			return Types.LONGVARCHAR;
		} else if ("numeric".equalsIgnoreCase(typeName)) {
			return Types.NUMERIC;
		} else if ("decimal".equalsIgnoreCase(typeName)) {
			return Types.DECIMAL;
		} else if ("bit".equalsIgnoreCase(typeName)) {
			return Types.BIT;
		} else if ("tinyint".equalsIgnoreCase(typeName)) {
			return Types.TINYINT;
		} else if ("smallint".equalsIgnoreCase(typeName)) {
			return Types.SMALLINT;
		} else if ("integer".equalsIgnoreCase(typeName)) {
			return Types.INTEGER;
		} else if ("bigint".equalsIgnoreCase(typeName)) {
			return Types.BIGINT;
		} else if ("real".equalsIgnoreCase(typeName)) {
			return Types.REAL;
		} else if ("float".equalsIgnoreCase(typeName)) {
			return Types.FLOAT;
		} else if ("double".equalsIgnoreCase(typeName)) {
			return Types.DOUBLE;
		} else if ("binary".equalsIgnoreCase(typeName)) {
			return Types.BINARY;
		} else if ("varbinary".equalsIgnoreCase(typeName)) {
			return Types.VARBINARY;
		} else if ("longvarbinary".equalsIgnoreCase(typeName)) {
			return Types.LONGVARBINARY;
		} else if ("date".equalsIgnoreCase(typeName)) {
			return Types.DATE;
		} else if ("time".equalsIgnoreCase(typeName)) {
			return Types.TIME;
		} else if ("timestamp".equalsIgnoreCase(typeName)) {
			return Types.TIMESTAMP;
		} else if ("clob".equalsIgnoreCase(typeName)) {
			return Types.CLOB;
		} else if ("blob".equalsIgnoreCase(typeName)) {
			return Types.BLOB;
		} else if ("array".equalsIgnoreCase(typeName)) {
			return Types.ARRAY;
		} else {
			return Types.VARCHAR;
		}
	}
}
