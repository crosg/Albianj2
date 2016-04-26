package org.albianj.persistence.service;

public class MappingAttributeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6883346258879546366L;

	/**
	 * 
	 */

	public MappingAttributeException() {
		super();
	}

	/**
	 * @param msg
	 */
	public MappingAttributeException(String msg) {
		super(msg);
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public MappingAttributeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * @param cause
	 */
	public MappingAttributeException(Throwable cause) {
		super(cause);
	}
}
