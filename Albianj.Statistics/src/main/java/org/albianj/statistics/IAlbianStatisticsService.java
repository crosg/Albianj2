package org.albianj.statistics;

import org.albianj.boot.tags.Comments;
import org.albianj.service.IAlbianService;

public interface IAlbianStatisticsService extends IAlbianService {

    String Name = "AlbianStatisticsService";

    @Comments("默认的统计的时间戳")
    long UnitMs = 300000;

    /**
     * 加入监控
     * @param tagName 监控的名称
     * @param now 当前的时间
     * @param figure 执行的时间
     */
    public void add(String tagName,long now,long figure) ;

}
