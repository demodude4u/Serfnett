package com.vectorcat.venire.internal.ri;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InterfaceProxyGenerator {

	private final CallDelegator callDelegator;
	private final RemoteInterfaceRegistry remoteInterfaceRegistry;

	public InterfaceProxyGenerator(CallDelegator callDelegator,
			RemoteInterfaceRegistry remoteInterfaceRegistry) {
		this.callDelegator = callDelegator;
		this.remoteInterfaceRegistry = remoteInterfaceRegistry;
	}

	private InvocationHandler createInvocationHandler(final int interfaceID) {
		return new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				try {
					return callDelegator.call(interfaceID, method, args);
					// XXX It's not apparent that this catch is necessary
					// because it is a RuntimeException.
				} catch (RemoteInvocationTargetException e) {

					throw e.getThrowable();
				}
			}
		};
	}

	@SuppressWarnings("unchecked")
	public <T> T createProxy(Class<?> interfaceClass) {
		int interfaceID = remoteInterfaceRegistry.getID(interfaceClass);
		InvocationHandler handler = createInvocationHandler(interfaceID);

		Object proxyInstance = Proxy.newProxyInstance(getProxyClassLoader(),
				new Class[] { interfaceClass }, handler);
		return (T) proxyInstance;
	}

	@SuppressWarnings("unchecked")
	public <T> T createProxy(int interfaceID) {
		InvocationHandler handler = createInvocationHandler(interfaceID);
		Class<?> interfaceClass = remoteInterfaceRegistry
				.getInterface(interfaceID);

		Object proxyInstance = Proxy.newProxyInstance(getProxyClassLoader(),
				new Class[] { interfaceClass }, handler);
		return (T) proxyInstance;
	}

	private ClassLoader getProxyClassLoader() {
		return getClass().getClassLoader();
	}

}
