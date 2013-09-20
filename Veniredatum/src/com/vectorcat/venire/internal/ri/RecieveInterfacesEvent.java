package com.vectorcat.venire.internal.ri;

import java.util.List;

class RecieveInterfacesEvent implements
		CommandResponseEvent<RequestInterfacesEvent> {
	private final int commandID;
	private final List<Class<?>> classes;

	RecieveInterfacesEvent(RequestInterfacesEvent event, List<Class<?>> classes) {
		this.commandID = event.getCommandID();
		this.classes = classes;
	}

	List<Class<?>> getClasses() {
		return classes;
	}

	@Override
	public int getResponseCommandID() {
		return commandID;
	}
}