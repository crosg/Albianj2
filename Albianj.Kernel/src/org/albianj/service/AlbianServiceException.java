package org.albianj.service;

/**
 * @author Seapeak.Xu
 * 
 */
public class AlbianServiceException extends RuntimeException {

	/**
	 * 
	 */
	/**
	 * 
	 */
	private static final long serialVersionUID = 8105050180239691024L;

	/**
	 * 
	 */
	public AlbianServiceException() {
		super();
	}

	/**
	 * @param msg
	 */
	public AlbianServiceException(String msg) {
		super(msg);
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public AlbianServiceException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * @param cause
	 */
	public AlbianServiceException(Throwable cause) {
		super(cause);
	}

}
