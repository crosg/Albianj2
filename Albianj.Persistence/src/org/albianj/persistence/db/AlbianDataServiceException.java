package org.albianj.persistence.db;

/**
 * albianj存储层异常
 * @author seapeak
 *
 */
public class AlbianDataServiceException extends Exception {
	private static final long serialVersionUID = 671904625520462583L;
	public AlbianDataServiceException() {
		super();
	}

	/**
	 * @param msg
	 */
	public AlbianDataServiceException(String msg) {
		super(msg);
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public AlbianDataServiceException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * @param cause
	 */
	public AlbianDataServiceException(Throwable cause) {
		super(cause);
	}
}
