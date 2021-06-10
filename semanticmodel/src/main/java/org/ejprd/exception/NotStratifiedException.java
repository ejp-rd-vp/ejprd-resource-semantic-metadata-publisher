package org.ejprd.exception;


public class NotStratifiedException extends Exception {
	private static final long serialVersionUID = 6795087867824896108L;

	public NotStratifiedException() {
	}

	public NotStratifiedException(String message) {
		super(message);
	}

	public NotStratifiedException(Throwable cause) {
		super(cause);
	}

	public NotStratifiedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotStratifiedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
