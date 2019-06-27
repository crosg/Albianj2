package org.albianj.statistics.impl;

import org.albianj.service.AlbianServiceRouter;
import org.albianj.statistics.IStatisticsService;

import java.util.HashMap;


public class AlbianSummary {
    private HashMap<String,AlbianSummaryItem> map = null;

    public AlbianSummary(){
        map = new HashMap<>();
    }

    public void add(String tagName,long now,long figure){
        AlbianSummaryItem item;
        try {
            if (!map.containsKey(tagName)) {
                item = new AlbianSummaryItem(tagName);
                item.add(now, figure);
                map.put(tagName, item);
                return;
            }

            item = map.get(tagName);
            if (item.getBeginClock() + IStatisticsService.UnitMs >= now) {
                item.add(now, figure);
                return;
            }

            try {
                String text = item.makeBuffer();
                AlbianServiceRouter.addLogV2("Static", "Static", AlbianServiceRouter.Mark, null, tagName, text);
            }catch (Exception e){

            }finally {
                item = new AlbianSummaryItem(tagName);
                item.add(now, figure);
                map.put(tagName, item);
            }
        }catch (Throwable t){

        }
    }
}
