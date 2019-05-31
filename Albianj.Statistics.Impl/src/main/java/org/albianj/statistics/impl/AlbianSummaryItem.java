package org.albianj.statistics.impl;

import org.albianj.datetime.AlbianDateTime;
import org.albianj.datetime.AlbianDateTimeHelper;
import org.albianj.text.StringHelper;

import java.math.BigInteger;
import java.util.Date;

public class AlbianSummaryItem {
    private long max = 0;
    private long min = 0;
    private long count = 0;
    private BigInteger sum;
    private long maxTimestamp = 0;
    private long minTimestamp = 0;
    private long beginClock;
    private String tagName;

    public AlbianSummaryItem(String tagName){
        sum = new BigInteger("0");
        this.beginClock = System.currentTimeMillis();
        this.tagName = tagName;
    }

    public void add(long ts,long figure){
        if(figure >  max) {
            this.max = figure;
            this.maxTimestamp = ts;
        }
        if(figure < max){
            this.min = figure;
            this.minTimestamp = ts;
        }
        ++count;
        sum.add(BigInteger.valueOf(figure));
    }

    public long getBeginClock(){
        return this.beginClock;
    }

    public String makeBuffer(){
        return StringHelper.join("BeginTime -> ", AlbianDateTime.getDateTimeString(new Date(this.beginClock),AlbianDateTime.CHINESE_FORMAT),
                ",MaxTime -> ",AlbianDateTime.getDateTimeString(new Date(this.maxTimestamp),AlbianDateTime.CHINESE_FORMAT)," Max -> ",max,
                ",MinTime -> ",AlbianDateTime.getDateTimeString(new Date(this.minTimestamp),AlbianDateTime.CHINESE_FORMAT)," Min -> ", min,
                ",Count -> ",count,",Sum -> ",sum,",Average -> ",0 == count ?  0 :  + (sum.longValue() / count));
    }
}
