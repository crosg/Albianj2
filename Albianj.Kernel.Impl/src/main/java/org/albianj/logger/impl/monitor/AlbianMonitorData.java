package org.albianj.logger.impl.monitor;

import org.albianj.logger.monitor.IAlbianMonitorData;

import java.util.Date;

public class AlbianMonitorData implements IAlbianMonitorData {

    private Date createtime;
    private int tasktime;
    private String sourceIp;
    private int sourcePort;
    private String desIp;
    private int desPort;
    private String level;
    private int status;
    private String appName;
    private String sessionId;
    private String bizId;
    private String bizName;
    private String bizExtend;
    private String exceptionName;
    private String detail;
    private String stacks;




//    createtime  date   (时间)
//    tasktime   int  (ms)      (消耗时间)
//    sourceip   keyword      (源IP)
//    sourceport keyword    (源port)
//    desip      keyword        (调用ip)
//    desport    keyword      (调用port)
//    level      keyword         (warn，degug，info，error。。。)
//    status     keyword         (200,400..)
//    appname    keyword    (发布项名？)
//    sessionid  keyword
//    bizid      keyword (CBID,authorid...    业务填写)
//    bizname    keyword (CBID,authorid... 业务填写)
//    bizextend  keyword  (业务填写)
//    exceptionname keyword  （业务填写）
//
//    不可条件查询  只可展示：
////    detail    业务填写     （业务填写）
//    stacks     堆栈信息

}
