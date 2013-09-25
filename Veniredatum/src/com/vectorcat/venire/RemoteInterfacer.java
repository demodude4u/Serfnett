package com.vectorcat.venire;

import java.util.List;

import com.vectorcat.venire.api.EventBus;
import com.vectorcat.venire.internal.ri.CallDelegator;
import com.vectorcat.venire.internal.ri.EventCommander;
import com.vectorcat.venire.internal.ri.EventResponder;
import com.vectorcat.venire.internal.ri.InterfaceProxyGenerator;
import com.vectorcat.venire.internal.ri.MethodRegistry;
import com.vectorcat.venire.internal.ri.RemoteInterfaceRegistry;

public class RemoteInterfacer {

	private final InterfaceProxyGenerator proxyGenerator;
	@SuppressWarnings("unused")
	private final EventResponder eventResponder;
	private final RemoteInterfaceRegistry remoteInterfaceRegistry;

	public RemoteInterfacer(EventBus bus) {
		this(bus, InterfaceRegistry.builder().build());
	}

	public RemoteInterfacer(EventBus bus, InterfaceRegistry<?> interfaceRegistry) {
		// Guice would be nice here

		EventCommander eventCommander = new EventCommander(bus);

		remoteInterfaceRegistry = new RemoteInterfaceRegistry(eventCommander);

		MethodRegistry methodRegistry = new MethodRegistry();
		CallDelegator callDelegator = new CallDelegator(eventCommander,
				interfaceRegistry, remoteInterfaceRegistry, methodRegistry);

		proxyGenerator = new InterfaceProxyGenerator(callDelegator,
				remoteInterfaceRegistry);

		// XXX This is odd, because my real reference is from the event bus
		eventResponder = new EventResponder(bus, callDelegator,
				interfaceRegistry);
	}

	RemoteInterfacer(InterfaceProxyGenerator proxyGenerator,
			EventResponder eventResponder,
			RemoteInterfaceRegistry remoteInterfaceRegistry) {
		this.proxyGenerator = proxyGenerator;
		this.eventResponder = eventResponder;
		this.remoteInterfaceRegistry = remoteInterfaceRegistry;
	}

	public <T> T createRemoteInterface(Class<T> clazz) {
		return proxyGenerator.createProxy(clazz);
	}

	public <T> T createRemoteInterface(int interfaceIndex) {
		return proxyGenerator.createProxy(interfaceIndex);
	}

	public List<Class<?>> getRemoteInterfaces() {
		return remoteInterfaceRegistry.getInterfaces();
	}
}
