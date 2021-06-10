
package org.ejprd.exception;

public class CyclicReferencesException extends Exception {
	private static final long serialVersionUID = -6435368195464164833L;

	public CyclicReferencesException() {
		super();
	}

	public CyclicReferencesException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CyclicReferencesException(String message, Throwable cause) {
		super(message, cause);
	}

	public CyclicReferencesException(String message) {
		super(message);
	}

	public CyclicReferencesException(Throwable cause) {
		super(cause);
	}

}
