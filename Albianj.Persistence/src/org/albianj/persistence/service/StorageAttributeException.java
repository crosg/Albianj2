package org.albianj.persistence.service;

public class StorageAttributeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -826864515616216400L;

	public StorageAttributeException() {
		super();
	}

	/**
	 * @param msg
	 */
	public StorageAttributeException(String msg) {
		super(msg);
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public StorageAttributeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * @param cause
	 */
	public StorageAttributeException(Throwable cause) {
		super(cause);
	}
}
