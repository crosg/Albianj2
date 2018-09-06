package org.albianj.mvc.config;

import org.albianj.mvc.Page;

import java.util.Map;

public class PageConfigurtion {
	
	private Class<? extends  Page> cla = null;
	private Map<String,PageFieldConfigurtion> fields = null;
	private Map<String,PageActionConfigurtion> actions = null;
	private boolean isAutoBinding = true;
	private String template;
	private String fullClassName;
	
	/**
	 * @return the cla
	 */
	public Class< ? extends Page> getRealClass( ) {
		return cla;
	}
	
	/**
	 * @param cla the cla to set
	 */
	public void setRealClass( Class< ? extends Page> cla ) {
		this.cla = cla;
	}
	
	/**
	 * @return the fields
	 */
	public Map< String, PageFieldConfigurtion > getFields( ) {
		return fields;
	}
	
	/**
	 * @param fields the fields to set
	 */
	public void setFields( Map< String, PageFieldConfigurtion > fields ) {
		this.fields = fields;
	}
	
	/**
	 * @return the actions
	 */
	public Map< String, PageActionConfigurtion > getActions( ) {
		return actions;
	}
	
	/**
	 * @param actions the actions to set
	 */
	public void setActions( Map< String, PageActionConfigurtion > actions ) {
		this.actions = actions;
	}
	
	public void setAutoBinding(boolean isAutoBinding){
		this.isAutoBinding  = isAutoBinding;
	}

	public boolean isAutoBinding(){
		return this.isAutoBinding;
	}

	public String getTemplate(){
		return this.template;
	}

	public void setTemplate(String template){
		this.template = template;
	}

	public String getFullClassName(){
		return this.fullClassName;
	}

	public void setFullClassName(String fullClassName){
		this.fullClassName = fullClassName;
	}



}
