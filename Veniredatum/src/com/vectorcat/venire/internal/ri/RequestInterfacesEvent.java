package com.vectorcat.venire.internal.ri;

class RequestInterfacesEvent implements CommandEvent {
	private final int commandID;

	RequestInterfacesEvent(CommandIDGenerator idGenerator) {
		this.commandID = idGenerator.getNextCommandID();
	}

	@Override
	public int getCommandID() {
		return commandID;
	}
}