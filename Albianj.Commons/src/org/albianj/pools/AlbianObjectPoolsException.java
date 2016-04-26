package org.albianj.pools;

public class AlbianObjectPoolsException extends Exception {
	private static final long serialVersionUID = 671904625520462583L;
	public AlbianObjectPoolsException() {
		super();
	}

	/**
	 * @param msg
	 */
	public AlbianObjectPoolsException(String msg) {
		super(msg);
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public AlbianObjectPoolsException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * @param cause
	 */
	public AlbianObjectPoolsException(Throwable cause) {
		super(cause);
	}
	

}
