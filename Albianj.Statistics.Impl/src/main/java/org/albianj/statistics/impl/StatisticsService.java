package org.albianj.statistics.impl;

import org.albianj.service.ServiceTag;
import org.albianj.service.FreeService;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.statistics.IStatisticsService;

import java.util.concurrent.ConcurrentLinkedQueue;

@ServiceTag(Id = IStatisticsService.Name, Interface = IStatisticsService.class)
public class StatisticsService extends FreeService implements IStatisticsService {

    private AlbianSummary data;
    private ConcurrentLinkedQueue<AlbianAddtionPkg> queue = null;

    @Override
    public void init() throws AlbianParserException {
        data = new AlbianSummary();
        queue = new ConcurrentLinkedQueue<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                           for(int i = 0; i != 1000; ++i ){ // 每秒大概处理2w个监控数据item
                               if(queue.isEmpty()) {
                                   break;
                                }
                               AlbianAddtionPkg pkg =  queue.poll();
                               data.add(pkg.getTagName(),pkg.getTimestamp(),pkg.getFigure());
                           }

                    }catch (Exception e){

                    }finally {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
        super.loadConf();
    }

    public void add(String tagName,long now,long figure) {
        try {
            AlbianAddtionPkg pkg = new AlbianAddtionPkg(tagName, now, figure);
            queue.add(pkg);
        }catch (Exception e){

        }
    }

}
