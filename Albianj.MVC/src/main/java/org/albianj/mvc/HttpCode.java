package org.albianj.mvc;

/**
 * Created by xuhaifeng on 16/12/19.
 */
public class HttpCode {

    /**
     * 200:请求成功
     */
    public static final int  Success = 200;

    /**
     * 202:接受和处理、但处理未完成
     */
    public static final int FailRequest = 202;

    /**
     * 203:返回信息不确定或不完整
     */
    public static final int MissingRequest = 203;

    /**
     * 204:请求收到，但返回信息为空
     */
    public static final int ReturnEmpty = 204;


    /**
     * 400——错误请求，如语法错误
     */
    public static final int ErrorRequest = 400;

    /**
     * 401——请求授权失败
     */
    public static final int IllegalAuthorization = 401;
    /**
     * 403——请求不允许
     */
    public static final int IllegalRequest = 403;


    /**
     * 404——没有发现文件、查询或URl
     */
    public static final int NotFound = 404;

    /**
     * 413——请求的资源大于服务器允许的大小
     */
    public static final int OverSize = 413;


    /**
     * 500——服务器产生内部错误
     */
    public static final int InnerError = 500;
    /**
     * 501——服务器不支持请求的函数
     */
    public static final int IllegalAction = 501;
}
