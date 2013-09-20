package com.vectorcat.venire.internal.ri;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.vectorcat.venire.InterfaceRegistry;

public class CallDelegator {

	private final EventCommander eventCommander;
	private final InterfaceRegistry interfaceRegistry;
	private final MethodRegistry methodRegistry;

	public CallDelegator(EventCommander eventCommander,
			InterfaceRegistry interfaceRegistry, MethodRegistry methodRegistry) {
		this.eventCommander = eventCommander;
		this.interfaceRegistry = interfaceRegistry;
		this.methodRegistry = methodRegistry;
	}

	Object call(int interfaceID, Method method, Object[] parameters)
			throws RemoteInvocationTargetException {
		Class<?> interfaceClass = interfaceRegistry.getInterface(interfaceID);
		int functionID = methodRegistry.getID(interfaceClass, method);
		Future<Object> future = eventCommander.commandCall(interfaceID,
				functionID, parameters);

		Object ret;
		try {
			ret = future.get();
		} catch (InterruptedException e) {
			throw new Error(e);
		} catch (ExecutionException e) {
			if (e.getCause() instanceof RemoteInvocationTargetException) {
				throw (RemoteInvocationTargetException) e.getCause();
			} else {
				throw new Error(e);
			}
		}

		return ret;
	}

	Object remoteCall(int interfaceID, int functionID, Object[] parameters)
			throws InvocationTargetException {

		Class<?> interfaceClass = interfaceRegistry.getInterface(interfaceID);
		Object instance = interfaceRegistry.getInstance(interfaceID);
		Method method = methodRegistry.getMethod(interfaceClass, functionID);

		Object ret;
		try {
			ret = method.invoke(instance, parameters);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new Error(e);
		}

		return ret;
	}
}
