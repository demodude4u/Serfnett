package com.vectorcat.venire.internal.ri;

class RequestInterfacesEvent implements CommandEvent {
	private int commandID;

	RequestInterfacesEvent() {
	}

	RequestInterfacesEvent(CommandIDGenerator idGenerator) {
		this.commandID = idGenerator.getNextCommandID();
	}

	@Override
	public int getCommandID() {
		return commandID;
	}
}