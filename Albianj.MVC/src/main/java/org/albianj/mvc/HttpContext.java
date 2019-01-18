package org.albianj.mvc;


import org.albianj.mvc.config.AlbianHttpConfigurtion;
import org.albianj.mvc.config.ViewConfigurtion;
import org.albianj.mvc.service.UploadFile;
import org.albianj.text.StringHelper;
import org.albianj.verify.Validate;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;


public class HttpContext {


    public final static String SessionKey = "Albian_User_Session";
    public final static String SessionIdKey = "Albian_User_Session_Id";

    private String serviceName;
    private String actionName = "PageLoad";
    private String templateName;
    private String templatePath;
    private String templateFullName;
    private Map<String, String> paras = new LinkedHashMap<>();
    private AlbianHttpConfigurtion config;
    private HttpServletRequest currentRequest;
    private HttpServletResponse currentResponse;
    private ServletContext currentServlet;
    private boolean isAjaxRequest = false;
    private boolean isMultipartRequest = false;
    private boolean isWelcomeViewRequest = false;
    private HttpSession session;
    private boolean isPost = false;
    private ViewConfigurtion pc;
    private View view;
    private boolean isUseMasterView = false;
    private MView masterView;
    private StringBuffer masterViewHtml;
    private Map<String, UploadFile> fileItems = null;
    private Map<String, String> attributes = null;
    private String currentUrl = null;

//	private AlbianHttpConfigurtion c;


    public HttpContext() {

    }


    public HttpContext(
            boolean isPost,
            HttpServletRequest currentRequest,
            HttpServletResponse currentResponse,
            ServletContext servletContext,
            HttpSession session,
            AlbianHttpConfigurtion c) {
        this.currentRequest = currentRequest;
        this.currentResponse = currentResponse;
        this.currentServlet = servletContext;
        this.config = c;
        this.session = session;
        this.isPost = isPost;
        String baseUrl = currentRequest.getRequestURI();
        if (Validate.isNullOrEmptyOrAllSpace(baseUrl) || baseUrl.endsWith(c.getWelcomePage().getTemplate())) {
            baseUrl = c.getWelcomePage().getTemplate();
            isWelcomeViewRequest = true;
        }


        if (Validate.isNullOrEmptyOrAllSpace(currentRequest.getQueryString())) {
            this.currentUrl = baseUrl;
        } else {
            this.currentUrl = baseUrl + "?" + currentRequest.getQueryString();
        }
    }

    /**
     * @return the c
     */
    public AlbianHttpConfigurtion getHttpConfigurtion() {
        return this.config;
    }

    /**
     * @param c the c to set
     */
    public void setHttpConfigurtion(AlbianHttpConfigurtion c) {
        this.config = c;
    }

    /**
     * @return the currentServlet
     */
    public ServletContext getCurrentServlet() {
        return currentServlet;
    }


    /**
     * @param currentServlet the currentServlet to set
     */
    public void setCurrentServlet(ServletContext currentServlet) {
        this.currentServlet = currentServlet;
    }

    public HttpSession getHttpSession() {
        return this.session;
    }

    public boolean isWelcomeViewRequest(){
        return this.isWelcomeViewRequest;
    }

    /**
     * @return the isMultipartRequest
     */
    public boolean isMultipartRequest() {
        return isMultipartRequest;
    }

    /**
     * @param isMultipartRequest the isMultipartRequest to set
     */
    public void setMultipartRequest(boolean isMultipartRequest) {
        this.isMultipartRequest = isMultipartRequest;
    }

    public String getHttpSessionId() {
        Object sid = this.session.getAttribute(SessionIdKey);
        if (null == sid || StringHelper.isBlank(sid.toString())) return session.getId();
        return sid.toString();
    }

    public boolean isPost() {
        return this.isPost;
    }

    public void setIsPoset(boolean isPost) {
        this.isPost = isPost;
    }

    /**
     * @return the isAjaxRequest
     */
    public boolean isAjaxRequest() {
        return isAjaxRequest;
    }


    /**
     * @param isAjaxRequest the isAjaxRequest to set
     */
    public void setAjaxRequest(boolean isAjaxRequest) {
        this.isAjaxRequest = isAjaxRequest;
    }


    /**
     * @return the currentRequest
     */
    public HttpServletRequest getCurrentRequest() {
        return currentRequest;
    }


    /**
     * @param currentRequest the currentRequest to set
     */
    public void setCurrentRequest(HttpServletRequest currentRequest) {
        this.currentRequest = currentRequest;
    }


    /**
     * @return the currentResponse
     */
    public HttpServletResponse getCurrentResponse() {
        return currentResponse;
    }


    /**
     * @param currentResponse the currentResponse to set
     */
    public void setCurrentResponse(HttpServletResponse currentResponse) {
        this.currentResponse = currentResponse;
    }


    /**
     * @return the config
     */
    public AlbianHttpConfigurtion getConfig() {
        return config;
    }


    /**
     * @param config the config to set
     */
    public void setConfig(AlbianHttpConfigurtion config) {
        this.config = config;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the actionName
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * @param actionName the actionName to set
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * @return the templateName
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * @param templateName the templateName to set
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * @return the templatePath
     */
    public String getTemplatePath() {
        return templatePath;
    }

    /**
     * @param templatePath the templatePath to set
     */
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    /**
     * @return the paras
     */
    public Map<String, String> getParas() {
        return paras;
    }

    /**
     * @param paras the paras to set
     */
    public void setParas(Map<String, String> paras) {
        this.paras = paras;
    }


    public String getTemplateFullName() {
        return templateFullName;
    }


    public void setTemplateFullName(String templateFullName) {
        this.templateFullName = templateFullName;
    }

    public ViewConfigurtion getPageConfigurtion() {
        return this.pc;
    }

    public void setPageConfigurtion(ViewConfigurtion pc) {
        this.pc = pc;
    }

    public View getView() {
        return this.view;
    }

    public void setView(View v) {
        this.view = v;
    }

    public boolean isUseMasterView() {
        return this.isUseMasterView;
    }

    public void setUseMasterView(boolean isUseMasterView) {
        this.isUseMasterView = isUseMasterView;
    }

    public MView getMasterView() {
        return this.masterView;
    }

    public void setMasterView(MView mv) {
        this.masterView = mv;
    }

    public StringBuffer getMasterViewHtml() {
        return this.masterViewHtml;
    }

    public void setMasterViewHtml(StringBuffer html) {
        this.masterViewHtml = html;
    }

    public Map<String, UploadFile> getFileItems() {
        return this.fileItems;
    }

    public void setFileItems(Map<String, UploadFile> fileItems) {
        this.fileItems = fileItems;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getCurrentUrl() {
        return this.currentUrl;
    }

    public void setCurrentUrl(String url) {
        this.currentUrl = url;
    }
}
