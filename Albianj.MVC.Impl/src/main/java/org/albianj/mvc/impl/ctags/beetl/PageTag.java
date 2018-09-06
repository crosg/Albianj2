package org.albianj.mvc.impl.ctags.beetl;

import org.albianj.mvc.HttpContext;
import org.albianj.mvc.impl.ctags.PagingInfo;
import org.albianj.verify.Validate;
import org.beetl.core.Context;
import org.beetl.core.GeneralVarTagBinding;
import org.beetl.core.statement.Statement;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by xuhaifeng on 16/12/20.
 */
public class PageTag extends GeneralVarTagBinding {
    private  long recordsCount = 0;
    private  int pageSize = 0;
    private int currentPageNumber;
    private int pageCount = 0;
    private boolean skpTag = false;

    private String baseUrl = null;
    private String query = null;

    public static String PagingParamentName = "pagenumb";

    public static String getName(){
        return "Paging";
    }

    private HttpContext currentContext = null;


    @Override
    public void init(Context ctx, Object[] args, Statement st) {
        super.init(ctx, args, st);
        Object o = getAttributeValue("ctx");
        if(null == o) {
            skpTag = true;
            return;
        }
        currentContext = (HttpContext) o;

        PagingInfo page = (PagingInfo)getAttributeValue("page");
        if(null == page) {
            skpTag = true;
            return ;
        }
        recordsCount = page.getRecordsCount();
        pageSize = page.getPagesize();
        currentPageNumber = 0 == page.getCurrentPageNumber() ? 1 : page.getCurrentPageNumber();
        pageCount = (int)(recordsCount / pageSize);
        if(pageCount * pageSize < recordsCount){
            pageCount++;
        }

        HttpServletRequest request = currentContext.getCurrentRequest();
        baseUrl = request.getContextPath() + request.getServletPath();

        Map<String,String[]> paras = request.getParameterMap();
        StringBuffer sb = new StringBuffer();
        if(!Validate.isNullOrEmpty(paras)){
            for(Map.Entry<String,String[]> entry : paras.entrySet()) {
                if(!entry.getKey().equals(PagingParamentName)){
                    if(null != entry.getValue() || 0 < entry.getValue().length)
                    sb.append(entry.getKey()).append("=").append(entry.getValue()[0]).append("&");
                }
            }
        }

        int len = sb.length();
        if(0 != len){
            query = sb.toString();
        }

    }

    @Override
    public void render(){
        if(skpTag) {
            this.doBodyRender();
            return;
        }
        StringBuilder paging = new StringBuilder("");
        try{

            paging.append("<div class=\"row clearfix\">\n")
                 .append("<div class=\"pull-right\">\n")
                    .append("<ul class=\"pagination \" style=\"margin: 0px -2px;\">\n");
        if(1 <= pageCount) { //第一页，页总数大于等于1就可以有
            paging.append("<li><a href=\"").append(makePagingUrl(1)).append("\"><i class=\"fa fa-fast-backward\" aria-hidden=\"true\"></i>\n</a></li>  &nbsp;");
        }
        if(1 == currentPageNumber) { //当前页为1的时候，往前按钮不能点，因为没页
            paging.append("<li><a><i class=\"fa fa-step-backward\" aria-hidden=\"true\"></i></a></li>  &nbsp;");
        }
        else if(2 <= currentPageNumber) {
            paging.append("<li><a href=\"").append(makePagingUrl(currentPageNumber - 1)).append("\"><i class=\"fa fa-step-backward\" aria-hidden=\"true\"></i></a></li>  &nbsp;");
        }

        paging.append("<li><a>&nbsp;").append(currentPageNumber).append("&nbsp;</a></li>&nbsp;");

        if(pageCount >= currentPageNumber + 1){//当前页+1后还是在总页数范围内，可以有下一页
            paging.append("<li><a href=\"").append(makePagingUrl(currentPageNumber + 1)).append("\"><i class=\"fa fa-step-forward\" aria-hidden=\"true\"></i></a></li>&nbsp;");
        } else {
            paging.append("<li><a><i class=\"fa fa-step-forward\" aria-hidden=\"true\"></i></a></li>&nbsp;");
        }

        if(pageCount > 0){
            paging.append("<li><a href=\"").append(makePagingUrl(pageCount)).append("\"><i class=\"fa fa-fast-forward\" aria-hidden=\"true\"></i></a></li>&nbsp;");
        }

        paging.append("<li> <input id=\"pageing_number\" class=\"ui-pg-input ui-corner-all\" type=\"text\" size=\"2\" maxlength=\"7\" value=\"").append(currentPageNumber).append("\" role=\"textbox\" style=\"width: 40px;height:32px;float: left;\"></li>&nbsp;");
        paging.append("<li><a style=\"cursor:pointer;\" onclick=\"javascript:return __albian_jump('").append(makePagingUrl()).append("');").append("\"><i class=\"fa fa-play\" aria-hidden=\"true\"></i></a></li>");
        paging.append("<li><span>共 ").append(pageCount ).append(" 页 ").append(recordsCount).append(" 条记录</span></li>");
        paging.append("</ul>")
                .append("</div>")
                .append("</div>");

        paging.append("<script language=\"JavaScript\" >")
                .append("/*引用common.js做校验*/")
                .append("function __albian_jump(url) { ")
                .append("var input = _$('pageing_number');")
                .append("if(null != input) { ")
                    .append(" if(isEmpty(input.value)) {")
                        .append(" alert('请输入跳转的页码!'); ")
                        .append(" input.focus();")
                        .append(" return false; }")
                    .append("if(!isInteger(input.value)) {")
                        .append(" alert('页码必须是整数!'); ")
                        .append(" input.focus();")
                        .append(" return false; }")
                    .append("var iNo = input.value - 0;")
                    .append("if( iNo <= 0 || iNo > ").append(pageCount).append(" ) {")
                        .append(" alert('页码必须在1-").append(pageCount).append("之间!'); ")
                        .append(" input.focus();")
                        .append(" return false; }")
                    .append("window.location.href=url + iNo;")
                .append("}")
                .append("return false;")
                .append("}")
                .append("</script>");



            ctx.byteWriter.writeString(paging.toString());

            this.doBodyRender();
        }catch (IOException  e){
            throw new RuntimeException(e);
        }
    }


    private String makePagingUrl(int pageNum) throws UnsupportedEncodingException {
        String url  = new StringBuffer(baseUrl)
                .append("?").append(null == query ? "" : query)
                .append(PagingParamentName).append("=").append(pageNum).toString();
        return url;
    }
    private String makePagingUrl() throws UnsupportedEncodingException {
        String url = new StringBuffer(baseUrl)
                .append("?").append(null == query ? "" : query)
                .append(PagingParamentName).append("=").toString();
        return url;
    }
}
