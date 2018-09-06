package org.albianj.mvc.config;

import java.lang.reflect.Field;

public class PageFieldConfigurtion {
	
	private String name;
	private String bindingName;
	private Field field;
	private Class<?> type;
	
	
	/**
	 * @return the type
	 */
	public Class< ? > getType( ) {
		return type;
	}

	
	/**
	 * @param type the type to set
	 */
	public void setType( Class< ? > type ) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName( ) {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName( String name ) {
		this.name = name;
	}
	
	/**
	 * @return the bindingName
	 */
	public String getBindingName( ) {
		return bindingName;
	}
	
	/**
	 * @param bindingName the bindingName to set
	 */
	public void setBindingName( String bindingName ) {
		this.bindingName = bindingName;
	}

	public Field getField( ) {
		return field;
	}

	public void setField( Field field ) {
		this.field = field;
	}
	

}
