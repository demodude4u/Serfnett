package com.vectorcat.venire.internal.ri;

interface CommandResponseEvent<E extends CommandEvent> {

	int getResponseCommandID();

}