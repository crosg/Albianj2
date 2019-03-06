package org.albianj.logger.monitor;

import org.albianj.net.AlbianHost;

import java.util.Date;

public class AlbianMonitorData {

    private int tasktime;
    private String desIp;
    private int desPort;
    private String level;
    private int status;
    private String appName;
    private String sessionId;
    private String bizId;
    private String bizName;
    private String bizExtend;
    private String detail;

    public Date getCreatetime() {
        return new Date();
    }

    public int getTasktime() {
        return tasktime;
    }

    public AlbianMonitorData setTasktime(int tasktime) {
        this.tasktime = tasktime;return this;
    }

    public String getSourceIp() {
        return AlbianHost.getLocalIP();
    }


    public String getDesIp() {
        return desIp;
    }

    public AlbianMonitorData setDesIp(String desIp) {
        this.desIp = desIp;return this;
    }

    public int getDesPort() {
        return desPort;
    }

    public AlbianMonitorData setDesPort(int desPort) {
        this.desPort = desPort;return this;
    }

    public String getLevel() {
        return level;
    }

    public AlbianMonitorData setLevel(String level) {
        this.level = level;return this;
    }

    public int getStatus() {
        return status;
    }

    public AlbianMonitorData setStatus(int status) {
        this.status = status;return this;
    }

    public String getAppName() {
        return appName;
    }

    public AlbianMonitorData setAppName(String appName) {
        this.appName = appName;return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public AlbianMonitorData setSessionId(String sessionId) {
        this.sessionId = sessionId;return this;
    }

    public String getBizId() {
        return bizId;
    }

    public AlbianMonitorData setBizId(String bizId) {
        this.bizId = bizId;return this;
    }

    public String getBizName() {
        return bizName;
    }

    public AlbianMonitorData setBizName(String bizName) {
        this.bizName = bizName;return this;
    }

    public String getBizExtend() {
        return bizExtend;
    }

    public AlbianMonitorData setBizExtend(String bizExtend) {
        this.bizExtend = bizExtend;return this;
    }

    public String getDetail() {
        return detail;
    }

    public AlbianMonitorData setDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public static AlbianMonitorData build(){
        return new AlbianMonitorData();
    }


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
