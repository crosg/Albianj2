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
package org.albianj.unid.service.impl;

import org.albianj.argument.RefArg;
import org.albianj.datetime.AlbianDateTime;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.net.IMsgHeader;
import org.albianj.net.MemoryToIOStream;
import org.albianj.net.MsgHeader;
import org.albianj.service.AlbianServiceRant;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.unid.service.AlbianRemoteUNIDAttributeException;
import org.albianj.unid.service.AlbianRemoteUNIDProtocol;
import org.albianj.unid.service.IAlbianRemoteUNIDAttribute;
import org.albianj.unid.service.IAlbianRemoteUNIDService;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Element;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Seapeak
 */
@AlbianServiceRant(Id = IAlbianRemoteUNIDService.Name,Interface = IAlbianRemoteUNIDService.class)
public class AlbianRemoteUNIDService extends FreeAlbianRemoteUNIDParser
        implements IAlbianRemoteUNIDService {
    private static int _idx = 0;
    int typeDefault = 3;
    private List<IAlbianRemoteUNIDAttribute> _list = null;
    private Object locker = new Object();

    public String getServiceName(){
        return Name;
    }



    @SuppressWarnings("unchecked")
    private static BigInteger createID(IAlbianRemoteUNIDAttribute attr, int type) {
        Socket client = null;
        try {
                client = new Socket(attr.getHost(), attr.getPort());
                client.setSoTimeout(attr.getTimeout());
        } catch (Exception e) {
            AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,
                    e, "create UNID client connect:%s:%d is fail.",
                    attr.getHost(), attr.getPort());
            return null;
        }

        if (null == client) {
            AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianRunningLoggerName,
                    "get UNID client connect:%s:%d from pool is fail.create it.",
                    attr.getHost(), attr.getPort());
                return null;
        }

        OutputStream os = null;
        InputStream is = null;
        try {
            os = client.getOutputStream();
            IMsgHeader outHeader = null;
            outHeader = new MsgHeader(AlbianRemoteUNIDProtocol.Version,
                    AlbianRemoteUNIDProtocol.MakeId, 4, 0, false, 0);

            byte[] outHeaderBuffer = outHeader.pack();
            byte[] outBodyBuffer = MemoryToIOStream.intToNetStream(type);
            os.write(outHeaderBuffer);
            os.write(outBodyBuffer);
            os.flush();

            is = client.getInputStream();
            byte[] inHeaderBuffer = new byte[IMsgHeader.MsgHeaderLength];
            is.read(inHeaderBuffer, 0, IMsgHeader.MsgHeaderLength);
            IMsgHeader inHeader = new MsgHeader().unpack(inHeaderBuffer);
            long inBodylen = inHeader.getBodyLength();
            byte[] inBodyBuffer = new byte[(int) inBodylen];
            is.read(inBodyBuffer, 0, (int) inBodylen);
            BigInteger unid = new BigInteger(inBodyBuffer);
            return unid;

        } catch (Exception e) {
            AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e, "get remote:%s:%d UNID is fail.",
                    attr.getHost(), attr.getPort());
        } finally {
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,
                            e, "close remote:%s:%d output stream is fail.",
                            attr.getHost(), attr.getPort());
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e, "close remote:%s:%d input stream is fail.",
                            attr.getHost(), attr.getPort());
                }
            }

            if (null != client) {
                try {
                        client.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e, "close remote:%s:%d output stream is fail.",
                            attr.getHost(), attr.getPort());
                }
            }
        }
        return null;
    }

    @Override
    public void init() throws AlbianParserException {
        _list = new ArrayList<IAlbianRemoteUNIDAttribute>();
        super.init();
    }

    protected void parserServers(@SuppressWarnings("rawtypes") List nodes)
            throws AlbianRemoteUNIDAttributeException {
        for (Object node : nodes) {
            IAlbianRemoteUNIDAttribute attr = parserServer((Element) node);
            if (null == attr)
                continue;
            _list.add(attr);
        }

    }

    protected IAlbianRemoteUNIDAttribute parserServer(Element node) {
        String host = XmlParser.getAttributeValue(node, "Host");
        IAlbianRemoteUNIDAttribute attr = new AlbianRemoteUNIDAttribute();
        if (Validate.isNullOrEmptyOrAllSpace(host)) {
            AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,
                    "remote server host in the unid.xml is null or empty.xml:%s.", node.asXML());
            return null;
        }
        attr.setHost(host);

        String sPort = XmlParser.getAttributeValue(node, "Port");
        if (Validate.isNullOrEmptyOrAllSpace(sPort)) {
            AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,
                    "remote server port in the unid.xml is null or empty.xml:%s.", node.asXML());
            return null;
        }
        attr.setPort(Integer.parseInt(sPort));

        String stto = XmlParser.getAttributeValue(node, "Timeout");
        if (!Validate.isNullOrEmptyOrAllSpace(stto)) {
            attr.setTimeout(Integer.parseInt(stto));
        }

        String ssize = XmlParser.getAttributeValue(node, "PoolSize");
        if (!Validate.isNullOrEmptyOrAllSpace(ssize)) {
            attr.setPoolSize(Integer.parseInt(ssize));
        }
        return attr;
    }

    public BigInteger createBookId() {
        int idx = 0;
        synchronized (locker) {
            idx = (_idx++) % _list.size();
            if (idx > _list.size()) {
                idx = 0;
                _idx = 0;
            }
        }
        IAlbianRemoteUNIDAttribute attr = _list.get(idx);
        return createID(attr, 0);
    }

    public BigInteger createAuthorId() {
        int idx = 0;
        synchronized (locker) {
            idx = (_idx++) % _list.size();
            if (idx > _list.size()) {
                idx = 0;
                _idx = 0;
            }
        }
        IAlbianRemoteUNIDAttribute attr = _list.get(idx);
        return createID(attr, 1);
    }

    public BigInteger createConfigItemId() {
        int idx = 0;
        synchronized (locker) {
            idx = (_idx++) % _list.size();
            if (idx > _list.size()) {
                idx = 0;
                _idx = 0;
            }
        }
        IAlbianRemoteUNIDAttribute attr = _list.get(idx);
        return createID(attr, 2);
    }

    public BigInteger createUNID() {
        int type = 0x3ff < typeDefault++ ? typeDefault = 3 : typeDefault;
        return createUNID(type);
    }

    public BigInteger createContractId(){
        return createUNID(0x4ff);
    }

    public BigInteger createUNID(int type) {
        int idx = 0;
        synchronized (locker) {
            idx = (_idx++) % _list.size();
            if (idx > _list.size()) {
                idx = 0;
                _idx = 0;
            }
        }
        IAlbianRemoteUNIDAttribute attr = _list.get(idx);
        return createID(attr, type);
    }

    public void unpack(BigInteger bi, RefArg<Timestamp> time,
                       RefArg<Integer> type) {
        if (null == time && null == type)
            return;
        long r32 = bi.shiftRight(32).longValue();
        long l32 = bi.intValue();
        if (null != type) {
            type.setValue((int) (l32 & 0x3ff));
        }
        if (null != time) {
            Date dt = AlbianDateTime.dateAddSeconds(2015, 1, 1, r32);
            @SuppressWarnings("deprecation")
            Timestamp tt = new Timestamp(dt.getYear(), dt.getMonth(),
                    dt.getDate(), dt.getHours(), dt.getMinutes(),
                    dt.getSeconds(), 0);
            time.setValue(tt);
        }

    }

    public void unpack(BigInteger bi, RefArg<Timestamp> time,
                       RefArg<Integer> sed, RefArg<Integer> idx) {
        long r32 = bi.divide(new BigInteger("1000000000")).longValue();
        int next = bi.divide(new BigInteger("100")).intValue();
        int i = bi.modInverse(new BigInteger("100")).intValue();
        if (null != time) {
            Date dt = AlbianDateTime.dateAddSeconds(2015, 1, 1, r32);
            @SuppressWarnings("deprecation")
            Timestamp tt = new Timestamp(dt.getYear(), dt.getMonth(),
                    dt.getDate(), dt.getHours(), dt.getMinutes(),
                    dt.getSeconds(), 0);
            time.setValue(tt);
        }
        if (null != sed)
            sed.setValue(next);
        if (null != idx)
            idx.setValue(i);
    }

    /**
     * 生成一个使用二进制算法的组合的id，改id对于人不是太友好，不能被很好的辨认
     * 但是对算法友好，计算较快
     *
     * @return 二进制算法生成的一个十进制数，uint64类型
     */
    public BigInteger createBinaryId() {
        int idx = 0;
        synchronized (locker) {
            idx = (_idx++) % _list.size();
            if (idx > _list.size()) {
                idx = 0;
                _idx = 0;
            }
        }
        IAlbianRemoteUNIDAttribute attr = _list.get(idx);
        return createID(attr, 3);
    }

    /**
     * 生成一个十进制、完整的id。
     * 这个id最后的4位将会从0-9999依次出现，这种id适合根据最后的4位做hash或者是轮询分库分表
     *
     * @return 十进制生成的id
     */
    public BigInteger createCompleteDigital() {
        int idx = 0;
        synchronized (locker) {
            idx = (_idx++) % _list.size();
            if (idx > _list.size()) {
                idx = 0;
                _idx = 0;
            }
        }
        IAlbianRemoteUNIDAttribute attr = _list.get(idx);
        return createID(attr, 99);
    }

    /**
     * 生成一个十进制，不完整的id
     * 这个id的最后两位是00，永远是00。这种id比较适合根据自己的规则来指定分库分表，
     * 如果要用这个id来做取模或者是轮询，必须排除最后的2位，排除最后的2位后，和createCompleteDigital生成的id一致
     *
     * @return
     */
    public BigInteger createIncompleteDigital() {
        int idx = 0;
        synchronized (locker) {
            idx = (_idx++) % _list.size();
            if (idx > _list.size()) {
                idx = 0;
                _idx = 0;
            }
        }
        IAlbianRemoteUNIDAttribute attr = _list.get(idx);
        return createID(attr, 0);
    }

    /**
     * 生成一个十进制，保证递增并且完整的十进制id
     * 这个id最后的4位将会从0-9999依次出现，但是如果新的1秒开始，这个计数将会从0重新开始。
     * 注意，这个id不是太适合取模或者是hash等分库分表，因为后面的四位数生成的不充分，可能会引起数据存储的数据量不平衡
     *
     * @return
     */
    public BigInteger createIncrAndCompleteDigital() {
        int idx = 0;
        synchronized (locker) {
            idx = (_idx++) % _list.size();
            if (idx > _list.size()) {
                idx = 0;
                _idx = 0;
            }
        }
        IAlbianRemoteUNIDAttribute attr = _list.get(idx);
        return createID(attr, 98);
    }


}
