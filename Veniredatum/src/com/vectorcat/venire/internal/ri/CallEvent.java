package com.vectorcat.venire.internal.ri;

class CallEvent implements CommandEvent, CallIdentity {
	private final int commandID;
	private final int interfaceID;
	private final int functionID;
	private final Object[] parameters;

	CallEvent(CommandIDGenerator idGenerator, int classID, int functionID,
			Object[] parameters) {
		this.commandID = idGenerator.getNextCommandID();
		this.interfaceID = classID;
		this.functionID = functionID;
		this.parameters = parameters;
	}

	@Override
	public int getCommandID() {
		return commandID;
	}

	@Override
	public int getFunctionID() {
		return functionID;
	}

	@Override
	public int getInterfaceID() {
		return interfaceID;
	}

	Object[] getParameters() {
		return parameters;
	}

}