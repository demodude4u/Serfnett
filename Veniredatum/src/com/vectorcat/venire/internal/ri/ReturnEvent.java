package com.vectorcat.venire.internal.ri;

class ReturnEvent implements CommandResponseEvent<CallEvent>, CallIdentity {
	private int commandID;
	private int interfaceID;
	private int functionID;
	private Object ret;
	private Throwable throwable;

	ReturnEvent() {
	}

	ReturnEvent(CallEvent event, Object ret) {
		this.commandID = event.getCommandID();
		this.interfaceID = event.getInterfaceID();
		this.functionID = event.getFunctionID();
		this.ret = ret;
		this.throwable = null;
	}

	ReturnEvent(CallEvent event, Throwable throwable) {
		this.commandID = event.getCommandID();
		this.interfaceID = event.getInterfaceID();
		this.functionID = event.getFunctionID();
		this.ret = null;
		this.throwable = throwable;
	}

	@Override
	public int getFunctionID() {
		return functionID;
	}

	@Override
	public int getInterfaceID() {
		return interfaceID;
	}

	@Override
	public int getResponseCommandID() {
		return commandID;
	}

	Object getRet() {
		return ret;
	}

	Throwable getThrowable() {
		return throwable;
	}

	boolean isThrowableCaught() {
		return throwable != null;
	}
}