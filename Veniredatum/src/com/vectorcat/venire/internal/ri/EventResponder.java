package com.vectorcat.venire.internal.ri;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.vectorcat.venire.InterfaceRegistry;
import com.vectorcat.venire.spi.EventBus;

public class EventResponder {

	private class Reciever {
		@Subscribe
		public void onCallEvent(CallEvent event) {
			try {
				Object ret = callDelegator.remoteCall(event.getInterfaceID(),
						event.getFunctionID(), event.getParameters());
				try {
					bus.post(new ReturnEvent(event, ret));
				} catch (InterruptedException e) {
					throw new Error(e);
				}
			} catch (InvocationTargetException e) {
				try {
					bus.post(new ReturnEvent(event, e.getTargetException()));
				} catch (InterruptedException e2) {
					throw new Error(e2);
				}
			}
		}

		@Subscribe
		public void onRequestInterfacesEvent(RequestInterfacesEvent event) {
			List<Class<?>> classes = interfaceRegistry.getInterfaces();

			try {
				bus.post(new RecieveInterfacesEvent(event, classes));
			} catch (InterruptedException e) {
				throw new Error(e);
			}
		}
	}

	private final EventBus bus;
	private final CallDelegator callDelegator;
	private final InterfaceRegistry<?> interfaceRegistry;

	public EventResponder(EventBus bus, CallDelegator callDelegator,
			InterfaceRegistry<?> interfaceRegistry) {
		this.bus = bus;
		this.callDelegator = callDelegator;
		this.interfaceRegistry = interfaceRegistry;

		bus.register(new Reciever());
	}

}
