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
package org.albianj.restful.impl.util;

import org.albianj.kernel.IAlbianLogicIdService;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.restful.impl.object.AlbianRestfulActionContext;
import org.albianj.restful.object.IAlbianRestfulActionContext;
import org.albianj.service.AlbianServiceRouter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author Sean
 * <p>
 * restful 参数获取
 */

public class AlbianRestfulUtils {
    public static String getLimitedData(HttpServletRequest req, int len) throws Exception {
//        StringBuffer info = new java.lang.StringBuffer();
        InputStream in = null;
        try {
            in = req.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            AlbianServiceRouter.getLogger().error(
                    IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
        }
        BufferedInputStream buf = new BufferedInputStream(in);
//        byte[] buffer = new byte[1024];
//        int iRead;
        int total = req.getContentLength();
        byte[] buffer = new byte[total];
        int offset = 0;
        try {
            int rc = 0;
            while (-1 != (rc = buf.read(buffer, offset, total - offset))) {
                offset += rc;
                if (offset == total) break;
            }


//            while ((iRead = buf.read(buffer)) != -1) {
//                info.append(new String(buffer, 0, iRead, "UTF-8"));
//                if (info.length() > len) {
//                    throw new Exception("the length of inputStream is out of limit length:" + len);
//                }
//            }
        } catch (UnsupportedEncodingException e) {
            AlbianServiceRouter.getLogger().error(
                    IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
        } catch (IOException e) {
            AlbianServiceRouter.getLogger().error(
                    IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
        } finally {
            buf.close();
            in.close();
        }

        try {
            return new String(buffer, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            AlbianServiceRouter.getLogger().error(
                    IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
        }

        return null;
    }

    public IAlbianRestfulActionContext AlbianRestfulActionContext(HttpServletRequest req,
                                                                  HttpServletResponse resp) {
        Map<String, String> param_pairs = splitQuery(req.getQueryString());
        IAlbianLogicIdService lids = AlbianServiceRouter.getLogIdService();
        return new AlbianRestfulActionContext(req, resp,
                req.getServletContext(), param_pairs.get("service"),
                param_pairs.get("action"), lids.makeStringUNID("session"),//req.getSession(true).getId(),内存持续增长
                param_pairs.get("sp"), param_pairs, getData(req));
    }

    public IAlbianRestfulActionContext AlbianRestfulActionContext_Safe(HttpServletRequest req,
                                                                       HttpServletResponse resp) {
        Map<String, String> param_pairs = splitQuery(req.getQueryString());
        IAlbianLogicIdService lids = AlbianServiceRouter.getLogIdService();
        return new AlbianRestfulActionContext(req, resp,
                req.getServletContext(), param_pairs.get("service"),
                param_pairs.get("action"), lids.makeStringUNID("session"),//req.getSession(true).getId(),内存持续增长
                param_pairs.get("sp"), param_pairs, null);
    }

    public Map<String, String> splitQuery(String qstring) {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        // String query = url.getQuery();
        String[] pairs = qstring.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            try {
                query_pairs.put(
                        URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                        URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                AlbianServiceRouter.getLogger().error(
                        IAlbianLoggerService.AlbianRunningLoggerName, e,
                        "splitQuery UnsupportedEncodingException");
            }
        }
        return query_pairs;
    }

    public String getData(HttpServletRequest req) {
        StringBuffer info = new StringBuffer();
        InputStream in = null;
        try {
            in = req.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            AlbianServiceRouter.getLogger().error(
                    IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
        }
        BufferedInputStream buf = new BufferedInputStream(in);

//        int total = req.getContentLength();
//        byte[] buffer = new byte[total];
//        int offset = 0;
//        try {
//            int rc = 0;
//            while(-1 != ( rc = buf.read(buffer,offset,total-offset))) {
//                offset += rc;
//                if(offset == total) break;
//            }
//
//        } catch (UnsupportedEncodingException e) {
//            AlbianServiceRouter.getLogger().error(
//                    IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
//        } catch (IOException e) {
//            AlbianServiceRouter.getLogger().error(
//                    IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
//        }
//
//        try {
//            return new String(buffer, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            AlbianServiceRouter.getLogger().error(
//                    IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
//        }

//        return null;

//        ArrayList<Byte> bytes = new ArrayList<>();
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int iRead = 0, count = 0;
        try {
            while ((iRead = buf.read(buffer)) != -1) {
                baos.write(buffer, 0, iRead);
                count += iRead;
            }

            byte[] bytes = baos.toByteArray();
            return new String(bytes, 0, count, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            AlbianServiceRouter.getLogger().error(
                    IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
        } catch (IOException e) {
            AlbianServiceRouter.getLogger().error(
                    IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
        }
        return null;


//        bytes.add()


//            byte[] buffer = new byte[1024];
//        int iRead;
//        try {
//            while ((iRead = buf.read(buffer)) != -1) {
//                info.append(new String(buffer, 0, iRead, "UTF-8"));
//            }
//       } catch (UnsupportedEncodingException e) {
//             AlbianServiceRouter.getLogger().error(
//                    IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
//        } catch (IOException e) {
//            AlbianServiceRouter.getLogger().error(
//                    IAlbianLoggerService.AlbianRunningLoggerName, e, "getData");
//        }

//        return info.toString();
    }
}
