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
package org.albianj.cached.impl.lcached;

import java.util.Collection;
import java.util.Map;

import org.albianj.cached.lcached.ICacheable;

/**
 * Utility class for determining the sizes in bytes of commonly used objects.
 * Classes implementing the Cacheable interface should use this class to
 * determine their size.
 * 
 * @author Matt Tucker
 */
public class CacheSizes {

	/**
	 * Returns the size in bytes of a basic Object. This method should only be
	 * used for actual Object objects and not classes that extend Object.
	 * 
	 * @return the size of an Object.
	 */
	public static int sizeOfObject() {
		return 4;
	}

	/**
	 * Returns the size in bytes of a String.
	 * 
	 * @param string
	 *            the String to determine the size of.
	 * @return the size of a String.
	 */
	public static int sizeOfString(String string) {
		if (string == null) {
			return 0;
		}
		return 4 + string.length() * 2;
	}

	/**
	 * Returns the size in bytes of a primitive int.
	 * 
	 * @return the size of a primitive int.
	 */
	public static int sizeOfInt() {
		return 4;
	}

	/**
	 * Returns the size in bytes of a primitive char.
	 * 
	 * @return the size of a primitive char.
	 */
	public static int sizeOfChar() {
		return 2;
	}

	/**
	 * Returns the size in bytes of a primitive boolean.
	 * 
	 * @return the size of a primitive boolean.
	 */
	public static int sizeOfBoolean() {
		return 1;
	}

	/**
	 * Returns the size in bytes of a primitive long.
	 * 
	 * @return the size of a primitive long.
	 */
	public static int sizeOfLong() {
		return 8;
	}

	/**
	 * Returns the size in bytes of a primitive double.
	 * 
	 * @return the size of a primitive double.
	 */
	public static int sizeOfDouble() {
		return 8;
	}

	/**
	 * Returns the size in bytes of a Date.
	 * 
	 * @return the size of a Date.
	 */
	public static int sizeOfDate() {
		return 12;
	}

	/**
	 * Returns the size in bytes of a Map object. All keys and values <b>must be
	 * Strings</b>.
	 * 
	 * @param map
	 *            the Map object to determine the size of.
	 * @return the size of the Map object.
	 */
	public static int sizeOfMap(@SuppressWarnings("rawtypes") Map map) {
		if (map == null) {
			return 0;
		}
		// Base map object -- should be something around this size.
		int size = 36;
		// Add in size of each value
		Object[] values = map.values().toArray();
		for (int i = 0; i < values.length; i++) {
			size += sizeOfString((String) values[i]);
		}
		Object[] keys = map.keySet().toArray();
		// Add in each key
		for (int i = 0; i < keys.length; i++) {
			size += sizeOfString((String) keys[i]);
		}
		return size;
	}

	/**
	 * Returns the size in bytes of a Collection object. Elements are assumed to
	 * be <tt>String</tt>s, <tt>Long</tt>s or <tt>Cacheable</tt> objects.
	 * 
	 * @param list
	 *            the Collection object to determine the size of.
	 * @return the size of the Collection object.
	 */
	public static int sizeOfCollection(
			@SuppressWarnings("rawtypes") Collection list) {
		if (list == null) {
			return 0;
		}
		// Base list object (approximate)
		int size = 36;
		// Add in size of each value
		Object[] values = list.toArray();
		for (int i = 0; i < values.length; i++) {
			Object obj = values[i];
			if (obj instanceof String) {
				size += sizeOfString((String) obj);
			} else if (obj instanceof Long) {
				size += sizeOfLong() + sizeOfObject();
			} else {
				size += ((ICacheable) obj).getCachedSize();
			}
		}
		return size;
	}
}

// Read more:
// http://kickjava.com/src/org/jivesoftware/util/CacheSizes.java.htm#ixzz1WZCREfqk
