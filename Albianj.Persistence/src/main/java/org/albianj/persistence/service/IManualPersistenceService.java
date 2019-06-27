package org.albianj.persistence.service;

import org.albianj.persistence.context.IManualCommand;
import org.albianj.service.IService;

import java.util.List;

/**
 * 手动执行sql
 * sq语句：delete from table-name where id = #id#,
 * 注意：手动sql的时候，albianj只支持单数据库，只会管理单数据库链接和事务；
 * 不会管理分布式事务、分库分表也会失效，所以这些都需要自己管理。
 */
public interface IManualPersistenceService extends IService {

    /**
     * 此service在service.xml中的id
     */
    String Name = "AlbianManualPersistenceService";


    int execute(String sessionId, String storageName, String dbName, IManualCommand cmd);

    int execute(String sessionId, String storageName, IManualCommand cmd);

    List<Integer> execute(String sessionId, String storageName, String dbName, List<IManualCommand> cmds);

    List<Integer> execute(String sessionId, String storageName, List<IManualCommand> cmds);
}
