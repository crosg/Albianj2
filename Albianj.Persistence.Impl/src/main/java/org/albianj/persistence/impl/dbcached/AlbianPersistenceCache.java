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
package org.albianj.persistence.impl.dbcached;

import org.albianj.cached.service.IAlbianCachedService;
import org.albianj.concurrent.IAlbianThreadPoolService;
import org.albianj.persistence.impl.context.ChainExpressionParser;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IFilterCondition;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.persistence.service.IAlbianMappingParserService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

import java.util.LinkedList;
import java.util.List;

//import net.rubyeye.xmemcached.MemcachedClient;

public class AlbianPersistenceCache {
    public static String buildKey(Class<?> cls, int start, int step, IChainExpression f,
                                  LinkedList<IOrderByCondition> orderbys) {
        StringBuilder sb = new StringBuilder();
        sb.append(cls.getName()).append("_").append(start).append("_").append(step).append("_");
        List<IFilterCondition> wheres = new LinkedList<>();
        ChainExpressionParser.toFilterConditionArray(f, wheres);
        if (null != wheres) {
            for (IFilterCondition where : wheres) {
                sb.append(where.getRelationalOperator()).append("_")
                        .append(Validate.isNullOrEmptyOrAllSpace(where.getAliasName()) ? where.getFieldName()
                                : where.getAliasName())
                        .append("_")
                        .append(where.getLogicalOperation()).append("_").append(where.getValue());
            }
        }
        if (null != orderbys) {
            for (IOrderByCondition orderby : orderbys) {
                sb.append(orderby.getFieldName()).append("_").append(orderby.getSortStyle()).append("_");
            }
        }
        return sb.toString();
    }

    public static <T extends IAlbianObject> void setObjects(Class<T> cls, int start, int step,
                                                            IChainExpression f, LinkedList<IOrderByCondition> orderbys, List<T> objs) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return;
        IAlbianThreadPoolService tps = AlbianServiceRouter.getSingletonService(IAlbianThreadPoolService.class,
                IAlbianThreadPoolService.Name, false);
        if (null == tps)
            return;

        String key = buildKey(cls, start, step, f, orderbys);
        int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
        String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
                ? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
        AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, objs, tto);
        tps.execute(apct);

    }

    public static <T extends IAlbianObject> void setObjects(Class<T> cls, String key, List<T> newObj) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return;
        IAlbianThreadPoolService tps = AlbianServiceRouter.getSingletonService(IAlbianThreadPoolService.class,
                IAlbianThreadPoolService.Name, false);
        if (null == tps)
            return;
        int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
        String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
                ? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
        AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, newObj, tto);
        tps.execute(apct);

    }

    public static <T extends IAlbianObject> void setObject(Class<T> cls, IChainExpression f,
                                                           LinkedList<IOrderByCondition> orderbys, IAlbianObject obj) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return;
        IAlbianThreadPoolService tps = AlbianServiceRouter.getSingletonService(IAlbianThreadPoolService.class,
                IAlbianThreadPoolService.Name, false);
        if (null == tps)
            return;
        String key = buildKey(cls, 0, 0, f, orderbys);
        int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
        String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
                ? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
        AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, obj, tto);
        tps.execute(apct);
    }

    public static <T extends IAlbianObject> void setPagesize(Class<T> cls, IChainExpression f,
                                                             LinkedList<IOrderByCondition> orderbys, long pagesize) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return;
        IAlbianThreadPoolService tps = AlbianServiceRouter.getSingletonService(IAlbianThreadPoolService.class,
                IAlbianThreadPoolService.Name, false);
        if (null == tps)
            return;
        String key = buildKey(cls, -1, -1, f, orderbys);
        int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
        String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
                ? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
        AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, pagesize, tto);
        tps.execute(apct);
    }

    public static <T extends IAlbianObject> void setObject(Class<T> cls, String key, IAlbianObject obj) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return;
        IAlbianThreadPoolService tps = AlbianServiceRouter.getSingletonService(IAlbianThreadPoolService.class,
                IAlbianThreadPoolService.Name, false);
        if (null == tps)
            return;
        int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
        String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
                ? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
        AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, obj, tto);
        tps.execute(apct);
    }

    public static <T extends IAlbianObject> T findObject(Class<T> cls, IChainExpression f,
                                                         LinkedList<IOrderByCondition> orderbys) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return null;
        String key = buildKey(cls, 0, 0, f, orderbys);
        try {
            IAlbianCachedService acs = AlbianServiceRouter.getSingletonService(IAlbianCachedService.class,
                    IAlbianCachedService.Name, false);

            if (null != acs) {
                T obj = acs.get(aoa.getCache().getName(), key, cls);
                if (null != obj) {
                    return obj;
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static <T extends IAlbianObject> T findObject(Class<T> cls, String key) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return null;
        try {
            IAlbianCachedService acs = AlbianServiceRouter.getSingletonService(IAlbianCachedService.class,
                    IAlbianCachedService.Name, false);
            if (null != acs) {
                T obj = acs.get(aoa.getCache().getName(), key, cls);
                if (null != obj) {
                    return obj;
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static <T extends IAlbianObject> List<T> findObjects(Class<T> cls, int start, int step,
                                                                IChainExpression f, LinkedList<IOrderByCondition> orderbys) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return null;
        String key = buildKey(cls, start, step, f, orderbys);
        try {
            IAlbianCachedService acs = AlbianServiceRouter.getSingletonService(IAlbianCachedService.class,
                    IAlbianCachedService.Name, false);
            if (null != acs) {
                List<T> obj = acs.getArray(aoa.getCache().getName(), key, cls);
                if (null != obj) {
                    return obj;
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static <T extends IAlbianObject> List<T> findObjects(Class<T> cls, String key) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return null;
        try {
            IAlbianCachedService acs = AlbianServiceRouter.getSingletonService(IAlbianCachedService.class,
                    IAlbianCachedService.Name, false);
            if (null != acs) {
                List<T> obj = acs.getArray(aoa.getCache().getName(), key, cls);
                if (null != obj) {
                    return obj;
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static <T extends IAlbianObject> long findPagesize(Class<T> cls, IChainExpression f,
                                                              LinkedList<IOrderByCondition> orderbys) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return -1;
        String key = buildKey(cls, -1, -1, f, orderbys);
        try {
            IAlbianCachedService acs = AlbianServiceRouter.getSingletonService(IAlbianCachedService.class,
                    IAlbianCachedService.Name, false);
            if (null != acs) {
                Long num = acs.get(aoa.getCache().getName(), key, Long.class);
                if (null == num) {
                    return -1;
                }
                return num;
            }
        } catch (Exception e) {

        }
        return -1;
    }

    public static <T extends IAlbianObject> long findPagesize(Class<T> cls, String key) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return -1;
        try {
            IAlbianCachedService acs = AlbianServiceRouter.getSingletonService(IAlbianCachedService.class,
                    IAlbianCachedService.Name, false);
            if (null != acs) {
                Long num = acs.get(aoa.getCache().getName(), key, Long.class);
                if (null == num) {
                    return -1;
                }
                return num;
            }
        } catch (Exception e) {

        }
        return -1;
    }


    @Deprecated
    public static String buildKey(Class<?> cls, int start, int step, LinkedList<IFilterCondition> wheres,
                                  LinkedList<IOrderByCondition> orderbys) {
        StringBuilder sb = new StringBuilder();
        sb.append(cls.getName()).append("_").append(start).append("_").append(step).append("_");
        if (null != wheres) {
            for (IFilterCondition where : wheres) {
                sb.append(where.getRelationalOperator()).append("_")
                        .append(Validate.isNullOrEmptyOrAllSpace(where.getAliasName()) ? where.getFieldName()
                                : where.getAliasName())
                        .append("_")
                        .append(where.getLogicalOperation()).append("_").append(where.getValue());
            }
        }
        if (null != orderbys) {
            for (IOrderByCondition orderby : orderbys) {
                sb.append(orderby.getFieldName()).append("_").append(orderby.getSortStyle()).append("_");
            }
        }
        return sb.toString();
    }

    @Deprecated
    public static <T extends IAlbianObject> void setObjects(Class<T> cls, int start, int step,
                                                            LinkedList<IFilterCondition> wheres, LinkedList<IOrderByCondition> orderbys, List<T> objs) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return;
        IAlbianThreadPoolService tps = AlbianServiceRouter.getSingletonService(IAlbianThreadPoolService.class,
                IAlbianThreadPoolService.Name, false);
        if (null == tps)
            return;
        String key = buildKey(cls, start, step, wheres, orderbys);
        int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
        String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
                ? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
        AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, objs, tto);
        tps.execute(apct);

    }

    @Deprecated
    public static <T extends IAlbianObject> void setObject(Class<T> cls, LinkedList<IFilterCondition> wheres,
                                                           LinkedList<IOrderByCondition> orderbys, IAlbianObject obj) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return;
        IAlbianThreadPoolService tps = AlbianServiceRouter.getSingletonService(IAlbianThreadPoolService.class,
                IAlbianThreadPoolService.Name, false);
        if (null == tps)
            return;
        String key = buildKey(cls, 0, 0, wheres, orderbys);
        int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
        String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
                ? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
        AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, obj, tto);
        tps.execute(apct);
    }

    @Deprecated
    public static <T extends IAlbianObject> void setPagesize(Class<T> cls, LinkedList<IFilterCondition> wheres,
                                                             LinkedList<IOrderByCondition> orderbys, long pagesize) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return;
        IAlbianThreadPoolService tps = AlbianServiceRouter.getSingletonService(IAlbianThreadPoolService.class,
                IAlbianThreadPoolService.Name, false);
        if (null == tps)
            return;
        String key = buildKey(cls, -1, -1, wheres, orderbys);
        int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
        String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
                ? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
        AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, pagesize, tto);
        tps.execute(apct);
    }

    @Deprecated
    public static <T extends IAlbianObject> T findObject(Class<T> cls, LinkedList<IFilterCondition> wheres,
                                                         LinkedList<IOrderByCondition> orderbys) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return null;
        String key = buildKey(cls, 0, 0, wheres, orderbys);
        try {
            IAlbianCachedService acs = AlbianServiceRouter.getSingletonService(IAlbianCachedService.class,
                    IAlbianCachedService.Name, false);

            if (null != acs) {
                T obj = acs.get(aoa.getCache().getName(), key, cls);
                if (null != obj) {
                    return obj;
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    @Deprecated
    public static <T extends IAlbianObject> List<T> findObjects(Class<T> cls, int start, int step,
                                                                LinkedList<IFilterCondition> wheres, LinkedList<IOrderByCondition> orderbys) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return null;
        String key = buildKey(cls, start, step, wheres, orderbys);
        try {
            IAlbianCachedService acs = AlbianServiceRouter.getSingletonService(IAlbianCachedService.class,
                    IAlbianCachedService.Name, false);
            if (null != acs) {
                List<T> obj = acs.getArray(aoa.getCache().getName(), key, cls);
                if (null != obj) {
                    return obj;
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    @Deprecated
    public static <T extends IAlbianObject> long findPagesize(Class<T> cls, LinkedList<IFilterCondition> wheres,
                                                              LinkedList<IOrderByCondition> orderbys) {
        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
        if (!aoa.getCache().getEnable())
            return -1;
        String key = buildKey(cls, -1, -1, wheres, orderbys);
        try {
            IAlbianCachedService acs = AlbianServiceRouter.getSingletonService(IAlbianCachedService.class,
                    IAlbianCachedService.Name, false);
            if (null != acs) {
                Long num = acs.get(aoa.getCache().getName(), key, Long.class);
                if (null == num) {
                    return -1;
                }
                return num;
            }
        } catch (Exception e) {

        }
        return -1;
    }


}
