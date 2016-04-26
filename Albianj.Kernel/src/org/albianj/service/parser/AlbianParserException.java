package org.albianj.service.parser;

public class AlbianParserException extends Exception {
	private static final long serialVersionUID = 671904625520462583L;
	public AlbianParserException() {
		super();
	}

	/**
	 * @param msg
	 */
	public AlbianParserException(String msg) {
		super(msg);
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public AlbianParserException(String msg, Throwable cause) {
		super(msg, cause);
	}

	/**
	 * @param cause
	 */
	public AlbianParserException(Throwable cause) {
		super(cause);
	}

}
