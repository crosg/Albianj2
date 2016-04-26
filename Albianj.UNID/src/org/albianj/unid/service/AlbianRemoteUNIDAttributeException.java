package org.albianj.unid.service;

public class AlbianRemoteUNIDAttributeException extends Exception {
	private static final long serialVersionUID = -826864515616216400L;

	public AlbianRemoteUNIDAttributeException() {
		super();
	}

	/**
	 * @param msg
	 */
	public AlbianRemoteUNIDAttributeException(String msg) {
		super(msg);
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public AlbianRemoteUNIDAttributeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * @param cause
	 */
	public AlbianRemoteUNIDAttributeException(Throwable cause) {
		super(cause);
	}

}
