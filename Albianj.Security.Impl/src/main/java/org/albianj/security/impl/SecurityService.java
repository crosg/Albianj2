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
package org.albianj.security.impl;

import org.albianj.kernel.KernelSetting;
import org.albianj.security.ISecurityService;
import org.albianj.security.MACStyle;
import org.albianj.security.StyleMapping;
import org.albianj.service.ServiceTag;
import org.albianj.service.FreeService;
import org.albianj.text.StringHelper;
import org.albianj.verify.Validate;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

@ServiceTag(Id = ISecurityService.Name, Interface = ISecurityService.class)
public class SecurityService extends FreeService implements ISecurityService {

    //donot try in my system,we not use this key
    private String DEFAULT_SHA_KEY = "oskey:sdfgrgeyt*&43543dfgsdfgs6454";
    private String DEFAULT_MD5_KEY = "oskey:!t#==-;'sdfd3432dfgdgs43242#!";
    private String DEFAULT_DES_KEY = "oskey:$fdge5rt7903=dfgdgr;.,'ergfegn$";

    public String getServiceName() {
        return Name;
    }

    @Override
    public void init() throws org.albianj.service.parser.AlbianParserException {
        super.loadConf();
        String mkey = KernelSetting.getMachineKey();
        if (Validate.isNullOrEmptyOrAllSpace(mkey)) {
            return;
        }
        if (40 <= mkey.length()) {
            DEFAULT_SHA_KEY = mkey.substring(3, 40);
        }
        if (80 <= mkey.length()) {
            DEFAULT_MD5_KEY = mkey.substring(50, 79);
        }
        if (60 <= mkey.length()) {
            DEFAULT_DES_KEY = mkey.substring(30, 58);
        }
    }


    public String decryptDES(String message) throws RuntimeException {
        return decryptDES(DEFAULT_DES_KEY, message);
    }

    public String decryptDES(String key, String message) throws RuntimeException {
        String k = StringHelper.padLeft(key, 8);
        byte[] bytesrc = decryptBASE64(message);
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(k.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(k.getBytes("UTF-8"));

            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            byte[] retByte = cipher.doFinal(bytesrc);
            return new String(retByte);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String encryptDES(String message) throws RuntimeException {
        return encryptDES(DEFAULT_DES_KEY, message);
    }

    public String encryptDES(String key, String message) throws RuntimeException {
        String k = StringHelper.padLeft(key, 8);
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            DESKeySpec desKeySpec = new DESKeySpec(k.getBytes("UTF-8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec iv = new IvParameterSpec(k.getBytes("UTF-8"));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            return encryptBASE64(cipher.doFinal(message.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public byte[] decryptBASE64(String key) throws RuntimeException {
        return Base64.decodeBase64(key);
    }

    public String encryptBASE64(byte[] key) throws RuntimeException {
        return Base64.encodeBase64String(key);
    }

    public String encryptMD5(String data) throws RuntimeException {
        return encryptHMAC(DEFAULT_MD5_KEY, MACStyle.MD5, data);
    }

    public String encryptSHA(String data) throws RuntimeException {
        return encryptHMAC(DEFAULT_SHA_KEY, MACStyle.SHA1, data);
    }

    public String initMacKey() throws RuntimeException {
        return initMacKey(MACStyle.MD5);
    }

    public String initMacKey(MACStyle style) throws RuntimeException {
        return initMacKey(StyleMapping.toMACStyleString(style));
    }

    protected String initMacKey(String key) throws RuntimeException {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(key);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        SecretKey secretKey = keyGenerator.generateKey();
        return encryptBASE64(secretKey.getEncoded());
    }

    public String encryptHMAC(String key, MACStyle style, byte[] data)
            throws RuntimeException {
        try {
            SecretKey secretKey = new SecretKeySpec(decryptBASE64(key),
                    StyleMapping.toMACStyleString(style));
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            return encryptBASE64(mac.doFinal(data));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String encryptHMAC(String key, MACStyle style, String data)
            throws RuntimeException {
        try {
            SecretKey secretKey = new SecretKeySpec(decryptBASE64(key),
                    StyleMapping.toMACStyleString(style));
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            return encryptBASE64(mac.doFinal(decryptBASE64(data)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String encryptHMAC(String key, byte[] data) throws RuntimeException {
        return encryptHMAC(key, MACStyle.MD5, data);
    }

    public String encryptHMAC(String key, String data) throws RuntimeException {
        return encryptHMAC(key, MACStyle.MD5, data);
    }


}
