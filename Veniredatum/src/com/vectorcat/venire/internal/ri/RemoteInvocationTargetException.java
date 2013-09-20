package com.vectorcat.venire.internal.ri;

public class RemoteInvocationTargetException extends RuntimeException {
	private static final long serialVersionUID = 5177669302551260379L;

	private final Throwable throwable;

	public RemoteInvocationTargetException(Throwable throwable) {
		this.throwable = throwable;
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
