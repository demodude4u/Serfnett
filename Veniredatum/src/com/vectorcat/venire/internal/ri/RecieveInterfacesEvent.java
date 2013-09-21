package com.vectorcat.venire.internal.ri;

import java.util.ArrayList;
import java.util.List;

class RecieveInterfacesEvent implements
		CommandResponseEvent<RequestInterfacesEvent> {
	private int commandID;
	private List<Class<?>> classes;

	RecieveInterfacesEvent() {
	}

	RecieveInterfacesEvent(RequestInterfacesEvent event, List<Class<?>> classes) {
		this.commandID = event.getCommandID();
		this.classes = new ArrayList<>(classes);
	}

	List<Class<?>> getClasses() {
		return classes;
	}

	@Override
	public int getResponseCommandID() {
		return commandID;
	}
}