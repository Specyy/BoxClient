package com.box.client.main;

public class BoxClientException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BoxClientException() {
	}

	public BoxClientException(String message) {
		super(message);
	}

	public BoxClientException(Throwable cause) {
		super(cause);
	}

	public BoxClientException(String message, Throwable cause) {
		super(message, cause);
	}

	protected BoxClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
