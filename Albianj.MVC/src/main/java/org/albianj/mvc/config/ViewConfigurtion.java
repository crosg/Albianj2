package org.albianj.mvc.config;

import org.albianj.mvc.View;

import java.util.Map;

public class ViewConfigurtion {
	
	private Class<? extends View> cla = null;
	private Map<String,ViewFieldConfigurtion> fields = null;
	private Map<String,ViewActionConfigurtion> actions = null;
	private boolean isAutoBinding = true;
	private String template;
	private String fullClassName;
	private String name;
	
	/**
	 * @return the cla
	 */
	public Class< ? extends View> getRealClass( ) {
		return cla;
	}
	
	/**
	 * @param cla the cla to set
	 */
	public void setRealClass( Class< ? extends View> cla ) {
		this.cla = cla;
	}
	
	/**
	 * @return the fields
	 */
	public Map< String, ViewFieldConfigurtion> getFields( ) {
		return fields;
	}
	
	/**
	 * @param fields the fields to set
	 */
	public void setFields( Map< String, ViewFieldConfigurtion> fields ) {
		this.fields = fields;
	}
	
	/**
	 * @return the actions
	 */
	public Map< String, ViewActionConfigurtion> getActions( ) {
		return actions;
	}
	
	/**
	 * @param actions the actions to set
	 */
	public void setActions( Map< String, ViewActionConfigurtion> actions ) {
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

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}




}
