package com.vectorcat.venire;

import java.util.List;

import com.vectorcat.venire.api.EventBus;
import com.vectorcat.venire.internal.ri.CallDelegator;
import com.vectorcat.venire.internal.ri.EventCommander;
import com.vectorcat.venire.internal.ri.EventResponder;
import com.vectorcat.venire.internal.ri.InterfaceProxyGenerator;
import com.vectorcat.venire.internal.ri.MethodRegistry;

public class RemoteInterfacer {

	private final InterfaceRegistry interfaceRegistry;
	private final InterfaceProxyGenerator proxyGenerator;

	@SuppressWarnings("unused")
	private final EventResponder eventResponder;

	public RemoteInterfacer(EventBus bus, InterfaceRegistry interfaceRegistry) {
		// Guice would be nice here
		this.interfaceRegistry = interfaceRegistry;

		EventCommander eventCommander = new EventCommander(bus);
		MethodRegistry methodRegistry = new MethodRegistry();
		CallDelegator callDelegator = new CallDelegator(eventCommander,
				interfaceRegistry, methodRegistry);
		proxyGenerator = new InterfaceProxyGenerator(callDelegator,
				interfaceRegistry);

		// This is odd, because my real reference is from the event bus
		eventResponder = new EventResponder(bus, callDelegator,
				interfaceRegistry);
	}

	RemoteInterfacer(InterfaceRegistry registry,
			InterfaceProxyGenerator proxyGenerator,
			EventResponder eventResponder) {
		this.interfaceRegistry = registry;
		this.proxyGenerator = proxyGenerator;
		this.eventResponder = eventResponder;
	}

	public <T> T createRemoteInterface(Class<T> clazz) {
		return proxyGenerator.createProxy(clazz);
	}

	public <T> T createRemoteInterface(int interfaceIndex) {
		return proxyGenerator.createProxy(interfaceIndex);
	}

	public List<Class<?>> getRegisteredInterfaces() {
		return interfaceRegistry.getInterfaces();
	}

}
