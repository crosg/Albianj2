package org.albianj.mvc;

import org.albianj.mvc.config.AlbianHttpConfigurtion;
import org.albianj.mvc.config.ViewConfigurtion;
import org.albianj.verify.Validate;

import java.util.Map;

/**
 * Created by xuhaifeng on 16/12/19.
 */
public class ActionResult {


    /**
     * 不执行任何的操作，继续执行代码，后续会读取template，然后输出到client
     * 仅供普通请求使用，不可用于ajax请求
     */
    public static final int Normal = 0;

    /**
     * 重定向，result必须是一个string
     * 仅供普通请求使用，ajax请求不可以使用
     */
    public static final int Redirect = 1;

    /**
     * 该值只有在ajax的时候才可以使用
     * 返回值是一个json的string或者是一个未序列化的object
     * 如果result是string，直接输出；如果是object，将会自动序列化
     */
    public static final int Json = 2;

    /**
     * 将返回结果输出到outstream，适用于二进制输出
     * 输出mimetype将有用户自己设置，write也有用户自己完成。
     * 设置这个值，svlt将会立即停止执行，直接返回
     * result值将会被忽略
     */
    public static final int OutputStream = 3;

    /**
     * 执行action的时候，action内部发生了错误。
     * 该值将会判断result是否为string，如果为string，将会把result看成是一个url，将会执行重定向
     * 如果result为null，则直接默认重定向到error页面
     * 如果ajax的时候发生了错误，则不可以使用该信息，而使用Json直接输出。
     */
    public static final int InnerError = 4;


    private int type = Normal;
    private Object rc = null;

    /**
     *  默认action返回值
     *  type为0，将会继续执行svlt流程
     */
    public static final  ActionResult Default = new ActionResult();

    public ActionResult(int resultType,Object result){
        this.type = resultType;
        this.rc = result;
    }

    public ActionResult(int resultType){
        this.type = resultType;
    }


    public ActionResult(){
    }

    public int getResultType(){
        return this.type;
    }

    public Object getResult(){
        return this.rc;
    }

    public static ActionResult value(int resultType,Object result){
        return new ActionResult(resultType,result);
    }

    public static ActionResult redirect(String url){
        return value(ActionResult.Redirect,url);
    }

    public static ActionResult redirect(HttpContext ctx,Class<? extends  View> cla, String... paras){
        AlbianHttpConfigurtion c = ctx.getHttpConfigurtion();
        Map<String, ViewConfigurtion> map  = c.getPages();
        if(!Validate.isNullOrEmpty(map)){
            throw new  RuntimeException("not found the view.");
        }

        ViewConfigurtion vc = map.get(cla.getName());
        if(null == vc){
            throw new  RuntimeException("not found the view.");
        }

        String template = vc.getTemplate();
        StringBuffer sb = new StringBuffer();
        if(null != paras){
            for(String s : paras){
                sb.append(s).append("&");
            }
        }
        if(0 != sb.length()){
            sb.insert(0,'?');
            sb.insert(0,template);
        } else {
            sb.append(template);
        }

        return redirect(sb.toString());
    }

}
